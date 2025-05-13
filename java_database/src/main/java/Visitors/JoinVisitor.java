package Visitors;

import DataStuctures.*;
import Nodes.*;

import java.util.ArrayList;

public class JoinVisitor implements ASTVisitor {

    private DBHandler dbHandler;
    private Table table1;
    private Table table2;
    private String attribute1;
    private String attribute2;
    private ArrayList<String> headers;
    private ArrayList<Row> results;

    public JoinVisitor(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.headers = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    @Override
    public Boolean visit(JoinNode joinNode) { return true; }

    @Override
    public Boolean visit(TableNode tableNode) {
        Database currentDB = dbHandler.getCurrentDatabase();
        if (table1 == null) {
            table1 = currentDB.getTable(tableNode.getTableName());
        }
        else if (table2 == null) {
            table2 = currentDB.getTable(tableNode.getTableName());
        }
        return true;
    }

    @Override
    public Boolean visit(AttributeNode attributeNode) {
        if (attribute1 == null) {
            attribute1 = attributeNode.getAttribute();
            checkAttributes(attribute1, table1);
        }
        else if (attribute2 == null) {
            attribute2 = attributeNode.getAttribute();
            checkAttributes(attribute2, table2);
            evaluateJoin();
        }
        return true;
    }

    public void checkAttributes(String attribute, Table table) {
        for (String header : table.getHeaders()) {
            if (header.equalsIgnoreCase(attribute)) {
                return;
            }
        }
        throw new IllegalArgumentException("Attribute '" + attribute + "' does not exist");
    }

    public void evaluateJoin() {
        headers.add("id");
        evaluateHeaders(table1, attribute1);
        evaluateHeaders(table2, attribute2);
        int index = 1;
        for (Row row2 : table2.getRows()) {
            for (Row row1 : table1.getRows()) {
                if (row1.getData(attribute1).equals(row2.getData(attribute2))) {
                    results.add(evaluateRow(row1, row2, index));
                    index++;
                }
            }
        }
    }

    public Row evaluateRow(Row row1, Row row2, int index) {
        ArrayList<Column> rowData = new ArrayList<>();
        // Add id column
        Column idCol = new Column("id", String.valueOf(index));
        rowData.add(idCol);
        // Add rows excluding matched ones
        rowData.addAll(row1.getColumnsExcluding(attribute1));
        rowData.addAll(row2.getColumnsExcluding(attribute2));
        Row newRow = new Row();
        newRow.setRow(rowData);
        return newRow;
    }

    public void evaluateHeaders(Table table, String attribute) {
        for (String header : table.getHeaders()) {
            if (!header.equalsIgnoreCase(attribute) && !header.equals("id")) {
                headers.add(table.getName() + "." + header);
            }
        }
    }

    public String getResult() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(String.join("\t", headers)).append("\t\n");
        for (Row row : results) {
            for (Column col : row.getColumns()) {
                String colData = col.getData();
                if (colData.startsWith("'") && colData.endsWith("'")) {
                    colData = colData.substring(1, colData.length() - 1);
                }
                sb.append(colData).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}


