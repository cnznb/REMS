public class old{
    public Builder add(String column, DataType type, List<String> path,
        ColumnPolicy columnPolicy, ReferenceInfo.IndexType indexType,
        boolean partitionBy) {
        RowGranularity rowGranularity = granularity;
        if (partitionBy) {
        rowGranularity = RowGranularity.PARTITION;
        }
        ReferenceInfo info = new ReferenceInfo(new ReferenceIdent(ident, column, path),
        rowGranularity, type, columnPolicy, indexType);
        if (info.ident().isColumn()) {
        columns.add(info);
        }
        references.put(info.ident().columnIdent(), info);
        if (partitionBy) {
        partitionedByColumns.add(info);
        partitionedBy.add(info.ident().columnIdent());
        }
        return this;
    }
}
