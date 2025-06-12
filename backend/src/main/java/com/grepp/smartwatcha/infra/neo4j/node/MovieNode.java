package com.grepp.smartwatcha.infra.neo4j.node;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

@Node("MOVIE")
@Getter @Setter @ToString
@RequiredArgsConstructor
public class MovieNode {

    @Id
    private final Long id;
    private final String title;

    @Relationship(type="ACTED_IN", direction = Direction.OUTGOING)
    private List<ActorNode> actors = new ArrayList<>();

    @Relationship(type="DIRECTED_BY", direction = Direction.OUTGOING)
    private List<DirectorNode> directors = new ArrayList<>();

    @Relationship(type="WRITTEN_BY", direction = Direction.OUTGOING)
    private List<WriterNode> writers = new ArrayList<>();

    @Relationship(type="HAS_GENRE", direction = Direction.OUTGOING)
    private List<GenreNode> genres = new ArrayList<>();

    @Relationship(type="HAS_TAG", direction = Direction.OUTGOING)
    private List<TagRelationship> tags = new ArrayList<>();

}
