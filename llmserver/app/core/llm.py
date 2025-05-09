from langgraph.graph import StateGraph
from typing import TypedDict
from typing import List
from schemas.prompt_schemas import *
import spacy
from langchain_core.runnables import RunnableLambda
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import JsonOutputParser
from langchain.output_parsers import OutputFixingParser
from langchain_openai import ChatOpenAI
from langchain_core.runnables import RunnablePassthrough
import yaml
import re

class SmartSearchGraph(object):

    class State(TypedDict):
        query: str
        intent: str
        adversarial_query: bool
        corrected_query: str
        keywords: List[str]
        inferred_movie: str
        generated_cypher: str

    def __new__(cls, *args, **kwargs):
        if not hasattr(cls, "_instance"):
            cls._instance = super().__new__(cls)
        return cls._instance
    
    def __init__(self, prompt_path):
        cls = type(self)
        if not hasattr(cls, "_init"):
            self.prompt_path = prompt_path
            self.nlp = spacy.load("en_core_web_sm")
            cls._instance._build_graph()
            cls._init = True
    
    def _build_graph(self):
        builder = StateGraph(self.State)

        adversarial_attack_filter_chain = build_chain(
            self.prompt_path,
            "is_adversarial",
            is_adversarial
        )

        correct_query_chain = build_chain(
            self.prompt_path,
            "correct_query",
            correct_query
        )

        infer_movie_chain = build_chain(
            self.prompt_path,
            "infer_movie",
            infer_movie
        )

        generate_cypher_chain = build_chain(
            self.prompt_path,
            "generate_cypher",
            generate_cypher
        )

        def branch_on_adversarial_filter(state):
            return "adversarial_flagged" if state["adversarial_query"] else "adversarial_normal"

        def adversarial_flagged_handler(state):
            return
        
        def keywords_handler(state):
            query = state["corrected_query"]
            kewords = generate_keywords(self.nlp, query)
            return {"keywords": kewords}
        
        def branch_on_intent(state):
            return "search" if state["intent"] == "search" else "recommend"
        
        def generate_movie_search_cypher_handler(state):
            movie = state["inferred_movie"]
            cypher = f'MATCH (m:MOVIE) WHERE toLower(m.title) = toLower("{movie}") RETURN m.id AS movie'

            return {"generated_cypher" : cypher}
        
        def branch_on_safe_cypher(state):
            return "banned_cypher" if not is_safe_cypher(state["generated_cypher"]) else "safe_cypher"

        builder.add_node("adversarial_filter", adversarial_attack_filter_chain)
        builder.add_node("adversarial_flagged_handler", RunnableLambda(adversarial_flagged_handler))
        builder.add_node("correct_query", correct_query_chain)
        builder.add_node("generate_keywords", RunnableLambda(keywords_handler))
        builder.add_node("infer_movie", infer_movie_chain)
        builder.add_node("generate_movie_search", RunnableLambda(generate_movie_search_cypher_handler))
        builder.add_node("generate_recommend_cypher", generate_cypher_chain)
        builder.add_node("end", RunnableLambda(lambda state: state))

        builder.add_conditional_edges(
            "adversarial_filter",
            branch_on_adversarial_filter,
            {
                "adversarial_flagged": "adversarial_flagged_handler",
                "adversarial_normal": "correct_query"
            }
            )
        builder.add_conditional_edges(
            "generate_keywords",
            branch_on_intent,
            {
                "search": "infer_movie",
                "recommend": "generate_recommend_cypher"
            }
        )
        builder.add_conditional_edges(
            "generate_recommend_cypher",
            branch_on_safe_cypher,
            {
                "banned_cypher": "generate_recommend_cypher",
                "safe_cypher": "end"
            }
        )

        builder.add_edge("correct_query", "generate_keywords")
        builder.add_edge("infer_movie", "generate_movie_search")

        builder.set_entry_point("adversarial_filter")
        builder.set_finish_point("adversarial_flagged_handler")
        builder.set_finish_point("generate_movie_search")
        builder.set_finish_point("end")

        self.graph = builder.compile()
    
    def get_graph(self):
        return self.graph
    
def build_chain(prompt_path, prompt_name, format_type):
    with open(prompt_path, 'r', encoding='utf-8') as f:
        prompt_data = yaml.safe_load(f)
        prompt_data = prompt_data[prompt_name]
    
    model_configure = prompt_data['configure']
    template = prompt_data['template']
    input_variables = prompt_data['input_variables']
    
    llm = ChatOpenAI(model = model_configure['model'],
                    temperature = model_configure['temperature'],
                    max_tokens=model_configure["maximum_length"],
                    top_p=model_configure['top_p'])
    
    prompt = ChatPromptTemplate.from_messages(
        [(message['role'], message['content']) for message in template]
    )
    
    parser = JsonOutputParser(pydantic_object = format_type)
    
    prompt = prompt.partial(format_instructions = parser.get_format_instructions())

    parser = OutputFixingParser.from_llm(parser=parser, llm=llm)

    chain_map = {}
    
    for var in input_variables:
        if var not in chain_map:
            chain_map[var] = RunnablePassthrough()
    
    chain = (
        chain_map
        | prompt
        | llm
        | parser
    )
    
    return chain

def generate_keywords(nlp, sentence):
    similarity_triggers = [
        "like", "similar", "such as", "as", "just like", "same as",
        "recommend", "suggest", "remind", "kind of like",
        "in the style of", "vibe", "tone", "style", "mood", "theme", "genre"
    ]

    doc = nlp(sentence)

    keywords = [token.lemma_ for token in doc if token.pos_ in ['NOUN', 'ADJ', 'VERB', 'NUM', 'PROPN'] or token.text.lower() in similarity_triggers]

    return keywords

def is_safe_cypher(query: str) -> bool:
    q = query.lower()
    
    banned_patterns = [
        r"match\s*\(\s*[a-z]\s*\)",              # match (n)
        r"->",                                    # 방향 탐색 (정방향)
        r"<-",                                    # 방향 탐색 (역방향)
        r"\bdelete\b",                            # delete
        r"\bset\b",                               # set
        r"\bdrop\b",                              # drop
        r"\bcreate\s+user\b",                     # create user
        r"\bdrop\s+user\b",                       # drop user
        r"\bgrant\b",                             # grant
        r"\brevoke\b",                            # revoke
        r"\bmerge\b"                              # merge
    ]
    
    for pattern in banned_patterns:
        if re.search(pattern, q):
            return False
    
    return True