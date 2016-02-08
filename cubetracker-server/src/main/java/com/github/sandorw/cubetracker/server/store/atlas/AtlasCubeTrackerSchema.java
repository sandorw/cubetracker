package com.github.sandorw.cubetracker.server.store.atlas;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.github.sandorw.cubetracker.server.cards.CardUsageDataPersister;
import com.palantir.atlasdb.schema.AtlasSchema;
import com.palantir.atlasdb.schema.Namespace;
import com.palantir.atlasdb.table.description.Schema;
import com.palantir.atlasdb.table.description.TableDefinition;
import com.palantir.atlasdb.table.description.ValueType;
import java.io.File;
import java.io.IOException;

/**
 * Atlas schema specification for the CubeTracker data store.
 */
public final class AtlasCubeTrackerSchema implements AtlasSchema {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new GuavaModule());
    public static final Namespace NAMESPACE = Namespace.create("cubeTracker");
    public static final Schema SCHEMA = generateSchema();

    private AtlasCubeTrackerSchema() {}

    @Override
    public Namespace getNamespace() {
        return NAMESPACE;
    }

    @Override
    public Schema getLatestSchema() {
        return SCHEMA;
    }

    private static Schema generateSchema() {
        Schema schema = new Schema(
                "CubeTrackerStore",
                AtlasCubeTrackerSchema.class.getPackage().getName() + ".generated",
                NAMESPACE);

        schema.addTableDefinition("cube_cards", new TableDefinition() {
            {
                rowName();
                rowComponent("card_name", ValueType.VAR_STRING);

                columns();
                column("card_usage", "c", CardUsageDataPersister.class);
            }
        });

        return schema;
    }

    public static void main(String[] args) throws IOException {
        checkArgument(args.length == 1, "Pass a single argument containing the location to output generated code.");
        SCHEMA.renderTables(new File(args[0]));
    }

}
