package com.gpc.catalog.bigtableedd.util;

import com.gpc.catalog.bigtableedd.model.Table;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class YamlFileReader<T>{

    public static Set<Table> readTableDetailsFromFile(InputStream inputStream){
        var schemaFile = new Yaml();
        Map<String,List<Object>> bigTableEddObj = schemaFile.load(inputStream);
        return readTable(bigTableEddObj.get("tables"));
    }


    public YamlFileReader() {
    }

    private static Set<Table> readTable(List<Object> tables){
        System.out.println(tables);
        return tables.stream()
                .map(table->{
                    Map<String,Object> tableDetails = (Map<String,Object>) table;
                    var bigTable = (Map<String,Object>)tableDetails.get("table");
                    var columnFamilies = (List<Map<String,String>>) bigTable.get("column-families");
                    var name = (String) bigTable.get("name");
                    var action = (String) bigTable.get("action");
                    return new Table(name,action,columnFamilies.stream()
                            .map(cf->cf.get("name"))
                            .collect(Collectors.toSet()));
                }).collect(Collectors.toSet());
    }


}
