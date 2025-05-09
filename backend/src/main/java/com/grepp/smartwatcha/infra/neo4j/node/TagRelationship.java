package com.grepp.smartwatcha.infra.neo4j.node;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Getter
@Setter @ToString
@RequiredArgsConstructor
public class TagRelationship {

    @Id @GeneratedValue
    private Long id;

    private Long count = 1L;

    @TargetNode
    private final TagNode tagNode;

    public void incrementCount() {
        this.count++;
    }

}
