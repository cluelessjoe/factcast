package org.factcast.server.rest.resources;

import com.mercateo.common.rest.schemagen.link.relation.RelType;
import com.mercateo.common.rest.schemagen.link.relation.Relation;
import com.mercateo.common.rest.schemagen.link.relation.RelationContainer;

public enum FactsRel implements RelationContainer {
    FACTS {
        @Override
        public Relation getRelation() {
            return Relation.of("facts", RelType.OTHER);
        }
    },
    CREATE_TRANSACTIONAL {
        @Override
        public Relation getRelation() {
            return Relation.of("create-transactional", RelType.OTHER);
        }
    },
}
