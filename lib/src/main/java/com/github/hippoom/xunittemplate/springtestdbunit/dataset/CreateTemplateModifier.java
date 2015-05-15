package com.github.hippoom.xunittemplate.springtestdbunit.dataset;

import com.github.springtestdbunit.dataset.DataSetModifier;
import org.dbunit.dataset.IDataSet;

public abstract class CreateTemplateModifier implements DataSetModifier {

    private String column;
    private Object value;

    public CreateTemplateModifier(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    @Override
    public IDataSet modify(IDataSet dataSet) {
        return new CreateTemplateDataSet(dataSet, column, value);
    }
}