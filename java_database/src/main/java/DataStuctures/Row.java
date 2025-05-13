package DataStuctures;

import java.util.ArrayList;
import java.util.Iterator;

public class Row {

    private ArrayList<Column> row;

    public Row() {
        this.row = new ArrayList<>();
    }

    public void addColumn(String colName, String data) {
        Column column = new Column(colName, data);
        row.add(column);
    }

    public void addColumns(ArrayList<String> headers, ArrayList<String> data) {
        // Iterate the row data and make columns
        for (int i = 0; i < data.size(); i++) {
            addColumn(headers.get(i), data.get(i));
        }
    }

    public void setRow(ArrayList<Column> row) {
        this.row = row;
    }

    public void dropColumn(String colName) {
        // Messy but prevents concurrent modification
        Iterator<Column> iterator = row.iterator();
        while (iterator.hasNext()) {
            Column col = iterator.next();
            if (colName.equalsIgnoreCase(col.getName())) {
                iterator.remove();
            }
        }
    }

    public void updateData(String colName, String value) {
        for (Column col : row) {
            if (colName.equalsIgnoreCase(col.getName())) {
                col.setData(value);
            }
        }
    }

    public String getData(String columnName) {
        for (Column col : row) {
            if (col.getName().equalsIgnoreCase(columnName)) return col.getData();
        }
        throw new IllegalArgumentException("Table does not contain the column: " + columnName);
    }

    public ArrayList<Column> getColumns() {
        return row;
    }

    public ArrayList<Column> getColumnsExcluding(String attribute) {
        ArrayList<Column> excludedCols = new ArrayList<>();
        for (Column col : row) {
            if (!col.getName().equals(attribute) && !col.getName().equals("id")) {
                excludedCols.add(col);
            }
        }
        return excludedCols;
    }
}

