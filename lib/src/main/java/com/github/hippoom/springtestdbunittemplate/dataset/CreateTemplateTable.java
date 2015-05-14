package com.github.hippoom.springtestdbunittemplate.dataset;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;

public class CreateTemplateTable implements ITable {

    private final ITable table;
    private String columnToBeReplaced;
    private Object valueToBeReplaced;

    public CreateTemplateTable(ITable table, String columnToBeReplaced, Object valueToBeReplaced) {
        this.table = table;
        this.columnToBeReplaced = columnToBeReplaced;
        this.valueToBeReplaced = valueToBeReplaced;
    }

    @Override
    public ITableMetaData getTableMetaData() {
        return table.getTableMetaData();
    }

    @Override
    public int getRowCount() {
        return table.getRowCount() * 2;
    }

    @Override
    public Object getValue(int row, String column) throws DataSetException {
        if (row < table.getRowCount()) {
            return table.getValue(row, column);
        } else {
            if (columnToBeReplaced.equalsIgnoreCase(column)) {
                Column c = null;
                ITableMetaData metaData = table.getTableMetaData();
                for (Column cl : metaData.getColumns()) {
                    if (cl.getColumnName().equalsIgnoreCase(column)) {
                        c = cl;
                    }
                }
                return c.getDataType().typeCast(valueToBeReplaced);
            }

            return table.getValue(row - table.getRowCount(), column);
        }

    }
}
