package com.labs.server.queryBuilder;

import java.util.Arrays;
import java.util.List;

public class JoinQuery {
    private String table;
    private String column1, column2;
    private String joinType;

    public JoinQuery(String joinType, String table, String column1, String column2) {
        this.joinType = joinType;
        this.column1 = column1;
        this.column2 = column2;
        this.table = table;
    }

    @Override
    public String toString() {
        String result = joinType + " " + table + " ON " + column1 + " = " + column2;
        return result;
    }
}
