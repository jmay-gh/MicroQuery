package DataStuctures;

import java.io.IOException;
import java.util.ArrayList;

public class Table {

    private String databaseName;
    private String tableName;
    private ArrayList<String> headers;
    private ArrayList<Row> rows;
    private int index;

    public Table(String tableName, ArrayList<String> headers, String databaseName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.headers = addIndex(headers);
        this.rows = new ArrayList<>();
        this.index = 1;
    }

    public final ArrayList<String> addIndex(ArrayList<String> headers) {
        if (!headers.contains("id")) headers.add(0, "id");
        return headers;
    }

    public void addRow(ArrayList<String> data) throws IOException {
        if (data.size() + 1 == headers.size()) {
            // Add ID col
            data.add(0, String.valueOf(index++));
            Row row = new Row();
            row.addColumns(headers, data);
            rows.add(row);
            FileHandler.addTable(this);
            return;
        }
        throw new IllegalArgumentException("Number of attributes provided doesn't match the number of columns");
    }

    public void dropRows(ArrayList<Row> droppedRows) {
        rows.removeAll(droppedRows);
        FileHandler.addTable(this);
    }

    public void addColumn(String colName) {
        for (String header : headers) {
            if (colName.equalsIgnoreCase(header)) {
                throw new IllegalArgumentException("Column '" + header + "' already exists");
            }
        }
        headers.add(colName);
        for (Row row : rows) {
            row.addColumn(colName, "NULL");
        }
        FileHandler.addTable(this);
    }

    public void dropColumn(String colName) {
        if (colName.equalsIgnoreCase("id")) throw new IllegalArgumentException("Cannot drop ID row");
        for (String header : headers) {
            if (colName.equalsIgnoreCase(header)) {
                headers.remove(header);
                for (Row row : rows) {
                    row.dropColumn(header);
                }
                FileHandler.addTable(this);
                return;
            }
        }
        throw new IllegalArgumentException("Column '" + colName + "' doesn't exist");
    }

    public void setRow(ArrayList<String> rowData) {
        Row row = new Row();
        row.addColumns(headers, rowData);
        rows.add(row);
    }

    public String getName() { return tableName; }
    public String getDatabase() { return databaseName; }
    public ArrayList<String> getHeaders() { return headers; }
    public ArrayList<Row> getRows() { return rows; }
    public int getIndex() { return index; }
    public void setIndex(int indexInput) { index = indexInput;}
}
