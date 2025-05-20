package com.grepp.smartwatcha.infra.neo4j.node;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("WRITER")
@RequiredArgsConstructor
@Getter @Setter @ToString
public class WriterNode {

    @Id
    private final String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WriterNode)) return false;
        WriterNode that = (WriterNode) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
