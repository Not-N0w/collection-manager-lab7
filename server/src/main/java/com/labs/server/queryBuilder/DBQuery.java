package com.labs.server.queryBuilder;

import java.util.Arrays;
import java.util.List;

public class DBQuery {
    private String tableName;
    private List<JoinQuery> joins;
    private List<WhereQuery> conditions;
    private QueryType queryType;
    private List<String> columns;
    private String more = "";

    public DBQuery(QueryType queryType, String tableName,  List<JoinQuery> joins, List<WhereQuery> conditions, String ... columns) {
        this.queryType = queryType;
        this.joins = joins;
        this.conditions = conditions;
        this.tableName = tableName;
        this.columns = Arrays.asList(columns);
    }
    public void setMore(String more) {
        this.more += more;
    }
    @Override
    public String toString() {
        String result = queryType.toString() + " ";
        if(queryType == QueryType.INSERT) {
            result += " INTO " + tableName + " (";
            for(var i = 0; i < columns.size(); i++) {
                result += columns.get(i);
                if(i != columns.size() - 1) result += ", ";
            }
            result += ") VALUES (";
            for(var i = 0; i < conditions.size(); i++) {
                result += conditions.get(i);
                if(i != conditions.size() - 1) result += ", ";
            }
            result += ")";
            return result;
        }
        for(var i = 0; i < columns.size(); i++) {
            result += columns.get(i);
            if(i != columns.size() - 1) result += ", ";
        }
        result += " FROM " + tableName;

        for(var i = 0; i < joins.size(); i++) {
            result += " " + joins.get(i);
        }
        if(conditions.size() > 0) {
            result += " WHERE ";
            for (var i = 0; i < conditions.size(); i++) {
                result += conditions.get(i);
                if (i != conditions.size() - 1) result += " AND ";
            }
        }
        return result;


    }
}
