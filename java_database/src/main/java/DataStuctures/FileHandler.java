package DataStuctures;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileHandler {

    private static final File rootDirectory = new File("databases");

    public static void loadDatabases(DBHandler dbHandler) {
        File[] databases = rootDirectory.listFiles(file -> file.isDirectory());
        // If no databases kick out
        if (databases == null) return;
        // Iterate all databases
        for (File database : databases) {
            Database newDatabase = new Database(database.getName());
            dbHandler.getDatabases().put(database.getName(), newDatabase);
            // Get all tab files in database
            File[] tables = database.listFiles(file -> file.isFile() && file.getName().endsWith(".tab"));
            // If no files kick out
            if (tables == null) return;
            loadTables(tables, newDatabase);
        }
    }

    public static void loadTables(File[] tables, Database newDatabase) {
        for (File table : tables) {
            try (BufferedReader br = new BufferedReader(new FileReader(table))) {
                // Get id row index
                int index = (Integer.parseInt(br.readLine().trim()));
                // Get headers
                String line = br.readLine();
                ArrayList<String> headers = new ArrayList<>(Arrays.asList(line.split("\t")));
                // Make table
                String tableName = table.getName().replace(".tab", "");
                Table newTable = new Table(tableName, headers, newDatabase.getName());
                newTable.setIndex(index);
                // Add rows
                while ((line = br.readLine()) != null) {
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(line.split("\t")));
                    newTable.setRow(data);
                }
                // Add to database
                newDatabase.setTable(newTable);
            } catch (IOException error) {
                System.err.println("Failed reading file: " + error.getMessage());
            }
        }
    }

    public static void addDatabase(String databaseName) {
        File directory = new File(rootDirectory, databaseName);
        directory.mkdirs();
    }

    public static void deleteDatabase(String databaseName) {
        File database = new File(rootDirectory, databaseName);
        // Delete tables inside first
        File[] tables = database.listFiles();
        if (tables != null) {
            for (File table : tables) {
                table.delete();
            }
        }
        database.delete();
    }

    public static void addTable(Table table) {
        File database = new File(rootDirectory, table.getDatabase());
        File newTable = new File(database, table.getName() + ".tab");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(newTable))) {
            bw.write(String.valueOf(table.getIndex()));
            bw.newLine();
            bw.write(String.join("\t", table.getHeaders()));
            bw.newLine();
            for (Row row : table.getRows()) {
                ArrayList<String> columnVals = new ArrayList<>();
                for (Column col : row.getColumns()) {
                    columnVals.add(col.getData());
                }
                bw.write(String.join("\t", columnVals));
                bw.newLine();
            }
        }
        catch (IOException error) {
            System.err.println("File writing failed: " + error.getMessage());
        }
    }

    public static void deleteTable(Table table) {
        File database = new File(rootDirectory, table.getDatabase());
        File tableFile = new File(database, table.getName() + ".tab");
        if (!tableFile.delete()) {
            throw new RuntimeException("Table: '" + table.getName() + "' failed to be deleted");
        }
    }
}
