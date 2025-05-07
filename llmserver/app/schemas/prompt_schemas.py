from pydantic import BaseModel, Field
from typing import List

class is_adversarial(BaseModel):
    adversarial_query: bool

class correct_query(BaseModel):
    corrected_query: str

class infer_movie(BaseModel):
    inferred_movie: str

class generate_cypher(BaseModel):
    generated_cypher: str