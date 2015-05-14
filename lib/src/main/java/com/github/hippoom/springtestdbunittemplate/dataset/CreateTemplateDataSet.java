package com.github.hippoom.springtestdbunittemplate.dataset;

import org.dbunit.dataset.*;

public class CreateTemplateDataSet extends AbstractDataSet {
    private final IDataSet _dataSet;
    private String column;
    private Object value;

    public CreateTemplateDataSet(IDataSet _dataSet, String column, Object value) {
        this._dataSet = _dataSet;
        this.column = column;
        this.value = value;
    }

    @Override
    protected ITableIterator createIterator(boolean reversed) throws DataSetException {
        return reversed ? _dataSet.reverseIterator() : _dataSet.iterator();
    }

    @Override
    public ITable getTable(String tableName) throws DataSetException {
        return new CreateTemplateTable(_dataSet.getTable(tableName), column, value);
    }

}
