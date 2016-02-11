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


public final class CubeMatchesTable implements
        AtlasDbMutablePersistentTable<CubeMatchesTable.CubeMatchesRow,
                                         CubeMatchesTable.CubeMatchesNamedColumnValue<?>,
                                         CubeMatchesTable.CubeMatchesRowResult>,
        AtlasDbNamedMutableTable<CubeMatchesTable.CubeMatchesRow,
                                    CubeMatchesTable.CubeMatchesNamedColumnValue<?>,
                                    CubeMatchesTable.CubeMatchesRowResult> {
    private final Transaction t;
    private final List<CubeMatchesTrigger> triggers;
    private final static String rawTableName = "cube_matches";
    private final String tableName;
    private final Namespace namespace;

    static CubeMatchesTable of(Transaction t, Namespace namespace) {
        return new CubeMatchesTable(t, namespace, ImmutableList.<CubeMatchesTrigger>of());
    }

    static CubeMatchesTable of(Transaction t, Namespace namespace, CubeMatchesTrigger trigger, CubeMatchesTrigger... triggers) {
        return new CubeMatchesTable(t, namespace, ImmutableList.<CubeMatchesTrigger>builder().add(trigger).add(triggers).build());
    }

    static CubeMatchesTable of(Transaction t, Namespace namespace, List<CubeMatchesTrigger> triggers) {
        return new CubeMatchesTable(t, namespace, triggers);
    }

    private CubeMatchesTable(Transaction t, Namespace namespace, List<CubeMatchesTrigger> triggers) {
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
     * CubeMatchesRow {
     *   {@literal String firstDeckId};
     *   {@literal String secondDeckId};
     * }
     * </pre>
     */
    public static final class CubeMatchesRow implements Persistable, Comparable<CubeMatchesRow> {
        private final String firstDeckId;
        private final String secondDeckId;

        public static CubeMatchesRow of(String firstDeckId, String secondDeckId) {
            return new CubeMatchesRow(firstDeckId, secondDeckId);
        }

        private CubeMatchesRow(String firstDeckId, String secondDeckId) {
            this.firstDeckId = firstDeckId;
            this.secondDeckId = secondDeckId;
        }

        public String getFirstDeckId() {
            return firstDeckId;
        }

        public String getSecondDeckId() {
            return secondDeckId;
        }

        public static Function<CubeMatchesRow, String> getFirstDeckIdFun() {
            return new Function<CubeMatchesRow, String>() {
                @Override
                public String apply(CubeMatchesRow row) {
                    return row.firstDeckId;
                }
            };
        }

        public static Function<CubeMatchesRow, String> getSecondDeckIdFun() {
            return new Function<CubeMatchesRow, String>() {
                @Override
                public String apply(CubeMatchesRow row) {
                    return row.secondDeckId;
                }
            };
        }

        @Override
        public byte[] persistToBytes() {
            byte[] firstDeckIdBytes = EncodingUtils.encodeVarString(firstDeckId);
            byte[] secondDeckIdBytes = EncodingUtils.encodeVarString(secondDeckId);
            return EncodingUtils.add(firstDeckIdBytes, secondDeckIdBytes);
        }

        public static final Hydrator<CubeMatchesRow> BYTES_HYDRATOR = new Hydrator<CubeMatchesRow>() {
            @Override
            public CubeMatchesRow hydrateFromBytes(byte[] __input) {
                int __index = 0;
                String firstDeckId = EncodingUtils.decodeVarString(__input, __index);
                __index += EncodingUtils.sizeOfVarString(firstDeckId);
                String secondDeckId = EncodingUtils.decodeVarString(__input, __index);
                __index += EncodingUtils.sizeOfVarString(secondDeckId);
                return new CubeMatchesRow(firstDeckId, secondDeckId);
            }
        };

        public static RangeRequest.Builder createPrefixRangeUnsorted(String firstDeckId) {
            byte[] firstDeckIdBytes = EncodingUtils.encodeVarString(firstDeckId);
            return RangeRequest.builder().prefixRange(EncodingUtils.add(firstDeckIdBytes));
        }

        public static Prefix prefixUnsorted(String firstDeckId) {
            byte[] firstDeckIdBytes = EncodingUtils.encodeVarString(firstDeckId);
            return new Prefix(EncodingUtils.add(firstDeckIdBytes));
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("firstDeckId", firstDeckId)
                .add("secondDeckId", secondDeckId)
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
            CubeMatchesRow other = (CubeMatchesRow) obj;
            return Objects.equal(firstDeckId, other.firstDeckId) && Objects.equal(secondDeckId, other.secondDeckId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(firstDeckId, secondDeckId);
        }

        @Override
        public int compareTo(CubeMatchesRow o) {
            return ComparisonChain.start()
                .compare(this.firstDeckId, o.firstDeckId)
                .compare(this.secondDeckId, o.secondDeckId)
                .result();
        }
    }

    public interface CubeMatchesNamedColumnValue<T> extends NamedColumnValue<T> { /* */ }

    /**
     * <pre>
     * Column value description {
     *   type: com.github.sandorw.cubetracker.server.match.MatchResult;
     * }
     * </pre>
     */
    public static final class MatchResult implements CubeMatchesNamedColumnValue<com.github.sandorw.cubetracker.server.match.MatchResult> {
        private final com.github.sandorw.cubetracker.server.match.MatchResult value;

        public static MatchResult of(com.github.sandorw.cubetracker.server.match.MatchResult value) {
            return new MatchResult(value);
        }

        private MatchResult(com.github.sandorw.cubetracker.server.match.MatchResult value) {
            this.value = value;
        }

        @Override
        public String getColumnName() {
            return "match_result";
        }

        @Override
        public String getShortColumnName() {
            return "m";
        }

        @Override
        public com.github.sandorw.cubetracker.server.match.MatchResult getValue() {
            return value;
        }

        @Override
        public byte[] persistValue() {
            byte[] bytes = com.palantir.atlasdb.compress.CompressionUtils.compress(new com.github.sandorw.cubetracker.server.match.MatchResultPersister().persistToBytes(value), com.palantir.atlasdb.table.description.ColumnValueDescription.Compression.NONE);
            return CompressionUtils.compress(bytes, Compression.NONE);
        }

        @Override
        public byte[] persistColumnName() {
            return PtBytes.toCachedBytes("m");
        }

        public static final Hydrator<MatchResult> BYTES_HYDRATOR = new Hydrator<MatchResult>() {
            @Override
            public MatchResult hydrateFromBytes(byte[] bytes) {
                bytes = CompressionUtils.decompress(bytes, Compression.NONE);
                return of(new com.github.sandorw.cubetracker.server.match.MatchResultPersister().hydrateFromBytes(com.palantir.atlasdb.compress.CompressionUtils.decompress(bytes, com.palantir.atlasdb.table.description.ColumnValueDescription.Compression.NONE)));
            }
        };

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("Value", this.value)
                .toString();
        }
    }

    public interface CubeMatchesTrigger {
        public void putCubeMatches(Multimap<CubeMatchesRow, ? extends CubeMatchesNamedColumnValue<?>> newRows);
    }

    public static final class CubeMatchesRowResult implements TypedRowResult {
        private final RowResult<byte[]> row;

        public static CubeMatchesRowResult of(RowResult<byte[]> row) {
            return new CubeMatchesRowResult(row);
        }

        private CubeMatchesRowResult(RowResult<byte[]> row) {
            this.row = row;
        }

        @Override
        public CubeMatchesRow getRowName() {
            return CubeMatchesRow.BYTES_HYDRATOR.hydrateFromBytes(row.getRowName());
        }

        public static Function<CubeMatchesRowResult, CubeMatchesRow> getRowNameFun() {
            return new Function<CubeMatchesRowResult, CubeMatchesRow>() {
                @Override
                public CubeMatchesRow apply(CubeMatchesRowResult rowResult) {
                    return rowResult.getRowName();
                }
            };
        }

        public static Function<RowResult<byte[]>, CubeMatchesRowResult> fromRawRowResultFun() {
            return new Function<RowResult<byte[]>, CubeMatchesRowResult>() {
                @Override
                public CubeMatchesRowResult apply(RowResult<byte[]> rowResult) {
                    return new CubeMatchesRowResult(rowResult);
                }
            };
        }

        public boolean hasMatchResult() {
            return row.getColumns().containsKey(PtBytes.toCachedBytes("m"));
        }

        public com.github.sandorw.cubetracker.server.match.MatchResult getMatchResult() {
            byte[] bytes = row.getColumns().get(PtBytes.toCachedBytes("m"));
            if (bytes == null) {
                return null;
            }
            MatchResult value = MatchResult.BYTES_HYDRATOR.hydrateFromBytes(bytes);
            return value.getValue();
        }

        public static Function<CubeMatchesRowResult, com.github.sandorw.cubetracker.server.match.MatchResult> getMatchResultFun() {
            return new Function<CubeMatchesRowResult, com.github.sandorw.cubetracker.server.match.MatchResult>() {
                @Override
                public com.github.sandorw.cubetracker.server.match.MatchResult apply(CubeMatchesRowResult rowResult) {
                    return rowResult.getMatchResult();
                }
            };
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(getClass().getSimpleName())
                .add("RowName", getRowName())
                .add("MatchResult", getMatchResult())
                .toString();
        }
    }

    public enum CubeMatchesNamedColumn {
        MATCH_RESULT {
            @Override
            public byte[] getShortName() {
                return PtBytes.toCachedBytes("m");
            }
        };

        public abstract byte[] getShortName();

        public static Function<CubeMatchesNamedColumn, byte[]> toShortName() {
            return new Function<CubeMatchesNamedColumn, byte[]>() {
                @Override
                public byte[] apply(CubeMatchesNamedColumn namedColumn) {
                    return namedColumn.getShortName();
                }
            };
        }
    }

    public static ColumnSelection getColumnSelection(Collection<CubeMatchesNamedColumn> cols) {
        return ColumnSelection.create(Collections2.transform(cols, CubeMatchesNamedColumn.toShortName()));
    }

    public static ColumnSelection getColumnSelection(CubeMatchesNamedColumn... cols) {
        return getColumnSelection(Arrays.asList(cols));
    }

    private static final Map<String, Hydrator<? extends CubeMatchesNamedColumnValue<?>>> shortNameToHydrator =
            ImmutableMap.<String, Hydrator<? extends CubeMatchesNamedColumnValue<?>>>builder()
                .put("m", MatchResult.BYTES_HYDRATOR)
                .build();

    public Map<CubeMatchesRow, com.github.sandorw.cubetracker.server.match.MatchResult> getMatchResults(Collection<CubeMatchesRow> rows) {
        Map<Cell, CubeMatchesRow> cells = Maps.newHashMapWithExpectedSize(rows.size());
        for (CubeMatchesRow row : rows) {
            cells.put(Cell.create(row.persistToBytes(), PtBytes.toCachedBytes("m")), row);
        }
        Map<Cell, byte[]> results = t.get(tableName, cells.keySet());
        Map<CubeMatchesRow, com.github.sandorw.cubetracker.server.match.MatchResult> ret = Maps.newHashMapWithExpectedSize(results.size());
        for (Entry<Cell, byte[]> e : results.entrySet()) {
            com.github.sandorw.cubetracker.server.match.MatchResult val = MatchResult.BYTES_HYDRATOR.hydrateFromBytes(e.getValue()).getValue();
            ret.put(cells.get(e.getKey()), val);
        }
        return ret;
    }

    public void putMatchResult(CubeMatchesRow row, com.github.sandorw.cubetracker.server.match.MatchResult value) {
        put(ImmutableMultimap.of(row, MatchResult.of(value)));
    }

    public void putMatchResult(Map<CubeMatchesRow, com.github.sandorw.cubetracker.server.match.MatchResult> map) {
        Map<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> toPut = Maps.newHashMapWithExpectedSize(map.size());
        for (Entry<CubeMatchesRow, com.github.sandorw.cubetracker.server.match.MatchResult> e : map.entrySet()) {
            toPut.put(e.getKey(), MatchResult.of(e.getValue()));
        }
        put(Multimaps.forMap(toPut));
    }

    public void putMatchResultUnlessExists(CubeMatchesRow row, com.github.sandorw.cubetracker.server.match.MatchResult value) {
        putUnlessExists(ImmutableMultimap.of(row, MatchResult.of(value)));
    }

    public void putMatchResultUnlessExists(Map<CubeMatchesRow, com.github.sandorw.cubetracker.server.match.MatchResult> map) {
        Map<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> toPut = Maps.newHashMapWithExpectedSize(map.size());
        for (Entry<CubeMatchesRow, com.github.sandorw.cubetracker.server.match.MatchResult> e : map.entrySet()) {
            toPut.put(e.getKey(), MatchResult.of(e.getValue()));
        }
        putUnlessExists(Multimaps.forMap(toPut));
    }

    @Override
    public void put(Multimap<CubeMatchesRow, ? extends CubeMatchesNamedColumnValue<?>> rows) {
        t.useTable(tableName, this);
        t.put(tableName, ColumnValues.toCellValues(rows));
        for (CubeMatchesTrigger trigger : triggers) {
            trigger.putCubeMatches(rows);
        }
    }

    @Override
    public void putUnlessExists(Multimap<CubeMatchesRow, ? extends CubeMatchesNamedColumnValue<?>> rows) {
        Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> existing = getRowsMultimap(rows.keySet());
        Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> toPut = HashMultimap.create();
        for (Entry<CubeMatchesRow, ? extends CubeMatchesNamedColumnValue<?>> entry : rows.entries()) {
            if (!existing.containsEntry(entry.getKey(), entry.getValue())) {
                toPut.put(entry.getKey(), entry.getValue());
            }
        }
        put(toPut);
    }

    public void deleteMatchResult(CubeMatchesRow row) {
        deleteMatchResult(ImmutableSet.of(row));
    }

    public void deleteMatchResult(Iterable<CubeMatchesRow> rows) {
        byte[] col = PtBytes.toCachedBytes("m");
        Set<Cell> cells = Cells.cellsWithConstantColumn(Persistables.persistAll(rows), col);
        t.delete(tableName, cells);
    }

    @Override
    public void delete(CubeMatchesRow row) {
        delete(ImmutableSet.of(row));
    }

    @Override
    public void delete(Iterable<CubeMatchesRow> rows) {
        List<byte[]> rowBytes = Persistables.persistAll(rows);
        Set<Cell> cells = Sets.newHashSetWithExpectedSize(rowBytes.size());
        cells.addAll(Cells.cellsWithConstantColumn(rowBytes, PtBytes.toCachedBytes("m")));
        t.delete(tableName, cells);
    }

    @Override
    public Optional<CubeMatchesRowResult> getRow(CubeMatchesRow row) {
        return getRow(row, ColumnSelection.all());
    }

    @Override
    public Optional<CubeMatchesRowResult> getRow(CubeMatchesRow row, ColumnSelection columns) {
        byte[] bytes = row.persistToBytes();
        RowResult<byte[]> rowResult = t.getRows(tableName, ImmutableSet.of(bytes), columns).get(bytes);
        if (rowResult == null) {
            return Optional.absent();
        } else {
            return Optional.of(CubeMatchesRowResult.of(rowResult));
        }
    }

    @Override
    public List<CubeMatchesRowResult> getRows(Iterable<CubeMatchesRow> rows) {
        return getRows(rows, ColumnSelection.all());
    }

    @Override
    public List<CubeMatchesRowResult> getRows(Iterable<CubeMatchesRow> rows, ColumnSelection columns) {
        SortedMap<byte[], RowResult<byte[]>> results = t.getRows(tableName, Persistables.persistAll(rows), columns);
        List<CubeMatchesRowResult> rowResults = Lists.newArrayListWithCapacity(results.size());
        for (RowResult<byte[]> row : results.values()) {
            rowResults.add(CubeMatchesRowResult.of(row));
        }
        return rowResults;
    }

    @Override
    public List<CubeMatchesRowResult> getAsyncRows(Iterable<CubeMatchesRow> rows, ExecutorService exec) {
        return getAsyncRows(rows, ColumnSelection.all(), exec);
    }

    @Override
    public List<CubeMatchesRowResult> getAsyncRows(final Iterable<CubeMatchesRow> rows, final ColumnSelection columns, ExecutorService exec) {
        Callable<List<CubeMatchesRowResult>> c =
                new Callable<List<CubeMatchesRowResult>>() {
            @Override
            public List<CubeMatchesRowResult> call() {
                return getRows(rows, columns);
            }
        };
        return AsyncProxy.create(exec.submit(c), List.class);
    }

    @Override
    public List<CubeMatchesNamedColumnValue<?>> getRowColumns(CubeMatchesRow row) {
        return getRowColumns(row, ColumnSelection.all());
    }

    @Override
    public List<CubeMatchesNamedColumnValue<?>> getRowColumns(CubeMatchesRow row, ColumnSelection columns) {
        byte[] bytes = row.persistToBytes();
        RowResult<byte[]> rowResult = t.getRows(tableName, ImmutableSet.of(bytes), columns).get(bytes);
        if (rowResult == null) {
            return ImmutableList.of();
        } else {
            List<CubeMatchesNamedColumnValue<?>> ret = Lists.newArrayListWithCapacity(rowResult.getColumns().size());
            for (Entry<byte[], byte[]> e : rowResult.getColumns().entrySet()) {
                ret.add(shortNameToHydrator.get(PtBytes.toString(e.getKey())).hydrateFromBytes(e.getValue()));
            }
            return ret;
        }
    }

    @Override
    public Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> getRowsMultimap(Iterable<CubeMatchesRow> rows) {
        return getRowsMultimapInternal(rows, ColumnSelection.all());
    }

    @Override
    public Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> getRowsMultimap(Iterable<CubeMatchesRow> rows, ColumnSelection columns) {
        return getRowsMultimapInternal(rows, columns);
    }

    @Override
    public Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> getAsyncRowsMultimap(Iterable<CubeMatchesRow> rows, ExecutorService exec) {
        return getAsyncRowsMultimap(rows, ColumnSelection.all(), exec);
    }

    @Override
    public Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> getAsyncRowsMultimap(final Iterable<CubeMatchesRow> rows, final ColumnSelection columns, ExecutorService exec) {
        Callable<Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>>> c =
                new Callable<Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>>>() {
            @Override
            public Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> call() {
                return getRowsMultimapInternal(rows, columns);
            }
        };
        return AsyncProxy.create(exec.submit(c), Multimap.class);
    }

    private Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> getRowsMultimapInternal(Iterable<CubeMatchesRow> rows, ColumnSelection columns) {
        SortedMap<byte[], RowResult<byte[]>> results = t.getRows(tableName, Persistables.persistAll(rows), columns);
        return getRowMapFromRowResults(results.values());
    }

    private static Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> getRowMapFromRowResults(Collection<RowResult<byte[]>> rowResults) {
        Multimap<CubeMatchesRow, CubeMatchesNamedColumnValue<?>> rowMap = HashMultimap.create();
        for (RowResult<byte[]> result : rowResults) {
            CubeMatchesRow row = CubeMatchesRow.BYTES_HYDRATOR.hydrateFromBytes(result.getRowName());
            for (Entry<byte[], byte[]> e : result.getColumns().entrySet()) {
                rowMap.put(row, shortNameToHydrator.get(PtBytes.toString(e.getKey())).hydrateFromBytes(e.getValue()));
            }
        }
        return rowMap;
    }

    public BatchingVisitableView<CubeMatchesRowResult> getRange(RangeRequest range) {
        if (range.getColumnNames().isEmpty()) {
            range = range.getBuilder().retainColumns(ColumnSelection.all()).build();
        }
        return BatchingVisitables.transform(t.getRange(tableName, range), new Function<RowResult<byte[]>, CubeMatchesRowResult>() {
            @Override
            public CubeMatchesRowResult apply(RowResult<byte[]> input) {
                return CubeMatchesRowResult.of(input);
            }
        });
    }

    public IterableView<BatchingVisitable<CubeMatchesRowResult>> getRanges(Iterable<RangeRequest> ranges) {
        Iterable<BatchingVisitable<RowResult<byte[]>>> rangeResults = t.getRanges(tableName, ranges);
        return IterableView.of(rangeResults).transform(
                new Function<BatchingVisitable<RowResult<byte[]>>, BatchingVisitable<CubeMatchesRowResult>>() {
            @Override
            public BatchingVisitable<CubeMatchesRowResult> apply(BatchingVisitable<RowResult<byte[]>> visitable) {
                return BatchingVisitables.transform(visitable, new Function<RowResult<byte[]>, CubeMatchesRowResult>() {
                    @Override
                    public CubeMatchesRowResult apply(RowResult<byte[]> row) {
                        return CubeMatchesRowResult.of(row);
                    }
                });
            }
        });
    }

    public void deleteRange(RangeRequest range) {
        deleteRanges(ImmutableSet.of(range));
    }

    public void deleteRanges(Iterable<RangeRequest> ranges) {
        BatchingVisitables.concat(getRanges(ranges))
                          .transform(CubeMatchesRowResult.getRowNameFun())
                          .batchAccept(1000, new AbortingVisitor<List<CubeMatchesRow>, RuntimeException>() {
            @Override
            public boolean visit(List<CubeMatchesRow> rows) {
                delete(rows);
                return true;
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

    static String __CLASS_HASH = "VlcGx19OW9v25GTtoPbOUw==";
}
