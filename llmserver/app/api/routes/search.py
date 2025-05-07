from fastapi import Depends, Request, HTTPException, APIRouter
from fastapi.responses import JSONResponse
from core.utils import verify_internal_request, get_neo4j_connector, get_smart_graph
from db.neo4j_driver import Neo4jConnector
from langgraph.graph.state import CompiledStateGraph
import os


router = APIRouter()

@router.post("/llm/search")
async def search(
    request: Request,
    db: Neo4jConnector = Depends(get_neo4j_connector),
    graph: CompiledStateGraph = Depends(get_smart_graph),
    _: None = Depends(verify_internal_request),
):

    body = await request.json()
    query = body.get("query")
    intent = body.get("intent")

    if not query:
        raise HTTPException(status_code=400, detail="No query provided.")
    
    if not intent:
        raise HTTPException(status_code=400, detail="No intent provided.")

    try:
        result = graph.invoke({"query": query, "intent": intent})
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

    if result.get("adversarial_query"):
        raise HTTPException(status_code=400, detail="Adversarial query detected.")

    cypher = result.get("generated_cypher")
    response = db.run_query(cypher)

    return JSONResponse(content={"movie_ids": response})