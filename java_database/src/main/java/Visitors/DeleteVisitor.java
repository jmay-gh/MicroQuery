package Visitors;

import DataStuctures.*;
import Nodes.*;

import java.util.ArrayList;

public class DeleteVisitor implements ASTVisitor {

    private final DBHandler dbHandler;
    private Table table;
    private final ConditionVisitor conditionVisitor;

    public DeleteVisitor(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.conditionVisitor = new ConditionVisitor();
    }

    @Override
    public Boolean visit(DeleteNode deleteNode) { return true; }

    @Override
    public Boolean visit(TableNode tableNode) {
        Database currentDB = dbHandler.getCurrentDatabase();
        table = currentDB.getTable(tableNode.getTableName());
        return true;
    }

    @Override
    public Boolean visit(ConditionNode conditionNode) {
        conditionVisitor.setHeaders(table.getHeaders());
        ArrayList<Row> droppedRows = new ArrayList<>();
        // Get rows for dropping
        for (Row row : table.getRows()) {
            conditionVisitor.setRow(row);
            if (conditionVisitor.visit(conditionNode)) {
                droppedRows.add(row);
            }
        }
        table.dropRows(droppedRows);
        return true;
    }

    @Override
    public String getResult() { return ""; }
}
