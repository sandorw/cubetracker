package com.github.sandorw.cubetracker.server.store.atlas.generated;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;



import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedBytes;
import com.google.protobuf.InvalidProtocolBufferException;
import com.palantir.atlasdb.compress.CompressionUtils;
import com.palantir.atlasdb.encoding.PtBytes;
import com.palantir.atlasdb.keyvalue.api.Cell;
import com.palantir.atlasdb.keyvalue.api.ColumnSelection;
import com.palantir.atlasdb.keyvalue.api.Prefix;
import com.palantir.atlasdb.keyvalue.api.RangeRequest;
import com.palantir.atlasdb.keyvalue.api.RowResult;
import com.palantir.atlasdb.keyvalue.impl.Cells;
import com.palantir.atlasdb.ptobject.EncodingUtils;
import com.palantir.atlasdb.schema.Namespace;
import com.palantir.atlasdb.table.api.AtlasDbDynamicMutableExpiringTable;
import com.palantir.atlasdb.table.api.AtlasDbDynamicMutablePersistentTable;
import com.palantir.atlasdb.table.api.AtlasDbMutableExpiringTable;
import com.palantir.atlasdb.table.api.AtlasDbMutablePersistentTable;
import com.palantir.atlasdb.table.api.AtlasDbNamedExpiringSet;
import com.palantir.atlasdb.table.api.AtlasDbNamedMutableTable;
import com.palantir.atlasdb.table.api.AtlasDbNamedPersistentSet;
import com.palantir.atlasdb.table.api.ColumnValue;
import com.palantir.atlasdb.table.api.TypedRowResult;
import com.palantir.atlasdb.table.description.ColumnValueDescription.Compression;
import com.palantir.atlasdb.table.description.ValueType;
import com.palantir.atlasdb.table.generation.ColumnValues;
import com.palantir.atlasdb.table.generation.Descending;
import com.palantir.atlasdb.table.generation.NamedColumnValue;
import com.palantir.atlasdb.transaction.api.AtlasDbConstraintCheckingMode;
import com.palantir.atlasdb.transaction.api.ConstraintCheckingTransaction;
import com.palantir.atlasdb.transaction.api.Transaction;
import com.palantir.common.base.AbortingVisitor;
import com.palantir.common.base.AbortingVisitors;
import com.palantir.common.base.BatchingVisitable;
import com.palantir.common.base.BatchingVisitableView;
import com.palantir.common.base.BatchingVisitables;
import com.palantir.common.base.Throwables;
import com.palantir.common.collect.IterableView;
import com.palantir.common.persist.Persistable;
import com.palantir.common.persist.Persistable.Hydrator;
import com.palantir.common.persist.Persistables;
import com.palantir.common.proxy.AsyncProxy;
import com.palantir.util.AssertUtils;
import com.palantir.util.crypto.Sha256Hash;


