package com.gpc.catalog.bigtableedd.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class Table {
    String tableName;
    ActionType action;
    Set<String> families ;

    public Table(String tableName, String action, Set<String> families) {
        this.tableName = tableName;
        this.action = ActionType.valueOf(action);
        this.families = families;
    }
}
