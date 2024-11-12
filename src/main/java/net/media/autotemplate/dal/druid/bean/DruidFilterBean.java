package net.media.autotemplate.dal.druid.bean;

import net.media.autotemplate.dal.druid.DruidConstants;

import java.util.Objects;

/*
    Created by shubham-ar
    on 1/12/17 3:36 PM   
*/
public class DruidFilterBean {
    final Dimension dimension;
    final String value;
    final DruidConstants.Filter operation;

    public DruidFilterBean(Dimension dimension, String value, DruidConstants.Filter filter) {
        this.dimension = dimension;
        this.value = value;
        this.operation = filter;
    }

    public DruidFilterBean(Dimension dimension, String value) {
        this.dimension = dimension;
        this.value = value;
        this.operation = DruidConstants.Filter.EQUAL;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public String getValue() {
        return value;
    }

    public DruidConstants.Filter getOperation() {
        return operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DruidFilterBean that = (DruidFilterBean) o;
        return dimension == that.dimension &&
                Objects.equals(value, that.value) &&
                operation == that.operation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimension, value, operation);
    }
}
