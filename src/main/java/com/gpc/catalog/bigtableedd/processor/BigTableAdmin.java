package com.gpc.catalog.bigtableedd.processor;


import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.cloud.Tuple;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.admin.v2.models.GCRules;
import com.google.cloud.bigtable.admin.v2.models.ModifyColumnFamiliesRequest;
import com.gpc.catalog.bigtableedd.model.ActionType;
import com.gpc.catalog.bigtableedd.model.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gpc.catalog.bigtableedd.model.ActionType.DELETE_COLUMN_FAMILY;

@Component
@Slf4j
public class BigTableAdmin {


    BigtableTableAdminClient bigtableTableAdminClient;

    BigTableAdmin(@Value("${gcp.project-id}") String projectId, @Value("${gcp.instance-id}") String instanceId) throws IOException {
        bigtableTableAdminClient = BigtableTableAdminClient.create(projectId, instanceId);
    }


   public Map<String,String> processBigtableSchemaChangeRequest(Set<Table> tableList){
       return tableList.stream()
               .map(table -> {
                   ActionType actionType = table.getAction();
                   return Tuple.of(table.getTableName(), switch (actionType) {
                       case CREATE_TABLE -> createTableRequest(table);
                       case DELETE_TABLE -> deleteTableRequest(table);
                       case ADD_COLUMN_FAMILY, DELETE_COLUMN_FAMILY -> updateTableRequest(table);
                   });
               }).collect(Collectors.toMap(Tuple::x, Tuple::y));
   }


   private String createTableRequest(Table table){
        if(checkIfTableExist(table.getTableName())){
            return "Table already exist: " + table.getTableName();
        }
       var tableRequest =  CreateTableRequest.of(table.getTableName());
        for(String cfName : table.getFamilies()){
            tableRequest.addFamily(cfName, GCRules.GCRULES.union().rule(GCRules.GCRULES.maxVersions(1)));
        }
        try {
            bigtableTableAdminClient.createTable(tableRequest);
        } catch (Exception exception){
            log.error("Exception while creating table", exception);
        }
        return "Table Created : " + table.getTableName();
   }

   private String updateTableRequest(Table table){
       if(!checkIfTableExist(table.getTableName())){
           return "Table Not Present : " + table.getTableName();
       }
       var request =
               ModifyColumnFamiliesRequest.of(table.getTableName());
       for(String cfName : table.getFamilies()){
           if(DELETE_COLUMN_FAMILY.equals(table.getAction())) {
               request.dropFamily(cfName);
           } else {
               request.addFamily(cfName, GCRules.GCRULES.union().rule(GCRules.GCRULES.maxVersions(1)));
           }
       }

       try {
           bigtableTableAdminClient.modifyFamilies(request);
       } catch (AlreadyExistsException exception){
           log.error("Exception while updating table ", exception);
            return "Table updated Failed: " + exception.getMessage();
       }
       return "Table updated : " + table.getTableName();

   }

   private String deleteTableRequest(Table table){
        bigtableTableAdminClient.deleteTable(table.getTableName());
        return "Table Deleted: " + table.getTableName();
   }




    private boolean checkIfTableExist(String tableName){
        return bigtableTableAdminClient.exists(tableName);
    }


}




