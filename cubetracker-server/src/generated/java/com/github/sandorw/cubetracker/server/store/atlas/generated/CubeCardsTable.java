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


public final class CubeCardsTable implements
        AtlasDbMutablePersistentTable<CubeCardsTable.CubeCardsRow,
                                         CubeCardsTable.CubeCardsNamedColumnValue<?>,
                                         CubeCardsTable.CubeCardsRowResult>,
        AtlasDbNamedMutableTable<CubeCardsTable.CubeCardsRow,
                                    CubeCardsTable.CubeCardsNamedColumnValue<?>,
                                    CubeCardsTable.CubeCardsRowResult> {
    private final Transaction t;
    private final List<CubeCardsTrigger> triggers;
    private final static String rawTableName = "cube_cards";
    private final String tableName;
    private final Namespace namespace;

    static CubeCardsTable of(Transaction t, Namespace namespace) {
        return new CubeCardsTable(t, namespace, ImmutableList.<CubeCardsTrigger>of());
    }

    static CubeCardsTable of(Transaction t, Namespace namespace, CubeCardsTrigger trigger, CubeCardsTrigger... triggers) {
        return new CubeCardsTable(t, namespace, ImmutableList.<CubeCardsTrigger>builder().add(trigger).add(triggers).build());
    }

    static CubeCardsTable of(Transaction t, Namespace namespace, List<CubeCardsTrigger> triggers) {
        return new CubeCardsTable(t, namespace, triggers);
    }

    private CubeCardsTable(Transaction t, Namespace namespace, List<CubeCardsTrigger> triggers) {
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
     * CubeCardsRow {
     *   {@literal String cardName};
     * }
     * </pre>
     */
    public static final class CubeCardsRow implements Persistable, Comparable<CubeCardsRow> {
        private final String cardName;

        public static CubeCardsRow of(String cardName) {
            return new CubeCardsRow(cardName);
        }

        private CubeCardsRow(String cardName) {
            this.cardName = cardName;
        }

        public String getCardName() {
            return cardName;
        }

        public static Function<CubeCardsRow, String> getCardNameFun() {
            return new Function<CubeCardsRow, String>() {
                @Override
                public String apply(CubeCardsRow row) {
                    return row.cardName;
                }
            };
        }

        public static Function<String, CubeCardsRow> fromCardNameFun() {
            return new Function<String, CubeCardsRow>() {
                @Override
                public CubeCardsRow apply(String row) {
                    return CubeCardsRow.of(row);
                }
            };
        }

        @Override
        public byte[] persistToBytes() {
            byte[] cardNameBytes = EncodingUtils.encodeVarString(cardName);
            return EncodingUtils.add(cardNameBytes);
        }

        public static final Hydrator<CubeCardsRow> BYTES_HYDRATOR = new Hydrator<CubeCardsRow>() {
            @Override
            public CubeCardsRow hydrateFromBytes(byte[] __input) {
                int __index = 0;
                String cardName = EncodingUtils.decodeVarString(__input, __index);
                __index += EncodingUtils.sizeOfVarString(cardName);
                return new CubeCardsRow(cardName);
            }
        };

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("cardName", cardName)
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
            CubeCardsRow other = (CubeCardsRow) obj;
            return Objects.equal(cardName, other.cardName);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(cardName);
        }

        @Override
        public int compareTo(CubeCardsRow o) {
            return ComparisonChain.start()
                .compare(this.cardName, o.cardName)
                .result();
        }
    }

    public interface CubeCardsNamedColumnValue<T> extends NamedColumnValue<T> { /* */ }

    /**
     * <pre>
     * Column value description {
     *   type: com.github.sandorw.cubetracker.server.cards.CardUsageData;
     * }
     * </pre>
     */
    public static final class CardUsage implements CubeCardsNamedColumnValue<com.github.sandorw.cubetracker.server.cards.CardUsageData> {
        private final com.github.sandorw.cubetracker.server.cards.CardUsageData value;

        public static CardUsage of(com.github.sandorw.cubetracker.server.cards.CardUsageData value) {
            return new CardUsage(value);
        }

        private CardUsage(com.github.sandorw.cubetracker.server.cards.CardUsageData value) {
            this.value = value;
        }

        @Override
        public String getColumnName() {
            return "card_usage";
        }

        @Override
        public String getShortColumnName() {
            return "c";
        }

        @Override
        public com.github.sandorw.cubetracker.server.cards.CardUsageData getValue() {
            return value;
        }

        @Override
        public byte[] persistValue() {
            byte[] bytes = com.palantir.atlasdb.compress.CompressionUtils.compress(new com.github.sandorw.cubetracker.server.cards.CardUsageDataPersister().persistToBytes(value), com.palantir.atlasdb.table.description.ColumnValueDescription.Compression.NONE);
            return CompressionUtils.compress(bytes, Compression.NONE);
        }

        @Override
        public byte[] persistColumnName() {
            return PtBytes.toCachedBytes("c");
        }

        public static final Hydrator<CardUsage> BYTES_HYDRATOR = new Hydrator<CardUsage>() {
            @Override
            public CardUsage hydrateFromBytes(byte[] bytes) {
                bytes = CompressionUtils.decompress(bytes, Compression.NONE);
                return of(new com.github.sandorw.cubetracker.server.cards.CardUsageDataPersister().hydrateFromBytes(com.palantir.atlasdb.compress.CompressionUtils.decompress(bytes, com.palantir.atlasdb.table.description.ColumnValueDescription.Compression.NONE)));
            }
        };

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("Value", this.value)
                .toString();
        }
    }

    public interface CubeCardsTrigger {
        public void putCubeCards(Multimap<CubeCardsRow, ? extends CubeCardsNamedColumnValue<?>> newRows);
    }

    public static final class CubeCardsRowResult implements TypedRowResult {
        private final RowResult<byte[]> row;

        public static CubeCardsRowResult of(RowResult<byte[]> row) {
            return new CubeCardsRowResult(row);
        }

        private CubeCardsRowResult(RowResult<byte[]> row) {
            this.row = row;
        }

        @Override
        public CubeCardsRow getRowName() {
            return CubeCardsRow.BYTES_HYDRATOR.hydrateFromBytes(row.getRowName());
        }

        public static Function<CubeCardsRowResult, CubeCardsRow> getRowNameFun() {
            return new Function<CubeCardsRowResult, CubeCardsRow>() {
                @Override
                public CubeCardsRow apply(CubeCardsRowResult rowResult) {
                    return rowResult.getRowName();
                }
            };
        }

        public static Function<RowResult<byte[]>, CubeCardsRowResult> fromRawRowResultFun() {
            return new Function<RowResult<byte[]>, CubeCardsRowResult>() {
                @Override
                public CubeCardsRowResult apply(RowResult<byte[]> rowResult) {
                    return new CubeCardsRowResult(rowResult);
                }
            };
        }

        public boolean hasCardUsage() {
            return row.getColumns().containsKey(PtBytes.toCachedBytes("c"));
        }

        public com.github.sandorw.cubetracker.server.cards.CardUsageData getCardUsage() {
            byte[] bytes = row.getColumns().get(PtBytes.toCachedBytes("c"));
            if (bytes == null) {
                return null;
            }
            CardUsage value = CardUsage.BYTES_HYDRATOR.hydrateFromBytes(bytes);
            return value.getValue();
        }

        public static Function<CubeCardsRowResult, com.github.sandorw.cubetracker.server.cards.CardUsageData> getCardUsageFun() {
            return new Function<CubeCardsRowResult, com.github.sandorw.cubetracker.server.cards.CardUsageData>() {
                @Override
                public com.github.sandorw.cubetracker.server.cards.CardUsageData apply(CubeCardsRowResult rowResult) {
                    return rowResult.getCardUsage();
                }
            };
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("RowName", getRowName())
                .add("CardUsage", getCardUsage())
                .toString();
        }
    }

    public enum CubeCardsNamedColumn {
        CARD_USAGE {
            @Override
            public byte[] getShortName() {
                return PtBytes.toCachedBytes("c");
            }
        };

        public abstract byte[] getShortName();

        public static Function<CubeCardsNamedColumn, byte[]> toShortName() {
            return new Function<CubeCardsNamedColumn, byte[]>() {
                @Override
                public byte[] apply(CubeCardsNamedColumn namedColumn) {
                    return namedColumn.getShortName();
                }
            };
        }
    }

    public static ColumnSelection getColumnSelection(Collection<CubeCardsNamedColumn> cols) {
        return ColumnSelection.create(Collections2.transform(cols, CubeCardsNamedColumn.toShortName()));
    }

    public static ColumnSelection getColumnSelection(CubeCardsNamedColumn... cols) {
        return getColumnSelection(Arrays.asList(cols));
    }

    private static final Map<String, Hydrator<? extends CubeCardsNamedColumnValue<?>>> shortNameToHydrator =
            ImmutableMap.<String, Hydrator<? extends CubeCardsNamedColumnValue<?>>>builder()
                .put("c", CardUsage.BYTES_HYDRATOR)
                .build();

    public Map<CubeCardsRow, com.github.sandorw.cubetracker.server.cards.CardUsageData> getCardUsages(Collection<CubeCardsRow> rows) {
        Map<Cell, CubeCardsRow> cells = Maps.newHashMapWithExpectedSize(rows.size());
        for (CubeCardsRow row : rows) {
            cells.put(Cell.create(row.persistToBytes(), PtBytes.toCachedBytes("c")), row);
        }
        Map<Cell, byte[]> results = t.get(tableName, cells.keySet());
        Map<CubeCardsRow, com.github.sandorw.cubetracker.server.cards.CardUsageData> ret = Maps.newHashMapWithExpectedSize(results.size());
        for (Entry<Cell, byte[]> e : results.entrySet()) {
            com.github.sandorw.cubetracker.server.cards.CardUsageData val = CardUsage.BYTES_HYDRATOR.hydrateFromBytes(e.getValue()).getValue();
            ret.put(cells.get(e.getKey()), val);
        }
        return ret;
    }

    public void putCardUsage(CubeCardsRow row, com.github.sandorw.cubetracker.server.cards.CardUsageData value) {
        put(ImmutableMultimap.of(row, CardUsage.of(value)));
    }

    public void putCardUsage(Map<CubeCardsRow, com.github.sandorw.cubetracker.server.cards.CardUsageData> map) {
        Map<CubeCardsRow, CubeCardsNamedColumnValue<?>> toPut = Maps.newHashMapWithExpectedSize(map.size());
        for (Entry<CubeCardsRow, com.github.sandorw.cubetracker.server.cards.CardUsageData> e : map.entrySet()) {
            toPut.put(e.getKey(), CardUsage.of(e.getValue()));
        }
        put(Multimaps.forMap(toPut));
    }

    public void putCardUsageUnlessExists(CubeCardsRow row, com.github.sandorw.cubetracker.server.cards.CardUsageData value) {
        putUnlessExists(ImmutableMultimap.of(row, CardUsage.of(value)));
    }

    public void putCardUsageUnlessExists(Map<CubeCardsRow, com.github.sandorw.cubetracker.server.cards.CardUsageData> map) {
        Map<CubeCardsRow, CubeCardsNamedColumnValue<?>> toPut = Maps.newHashMapWithExpectedSize(map.size());
        for (Entry<CubeCardsRow, com.github.sandorw.cubetracker.server.cards.CardUsageData> e : map.entrySet()) {
            toPut.put(e.getKey(), CardUsage.of(e.getValue()));
        }
        putUnlessExists(Multimaps.forMap(toPut));
    }

    @Override
    public void put(Multimap<CubeCardsRow, ? extends CubeCardsNamedColumnValue<?>> rows) {
        t.useTable(tableName, this);
        t.put(tableName, ColumnValues.toCellValues(rows));
        for (CubeCardsTrigger trigger : triggers) {
            trigger.putCubeCards(rows);
        }
    }

    @Override
    public void putUnlessExists(Multimap<CubeCardsRow, ? extends CubeCardsNamedColumnValue<?>> rows) {
        Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>> existing = getRowsMultimap(rows.keySet());
        Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>> toPut = HashMultimap.create();
        for (Entry<CubeCardsRow, ? extends CubeCardsNamedColumnValue<?>> entry : rows.entries()) {
            if (!existing.containsEntry(entry.getKey(), entry.getValue())) {
                toPut.put(entry.getKey(), entry.getValue());
            }
        }
        put(toPut);
    }

    public void deleteCardUsage(CubeCardsRow row) {
        deleteCardUsage(ImmutableSet.of(row));
    }

    public void deleteCardUsage(Iterable<CubeCardsRow> rows) {
        byte[] col = PtBytes.toCachedBytes("c");
        Set<Cell> cells = Cells.cellsWithConstantColumn(Persistables.persistAll(rows), col);
        t.delete(tableName, cells);
    }

    @Override
    public void delete(CubeCardsRow row) {
        delete(ImmutableSet.of(row));
    }

    @Override
    public void delete(Iterable<CubeCardsRow> rows) {
        List<byte[]> rowBytes = Persistables.persistAll(rows);
        Set<Cell> cells = Sets.newHashSetWithExpectedSize(rowBytes.size());
        cells.addAll(Cells.cellsWithConstantColumn(rowBytes, PtBytes.toCachedBytes("c")));
        t.delete(tableName, cells);
    }

    @Override
    public Optional<CubeCardsRowResult> getRow(CubeCardsRow row) {
        return getRow(row, ColumnSelection.all());
    }

    @Override
    public Optional<CubeCardsRowResult> getRow(CubeCardsRow row, ColumnSelection columns) {
        byte[] bytes = row.persistToBytes();
        RowResult<byte[]> rowResult = t.getRows(tableName, ImmutableSet.of(bytes), columns).get(bytes);
        if (rowResult == null) {
            return Optional.absent();
        } else {
            return Optional.of(CubeCardsRowResult.of(rowResult));
        }
    }

    @Override
    public List<CubeCardsRowResult> getRows(Iterable<CubeCardsRow> rows) {
        return getRows(rows, ColumnSelection.all());
    }

    @Override
    public List<CubeCardsRowResult> getRows(Iterable<CubeCardsRow> rows, ColumnSelection columns) {
        SortedMap<byte[], RowResult<byte[]>> results = t.getRows(tableName, Persistables.persistAll(rows), columns);
        List<CubeCardsRowResult> rowResults = Lists.newArrayListWithCapacity(results.size());
        for (RowResult<byte[]> row : results.values()) {
            rowResults.add(CubeCardsRowResult.of(row));
        }
        return rowResults;
    }

    @Override
    public List<CubeCardsRowResult> getAsyncRows(Iterable<CubeCardsRow> rows, ExecutorService exec) {
        return getAsyncRows(rows, ColumnSelection.all(), exec);
    }

    @Override
    public List<CubeCardsRowResult> getAsyncRows(final Iterable<CubeCardsRow> rows, final ColumnSelection columns, ExecutorService exec) {
        Callable<List<CubeCardsRowResult>> c =
                new Callable<List<CubeCardsRowResult>>() {
            @Override
            public List<CubeCardsRowResult> call() {
                return getRows(rows, columns);
            }
        };
        return AsyncProxy.create(exec.submit(c), List.class);
    }

    @Override
    public List<CubeCardsNamedColumnValue<?>> getRowColumns(CubeCardsRow row) {
        return getRowColumns(row, ColumnSelection.all());
    }

    @Override
    public List<CubeCardsNamedColumnValue<?>> getRowColumns(CubeCardsRow row, ColumnSelection columns) {
        byte[] bytes = row.persistToBytes();
        RowResult<byte[]> rowResult = t.getRows(tableName, ImmutableSet.of(bytes), columns).get(bytes);
        if (rowResult == null) {
            return ImmutableList.of();
        } else {
            List<CubeCardsNamedColumnValue<?>> ret = Lists.newArrayListWithCapacity(rowResult.getColumns().size());
            for (Entry<byte[], byte[]> e : rowResult.getColumns().entrySet()) {
                ret.add(shortNameToHydrator.get(PtBytes.toString(e.getKey())).hydrateFromBytes(e.getValue()));
            }
            return ret;
        }
    }

    @Override
    public Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>> getRowsMultimap(Iterable<CubeCardsRow> rows) {
        return getRowsMultimapInternal(rows, ColumnSelection.all());
    }

    @Override
    public Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>> getRowsMultimap(Iterable<CubeCardsRow> rows, ColumnSelection columns) {
        return getRowsMultimapInternal(rows, columns);
    }

    @Override
    public Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>> getAsyncRowsMultimap(Iterable<CubeCardsRow> rows, ExecutorService exec) {
        return getAsyncRowsMultimap(rows, ColumnSelection.all(), exec);
    }

    @Override
    public Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>> getAsyncRowsMultimap(final Iterable<CubeCardsRow> rows, final ColumnSelection columns, ExecutorService exec) {
        Callable<Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>>> c =
                new Callable<Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>>>() {
            @Override
            public Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>> call() {
                return getRowsMultimapInternal(rows, columns);
            }
        };
        return AsyncProxy.create(exec.submit(c), Multimap.class);
    }

    private Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>> getRowsMultimapInternal(Iterable<CubeCardsRow> rows, ColumnSelection columns) {
        SortedMap<byte[], RowResult<byte[]>> results = t.getRows(tableName, Persistables.persistAll(rows), columns);
        return getRowMapFromRowResults(results.values());
    }

    private static Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>> getRowMapFromRowResults(Collection<RowResult<byte[]>> rowResults) {
        Multimap<CubeCardsRow, CubeCardsNamedColumnValue<?>> rowMap = HashMultimap.create();
        for (RowResult<byte[]> result : rowResults) {
            CubeCardsRow row = CubeCardsRow.BYTES_HYDRATOR.hydrateFromBytes(result.getRowName());
            for (Entry<byte[], byte[]> e : result.getColumns().entrySet()) {
                rowMap.put(row, shortNameToHydrator.get(PtBytes.toString(e.getKey())).hydrateFromBytes(e.getValue()));
            }
        }
        return rowMap;
    }

    public BatchingVisitableView<CubeCardsRowResult> getAllRowsUnordered() {
        return getAllRowsUnordered(ColumnSelection.all());
    }

    public BatchingVisitableView<CubeCardsRowResult> getAllRowsUnordered(ColumnSelection columns) {
        return BatchingVisitables.transform(t.getRange(tableName, RangeRequest.builder().retainColumns(columns).build()),
                new Function<RowResult<byte[]>, CubeCardsRowResult>() {
            @Override
            public CubeCardsRowResult apply(RowResult<byte[]> input) {
                return CubeCardsRowResult.of(input);
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

    static String __CLASS_HASH = "WJ4o/poDiaLZjpyW68FmQQ==";
}
