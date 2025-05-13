package Visitors;

import DataStuctures.*;
import Nodes.*;

import java.util.ArrayList;

public class SelectVisitor implements ASTVisitor {

    private final DBHandler dbHandler;
    private Table table;
    private ArrayList<String> attributes;
    private ArrayList<Row> results;
    private ArrayList<String> resultHeaders;
    private final ConditionVisitor conditionVisitor;

    public SelectVisitor(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.conditionVisitor = new ConditionVisitor();
        this.results = new ArrayList<>();
        this.resultHeaders = new ArrayList<>();
        this.attributes = new ArrayList<>();
    }

    @Override
    public Boolean visit(SelectNode selectNode) { return true; }

    @Override
    public Boolean visit(AttributeNode attributeNode) {
        // If * was entered, list is empty
        if (!attributeNode.getAttributes().isEmpty()) {
            attributes = attributeNode.getAttributes();
        }
        return true;
    }

    @Override
    public Boolean visit(TableNode tableNode) {
        Database currentDB = dbHandler.getCurrentDatabase();
        table = currentDB.getTable(tableNode.getTableName());
        checkAttributes();
        // If no conditional node, set results to all
        if (tableNode.getChild() == null) {
            results = table.getRows();
        }
        // If attributes were *, add all headers, else get correct headers
        if (attributes.isEmpty()) {
            resultHeaders = table.getHeaders();
        }
        else {
            for (String attribute : attributes) {
                for (String header : table.getHeaders()) {
                    if (header.equalsIgnoreCase(attribute)) resultHeaders.add(header);
                }
            }
        }
        return true;
    }

    public void checkAttributes() {
        for (String attribute : attributes) {
            boolean matched = false;
            for (String header  : table.getHeaders()) {
                if (header.equalsIgnoreCase(attribute)) {
                    matched = true;
                    break;
                }
            }
            if (!matched) throw new IllegalArgumentException("Attribute '" + attribute + "' does not exist");
        }
    }

    @Override
    public Boolean visit(ConditionNode conditionNode) {
        conditionVisitor.setHeaders(table.getHeaders());
        for (Row row : table.getRows()) {
            conditionVisitor.setRow(row);
            if (conditionVisitor.visit(conditionNode)) {
                results.add(row);
            }
        }
        return true;
    }

    public String removeApostrophes(String colData) {
        if (colData.startsWith("'") && colData.endsWith("'")) {
            colData = colData.substring(1, colData.length() - 1);
        }
        return colData;
    }

    @Override
    public String getResult() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(String.join("\t", resultHeaders)).append("\t\n");
        for (Row row : results) {
            for (String header : resultHeaders) {
                for (Column col : row.getColumns()) {
                    if (header.equalsIgnoreCase(col.getName())) {
                        String colData = col.getData();
                        sb.append(removeApostrophes(colData)).append("\t");
                    }
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}


