package DataStuctures;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Database {

    private final String databaseName;
    private Map<String, Table> tables;

    public Database(String databaseName) {
        this.databaseName = databaseName;
        this.tables = new HashMap<>();
    }

    public void addTable(String tableName, ArrayList<String> headers) {
        // Check the table DOESN'T exist
        String lowerName = tableName.toLowerCase();
        if (!tables.containsKey(lowerName)) {
            Table newTable = new Table(lowerName, headers, databaseName);
            tables.put(lowerName, newTable);
            FileHandler.addTable(newTable);
            return;
        }
        throw new IllegalArgumentException("Table '" + tableName + "' already exists");
    }

    public void dropTable(String tableName) {
        Table table = getTable(tableName);
        FileHandler.deleteTable(table);
        tables.remove(table.getName());
    }

    public Table getTable(String tableName) {
        // Check the table DOES exist
        String lowerName = tableName.toLowerCase();
        if (tables.containsKey(lowerName)) {
            return tables.get(lowerName);
        }
        throw new IllegalArgumentException("Table '" + tableName + "' doesn't exist");
    }

    public String getName() { return databaseName; }

    public void setTable(Table table) { tables.put(table.getName(), table); }
}

