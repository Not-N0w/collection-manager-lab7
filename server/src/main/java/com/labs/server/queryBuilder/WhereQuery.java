package com.labs.server.queryBuilder;

import java.util.Arrays;
import java.util.List;

public class WhereQuery {
    private String condition;

    public WhereQuery(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return condition;
    }
}
