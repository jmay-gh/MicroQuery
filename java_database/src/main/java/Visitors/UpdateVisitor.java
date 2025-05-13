package Visitors;

import DataStuctures.*;
import Nodes.*;

import java.util.ArrayList;

public class UpdateVisitor implements ASTVisitor {

    private final DBHandler dbHandler;
    private Table table;
    private final ArrayList<String> attributes;
    private final ArrayList<String> values;
    private final ConditionVisitor conditionVisitor;

    public UpdateVisitor(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.attributes = new ArrayList<>();
        this.values = new ArrayList<>();
        this.conditionVisitor = new ConditionVisitor();
    }

    @Override
    public Boolean visit(UpdateNode updateNode) { return true; }

    @Override
    public Boolean visit(TableNode tableNode) {
        Database currentDB = dbHandler.getCurrentDatabase();
        table = currentDB.getTable(tableNode.getTableName());
        return true;
    }

    @Override
    public Boolean visit(ValueNode valueNode) {
        ArrayList<String> valueList = valueNode.getValues();
        for (int i = 0; i < valueList.size(); i++) {
            if (i%2 == 0) {
                boolean matchFound = false;
                for (String header : table.getHeaders()) {
                    if (header.equalsIgnoreCase(valueList.get(i))) {
                        attributes.add(header);
                        matchFound = true;
                    }
                }
                if (!matchFound) throw new IllegalArgumentException("Attribute '"
                        + valueList.get(i) + "' not in table");
            }
            else values.add(valueList.get(i));
        }
        return true;
    }

    @Override
    public Boolean visit(ConditionNode conditionNode) {
        conditionVisitor.setHeaders(table.getHeaders());
        for (Row row : table.getRows()) {
            conditionVisitor.setRow(row);
            if (conditionVisitor.visit(conditionNode)) {
                for (int i = 0; i < attributes.size(); i++) {
                    row.updateData(attributes.get(i), values.get(i));
                }
            }
        }
        FileHandler.addTable(table);
        return true;
    }

    @Override
    public String getResult() {
        return "";
    }
}