public final class CubeDecksTable implements
        AtlasDbMutablePersistentTable<CubeDecksTable.CubeDecksRow,
                                         CubeDecksTable.CubeDecksNamedColumnValue<?>,
                                         CubeDecksTable.CubeDecksRowResult>,
        AtlasDbNamedMutableTable<CubeDecksTable.CubeDecksRow,
                                    CubeDecksTable.CubeDecksNamedColumnValue<?>,
                                    CubeDecksTable.CubeDecksRowResult> {
    private final Transaction t;
    private final List<CubeDecksTrigger> triggers;
    private final static String rawTableName = "cube_decks";
    private final String tableName;
    private final Namespace namespace;

    static CubeDecksTable of(Transaction t, Namespace namespace) {
        return new CubeDecksTable(t, namespace, ImmutableList.<CubeDecksTrigger>of());
    }

    static CubeDecksTable of(Transaction t, Namespace namespace, CubeDecksTrigger trigger, CubeDecksTrigger... triggers) {
        return new CubeDecksTable(t, namespace, ImmutableList.<CubeDecksTrigger>builder().add(trigger).add(triggers).build());
    }

    static CubeDecksTable of(Transaction t, Namespace namespace, List<CubeDecksTrigger> triggers) {
        return new CubeDecksTable(t, namespace, triggers);
    }

    private CubeDecksTable(Transaction t, Namespace namespace, List<CubeDecksTrigger> triggers) {
        this.t = t;
        this.tableName = namespace.getName() + "." + rawTableName;
        this.triggers = triggers;
        this.namespace = namespace;
    }

    public static String getRawTableName() {
        return rawTableName;
    }

    public String getTableName() {
        return tableName;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    /**
     * <pre>
     * CubeDecksRow {
     *   {@literal String deckId};
     * }
     * </pre>
     */
    public static final class CubeDecksRow implements Persistable, Comparable<CubeDecksRow> {
        private final String deckId;

        public static CubeDecksRow of(String deckId) {
            return new CubeDecksRow(deckId);
        }

        private CubeDecksRow(String deckId) {
            this.deckId = deckId;
        }

        public String getDeckId() {
            return deckId;
        }

        public static Function<CubeDecksRow, String> getDeckIdFun() {
            return new Function<CubeDecksRow, String>() {
                @Override
                public String apply(CubeDecksRow row) {
                    return row.deckId;
                }
            };
        }

        public static Function<String, CubeDecksRow> fromDeckIdFun() {
            return new Function<String, CubeDecksRow>() {
                @Override
                public CubeDecksRow apply(String row) {
                    return CubeDecksRow.of(row);
                }
            };
        }

        @Override
        public byte[] persistToBytes() {
            byte[] deckIdBytes = EncodingUtils.encodeVarString(deckId);
            return EncodingUtils.add(deckIdBytes);
        }

        public static final Hydrator<CubeDecksRow> BYTES_HYDRATOR = new Hydrator<CubeDecksRow>() {
            @Override
            public CubeDecksRow hydrateFromBytes(byte[] __input) {
                int __index = 0;
                String deckId = EncodingUtils.decodeVarString(__input, __index);
                __index += EncodingUtils.sizeOfVarString(deckId);
                return new CubeDecksRow(deckId);
            }
        };

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("deckId", deckId)
                .toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CubeDecksRow other = (CubeDecksRow) obj;
            return Objects.equal(deckId, other.deckId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(deckId);
        }

        @Override
        public int compareTo(CubeDecksRow o) {
            return ComparisonChain.start()
                .compare(this.deckId, o.deckId)
                .result();
        }
    }

    public interface CubeDecksNamedColumnValue<T> extends NamedColumnValue<T> { /* */ }

    /**
     * <pre>
     * Column value description {
     *   type: com.github.sandorw.cubetracker.server.decks.CompleteDeckList;
     * }
     * </pre>
     */
    public static final class DeckList implements CubeDecksNamedColumnValue<com.github.sandorw.cubetracker.server.decks.CompleteDeckList> {
        private final com.github.sandorw.cubetracker.server.decks.CompleteDeckList value;

        public static DeckList of(com.github.sandorw.cubetracker.server.decks.CompleteDeckList value) {
            return new DeckList(value);
        }

        private DeckList(com.github.sandorw.cubetracker.server.decks.CompleteDeckList value) {
            this.value = value;
        }

        @Override
        public String getColumnName() {
            return "deck_list";
        }

        @Override
        public String getShortColumnName() {
            return "d";
        }

        @Override
        public com.github.sandorw.cubetracker.server.decks.CompleteDeckList getValue() {
            return value;
        }

        @Override
        public byte[] persistValue() {
            byte[] bytes = com.palantir.atlasdb.compress.CompressionUtils.compress(new com.github.sandorw.cubetracker.server.decks.CompleteDeckListPersister().persistToBytes(value), com.palantir.atlasdb.table.description.ColumnValueDescription.Compression.NONE);
            return CompressionUtils.compress(bytes, Compression.NONE);
        }

        @Override
        public byte[] persistColumnName() {
            return PtBytes.toCachedBytes("d");
        }

        public static final Hydrator<DeckList> BYTES_HYDRATOR = new Hydrator<DeckList>() {
            @Override
            public DeckList hydrateFromBytes(byte[] bytes) {
                bytes = CompressionUtils.decompress(bytes, Compression.NONE);
                return of(new com.github.sandorw.cubetracker.server.decks.CompleteDeckListPersister().hydrateFromBytes(com.palantir.atlasdb.compress.CompressionUtils.decompress(bytes, com.palantir.atlasdb.table.description.ColumnValueDescription.Compression.NONE)));
            }
        };

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("Value", this.value)
                .toString();
        }
    }

    public interface CubeDecksTrigger {
        public void putCubeDecks(Multimap<CubeDecksRow, ? extends CubeDecksNamedColumnValue<?>> newRows);
    }

    public static final class CubeDecksRowResult implements TypedRowResult {
        private final RowResult<byte[]> row;

        public static CubeDecksRowResult of(RowResult<byte[]> row) {
            return new CubeDecksRowResult(row);
        }

        private CubeDecksRowResult(RowResult<byte[]> row) {
            this.row = row;
        }

        @Override
        public CubeDecksRow getRowName() {
            return CubeDecksRow.BYTES_HYDRATOR.hydrateFromBytes(row.getRowName());
        }

        public static Function<CubeDecksRowResult, CubeDecksRow> getRowNameFun() {
            return new Function<CubeDecksRowResult, CubeDecksRow>() {
                @Override
                public CubeDecksRow apply(CubeDecksRowResult rowResult) {
                    return rowResult.getRowName();
                }
            };
        }

        public static Function<RowResult<byte[]>, CubeDecksRowResult> fromRawRowResultFun() {
            return new Function<RowResult<byte[]>, CubeDecksRowResult>() {
                @Override
                public CubeDecksRowResult apply(RowResult<byte[]> rowResult) {
                    return new CubeDecksRowResult(rowResult);
                }
            };
        }

        public boolean hasDeckList() {
            return row.getColumns().containsKey(PtBytes.toCachedBytes("d"));
        }

        public com.github.sandorw.cubetracker.server.decks.CompleteDeckList getDeckList() {
            byte[] bytes = row.getColumns().get(PtBytes.toCachedBytes("d"));
            if (bytes == null) {
                return null;
            }
            DeckList value = DeckList.BYTES_HYDRATOR.hydrateFromBytes(bytes);
            return value.getValue();
        }

        public static Function<CubeDecksRowResult, com.github.sandorw.cubetracker.server.decks.CompleteDeckList> getDeckListFun() {
            return new Function<CubeDecksRowResult, com.github.sandorw.cubetracker.server.decks.CompleteDeckList>() {
                @Override
                public com.github.sandorw.cubetracker.server.decks.CompleteDeckList apply(CubeDecksRowResult rowResult) {
                    return rowResult.getDeckList();
                }
            };
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("RowName", getRowName())
                .add("DeckList", getDeckList())
                .toString();
        }
    }

    public enum CubeDecksNamedColumn {
        DECK_LIST {
            @Override
            public byte[] getShortName() {
                return PtBytes.toCachedBytes("d");
            }
        };

        public abstract byte[] getShortName();

        public static Function<CubeDecksNamedColumn, byte[]> toShortName() {
            return new Function<CubeDecksNamedColumn, byte[]>() {
                @Override
                public byte[] apply(CubeDecksNamedColumn namedColumn) {
                    return namedColumn.getShortName();
                }
            };
        }
    }

    public static ColumnSelection getColumnSelection(Collection<CubeDecksNamedColumn> cols) {
        return ColumnSelection.create(Collections2.transform(cols, CubeDecksNamedColumn.toShortName()));
    }

    public static ColumnSelection getColumnSelection(CubeDecksNamedColumn... cols) {
        return getColumnSelection(Arrays.asList(cols));
    }

    private static final Map<String, Hydrator<? extends CubeDecksNamedColumnValue<?>>> shortNameToHydrator =
            ImmutableMap.<String, Hydrator<? extends CubeDecksNamedColumnValue<?>>>builder()
                .put("d", DeckList.BYTES_HYDRATOR)
                .build();

    public Map<CubeDecksRow, com.github.sandorw.cubetracker.server.decks.CompleteDeckList> getDeckLists(Collection<CubeDecksRow> rows) {
        Map<Cell, CubeDecksRow> cells = Maps.newHashMapWithExpectedSize(rows.size());
        for (CubeDecksRow row : rows) {
            cells.put(Cell.create(row.persistToBytes(), PtBytes.toCachedBytes("d")), row);
        }
        Map<Cell, byte[]> results = t.get(tableName, cells.keySet());
        Map<CubeDecksRow, com.github.sandorw.cubetracker.server.decks.CompleteDeckList> ret = Maps.newHashMapWithExpectedSize(results.size());
        for (Entry<Cell, byte[]> e : results.entrySet()) {
            com.github.sandorw.cubetracker.server.decks.CompleteDeckList val = DeckList.BYTES_HYDRATOR.hydrateFromBytes(e.getValue()).getValue();
            ret.put(cells.get(e.getKey()), val);
        }
        return ret;
    }

    public void putDeckList(CubeDecksRow row, com.github.sandorw.cubetracker.server.decks.CompleteDeckList value) {
        put(ImmutableMultimap.of(row, DeckList.of(value)));
    }

    public void putDeckList(Map<CubeDecksRow, com.github.sandorw.cubetracker.server.decks.CompleteDeckList> map) {
        Map<CubeDecksRow, CubeDecksNamedColumnValue<?>> toPut = Maps.newHashMapWithExpectedSize(map.size());
        for (Entry<CubeDecksRow, com.github.sandorw.cubetracker.server.decks.CompleteDeckList> e : map.entrySet()) {
            toPut.put(e.getKey(), DeckList.of(e.getValue()));
        }
        put(Multimaps.forMap(toPut));
    }

    public void putDeckListUnlessExists(CubeDecksRow row, com.github.sandorw.cubetracker.server.decks.CompleteDeckList value) {
        putUnlessExists(ImmutableMultimap.of(row, DeckList.of(value)));
    }

    public void putDeckListUnlessExists(Map<CubeDecksRow, com.github.sandorw.cubetracker.server.decks.CompleteDeckList> map) {
        Map<CubeDecksRow, CubeDecksNamedColumnValue<?>> toPut = Maps.newHashMapWithExpectedSize(map.size());
        for (Entry<CubeDecksRow, com.github.sandorw.cubetracker.server.decks.CompleteDeckList> e : map.entrySet()) {
            toPut.put(e.getKey(), DeckList.of(e.getValue()));
        }
        putUnlessExists(Multimaps.forMap(toPut));
    }

    @Override
    public void put(Multimap<CubeDecksRow, ? extends CubeDecksNamedColumnValue<?>> rows) {
        t.useTable(tableName, this);
        t.put(tableName, ColumnValues.toCellValues(rows));
        for (CubeDecksTrigger trigger : triggers) {
            trigger.putCubeDecks(rows);
        }
    }

    @Override
    public void putUnlessExists(Multimap<CubeDecksRow, ? extends CubeDecksNamedColumnValue<?>> rows) {
        Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>> existing = getRowsMultimap(rows.keySet());
        Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>> toPut = HashMultimap.create();
        for (Entry<CubeDecksRow, ? extends CubeDecksNamedColumnValue<?>> entry : rows.entries()) {
            if (!existing.containsEntry(entry.getKey(), entry.getValue())) {
                toPut.put(entry.getKey(), entry.getValue());
            }
        }
        put(toPut);
    }

    public void deleteDeckList(CubeDecksRow row) {
        deleteDeckList(ImmutableSet.of(row));
    }

    public void deleteDeckList(Iterable<CubeDecksRow> rows) {
        byte[] col = PtBytes.toCachedBytes("d");
        Set<Cell> cells = Cells.cellsWithConstantColumn(Persistables.persistAll(rows), col);
        t.delete(tableName, cells);
    }

    @Override
    public void delete(CubeDecksRow row) {
        delete(ImmutableSet.of(row));
    }

    @Override
    public void delete(Iterable<CubeDecksRow> rows) {
        List<byte[]> rowBytes = Persistables.persistAll(rows);
        Set<Cell> cells = Sets.newHashSetWithExpectedSize(rowBytes.size());
        cells.addAll(Cells.cellsWithConstantColumn(rowBytes, PtBytes.toCachedBytes("d")));
        t.delete(tableName, cells);
    }

    @Override
    public Optional<CubeDecksRowResult> getRow(CubeDecksRow row) {
        return getRow(row, ColumnSelection.all());
    }

    @Override
    public Optional<CubeDecksRowResult> getRow(CubeDecksRow row, ColumnSelection columns) {
        byte[] bytes = row.persistToBytes();
        RowResult<byte[]> rowResult = t.getRows(tableName, ImmutableSet.of(bytes), columns).get(bytes);
        if (rowResult == null) {
            return Optional.absent();
        } else {
            return Optional.of(CubeDecksRowResult.of(rowResult));
        }
    }

    @Override
    public List<CubeDecksRowResult> getRows(Iterable<CubeDecksRow> rows) {
        return getRows(rows, ColumnSelection.all());
    }

    @Override
    public List<CubeDecksRowResult> getRows(Iterable<CubeDecksRow> rows, ColumnSelection columns) {
        SortedMap<byte[], RowResult<byte[]>> results = t.getRows(tableName, Persistables.persistAll(rows), columns);
        List<CubeDecksRowResult> rowResults = Lists.newArrayListWithCapacity(results.size());
        for (RowResult<byte[]> row : results.values()) {
            rowResults.add(CubeDecksRowResult.of(row));
        }
        return rowResults;
    }

    @Override
    public List<CubeDecksRowResult> getAsyncRows(Iterable<CubeDecksRow> rows, ExecutorService exec) {
        return getAsyncRows(rows, ColumnSelection.all(), exec);
    }

    @Override
    public List<CubeDecksRowResult> getAsyncRows(final Iterable<CubeDecksRow> rows, final ColumnSelection columns, ExecutorService exec) {
        Callable<List<CubeDecksRowResult>> c =
                new Callable<List<CubeDecksRowResult>>() {
            @Override
            public List<CubeDecksRowResult> call() {
                return getRows(rows, columns);
            }
        };
        return AsyncProxy.create(exec.submit(c), List.class);
    }

    @Override
    public List<CubeDecksNamedColumnValue<?>> getRowColumns(CubeDecksRow row) {
        return getRowColumns(row, ColumnSelection.all());
    }

    @Override
    public List<CubeDecksNamedColumnValue<?>> getRowColumns(CubeDecksRow row, ColumnSelection columns) {
        byte[] bytes = row.persistToBytes();
        RowResult<byte[]> rowResult = t.getRows(tableName, ImmutableSet.of(bytes), columns).get(bytes);
        if (rowResult == null) {
            return ImmutableList.of();
        } else {
            List<CubeDecksNamedColumnValue<?>> ret = Lists.newArrayListWithCapacity(rowResult.getColumns().size());
            for (Entry<byte[], byte[]> e : rowResult.getColumns().entrySet()) {
                ret.add(shortNameToHydrator.get(PtBytes.toString(e.getKey())).hydrateFromBytes(e.getValue()));
            }
            return ret;
        }
    }

    @Override
    public Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>> getRowsMultimap(Iterable<CubeDecksRow> rows) {
        return getRowsMultimapInternal(rows, ColumnSelection.all());
    }

    @Override
    public Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>> getRowsMultimap(Iterable<CubeDecksRow> rows, ColumnSelection columns) {
        return getRowsMultimapInternal(rows, columns);
    }

    @Override
    public Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>> getAsyncRowsMultimap(Iterable<CubeDecksRow> rows, ExecutorService exec) {
        return getAsyncRowsMultimap(rows, ColumnSelection.all(), exec);
    }

    @Override
    public Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>> getAsyncRowsMultimap(final Iterable<CubeDecksRow> rows, final ColumnSelection columns, ExecutorService exec) {
        Callable<Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>>> c =
                new Callable<Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>>>() {
            @Override
            public Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>> call() {
                return getRowsMultimapInternal(rows, columns);
            }
        };
        return AsyncProxy.create(exec.submit(c), Multimap.class);
    }

    private Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>> getRowsMultimapInternal(Iterable<CubeDecksRow> rows, ColumnSelection columns) {
        SortedMap<byte[], RowResult<byte[]>> results = t.getRows(tableName, Persistables.persistAll(rows), columns);
        return getRowMapFromRowResults(results.values());
    }

    private static Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>> getRowMapFromRowResults(Collection<RowResult<byte[]>> rowResults) {
        Multimap<CubeDecksRow, CubeDecksNamedColumnValue<?>> rowMap = HashMultimap.create();
        for (RowResult<byte[]> result : rowResults) {
            CubeDecksRow row = CubeDecksRow.BYTES_HYDRATOR.hydrateFromBytes(result.getRowName());
            for (Entry<byte[], byte[]> e : result.getColumns().entrySet()) {
                rowMap.put(row, shortNameToHydrator.get(PtBytes.toString(e.getKey())).hydrateFromBytes(e.getValue()));
            }
        }
        return rowMap;
    }

    public BatchingVisitableView<CubeDecksRowResult> getAllRowsUnordered() {
        return getAllRowsUnordered(ColumnSelection.all());
    }

    public BatchingVisitableView<CubeDecksRowResult> getAllRowsUnordered(ColumnSelection columns) {
        return BatchingVisitables.transform(t.getRange(tableName, RangeRequest.builder().retainColumns(columns).build()),
                new Function<RowResult<byte[]>, CubeDecksRowResult>() {
            @Override
            public CubeDecksRowResult apply(RowResult<byte[]> input) {
                return CubeDecksRowResult.of(input);
            }
        });
    }

    @Override
    public List<String> findConstraintFailures(Map<Cell, byte[]> writes,
                                               ConstraintCheckingTransaction transaction,
                                               AtlasDbConstraintCheckingMode constraintCheckingMode) {
        return ImmutableList.of();
    }

    @Override
    public List<String> findConstraintFailuresNoRead(Map<Cell, byte[]> writes,
                                                     AtlasDbConstraintCheckingMode constraintCheckingMode) {
        return ImmutableList.of();
    }

    static String __CLASS_HASH = "oMzXPe95zPZNlcfTeUmOcQ==";
}
