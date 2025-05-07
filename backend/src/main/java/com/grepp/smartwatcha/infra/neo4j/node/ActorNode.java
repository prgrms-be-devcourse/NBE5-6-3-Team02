package com.grepp.smartwatcha.infra.neo4j.node;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("ACTOR")
@RequiredArgsConstructor
public class ActorNode {

    @Id
    private final String name;

}
