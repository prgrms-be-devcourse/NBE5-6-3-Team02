from fastapi import Request, HTTPException, Depends
import os
from .llm import SmartSearchGraph
from langgraph.graph.state import CompiledStateGraph
from db.neo4j_driver import Neo4jConnector

def verify_internal_request(request: Request):
    token = request.headers.get("X-Internal-Token")
    if token != os.environ.get("SECRET_TOKEN"):
        raise HTTPException(status_code=403, detail="Forbidden")

def get_smart_graph() -> CompiledStateGraph:
    return SmartSearchGraph(os.environ.get("PROMPT_PATH")).get_graph()

def get_neo4j_connector() -> Neo4jConnector:
    return Neo4jConnector(
        os.environ.get("NEO4J_URI"),
        os.environ.get("NEO4J_USERNAME"),
        os.environ.get("NEO4J_PASSWORD")
    )