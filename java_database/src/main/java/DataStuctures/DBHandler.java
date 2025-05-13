package DataStuctures;

import java.util.Map;
import java.util.HashMap;

public class DBHandler {

    private Map<String, Database> databases;
    private Database currentDatabase;

    public DBHandler() {
        this.databases = new HashMap<>();
    }

    public void addDatabase(String databaseName) {
        // Check it DOESN'T exist and add
        String lowerName = databaseName.toLowerCase();
        if (!databases.containsKey(lowerName)) {
            databases.put(lowerName, new Database(lowerName));
            FileHandler.addDatabase(lowerName);
            return;
        }
        throw new IllegalArgumentException("Database '" + databaseName + "' already exists");
    }

    public void dropDatabase(String databaseName) {
        // Check it DOES exist and remove
        String lowerName = databaseName.toLowerCase();
        if (databases.containsKey(lowerName)) {
            if (currentDatabase != null && lowerName.equals(currentDatabase.getName())) {
                currentDatabase = null;
            }
            databases.remove(lowerName);
            FileHandler.deleteDatabase(lowerName);
            return;
        }
        throw new IllegalArgumentException("Database '" + databaseName + "' doesn't exist");
    }

    public void useDatabase(String databaseName) {
        // Check it DOES exist and get
        String lowerName = databaseName.toLowerCase();
        if (databases.containsKey(lowerName)) {
            currentDatabase = databases.get(lowerName);
            return;
        }
        throw new IllegalArgumentException("Database '" + databaseName + "' doesn't exist");
    }

    public Database getCurrentDatabase() {
        if (currentDatabase != null) return currentDatabase;
        throw new IllegalArgumentException("Current database not set.");
    }

    public Map<String, Database> getDatabases() { return databases; }
}
