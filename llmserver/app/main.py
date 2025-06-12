from fastapi import APIRouter
from fastapi import FastAPI
from fastapi.openapi.docs import get_swagger_ui_html
from fastapi.openapi.models import Info

from api.routes import search

from dotenv import load_dotenv

from uvicorn import run

app = FastAPI(openapi_url="/api/docs/openapi.json")

@app.get("/api/docs", include_in_schema=False)
async def get_documentation():
    return get_swagger_ui_html(
        openapi_url=app.openapi_url,
        title=app.title + " - Swagger UI"
    )

app.include_router(search.router, tags=["search"])

if __name__ == "__main__":
    run(app, host='0.0.0.0', port=8080)