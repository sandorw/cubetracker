package com.github.sandorw.cubetracker.server.store.atlas.generated;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.palantir.atlasdb.schema.Namespace;
import com.palantir.atlasdb.table.generation.Triggers;
import com.palantir.atlasdb.transaction.api.Transaction;

public class CubeTrackerStoreTableFactory {
    private final static Namespace defaultNamespace = Namespace.create("cubeTracker", Namespace.UNCHECKED_NAME);
    private final List<Function<? super Transaction, SharedTriggers>> sharedTriggers;
    private final Namespace namespace;

    public static CubeTrackerStoreTableFactory of(List<Function<? super Transaction, SharedTriggers>> sharedTriggers, Namespace namespace) {
        return new CubeTrackerStoreTableFactory(sharedTriggers, namespace);
    }

    public static CubeTrackerStoreTableFactory of(List<Function<? super Transaction, SharedTriggers>> sharedTriggers) {
        return new CubeTrackerStoreTableFactory(sharedTriggers, defaultNamespace);
    }

    private CubeTrackerStoreTableFactory(List<Function<? super Transaction, SharedTriggers>> sharedTriggers, Namespace namespace) {
        this.sharedTriggers = sharedTriggers;
        this.namespace = namespace;
    }

    public static CubeTrackerStoreTableFactory of(Namespace namespace) {
        return of(ImmutableList.<Function<? super Transaction, SharedTriggers>>of(), namespace);
    }

    public static CubeTrackerStoreTableFactory of() {
        return of(ImmutableList.<Function<? super Transaction, SharedTriggers>>of(), defaultNamespace);
    }

    public CubeCardsTable getCubeCardsTable(Transaction t, CubeCardsTable.CubeCardsTrigger... triggers) {
        return CubeCardsTable.of(t, namespace, Triggers.getAllTriggers(t, sharedTriggers, triggers));
    }

    public interface SharedTriggers extends
            CubeCardsTable.CubeCardsTrigger {
        /* empty */
    }

    public abstract static class NullSharedTriggers implements SharedTriggers {
        @Override
        public void putCubeCards(Multimap<CubeCardsTable.CubeCardsRow, ? extends CubeCardsTable.CubeCardsNamedColumnValue<?>> newRows) {
            // do nothing
        }
    }
}