from neo4j import GraphDatabase
import os


class Neo4jConnector:
    def __new__(cls, *args, **kwargs):
        if not hasattr(cls, "_instance"):
            cls._instance = super().__new__(cls)
        return cls._instance

    def __init__(self, uri, user, password):
        cls = type(self)
        if not hasattr(cls, "_init"):
            self.driver = GraphDatabase.driver(uri, auth=(user, password))
            cls._init = True

    def close(self):
        self.driver.close()

    def run_query(self, query: str, parameters: dict = None):
        with self.driver.session() as session:
            result = session.run(query, parameters)
            return [record.data() for record in result]