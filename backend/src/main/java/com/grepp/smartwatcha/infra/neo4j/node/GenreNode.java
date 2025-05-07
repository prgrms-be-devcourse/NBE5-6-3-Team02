package com.grepp.smartwatcha.infra.neo4j.node;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("GENRE")
@Getter @Setter @ToString
@RequiredArgsConstructor
public class GenreNode {

    @Id
    private final String name;
}
