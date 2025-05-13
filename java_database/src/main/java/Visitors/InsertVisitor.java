package Visitors;

import DataStuctures.*;
import Nodes.*;

import java.io.IOException;

public class InsertVisitor implements ASTVisitor {

    private final DBHandler dbHandler;
    private Table table;

    public InsertVisitor(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public Boolean visit(InsertNode insertNode) { return true; }

    @Override
    public Boolean visit(TableNode tableNode) {
        Database currentDB = dbHandler.getCurrentDatabase();
        table = currentDB.getTable(tableNode.getTableName());
        return true;
    }

    @Override
    public Boolean visit (ValueNode valueNode) {
        try {
            table.addRow(valueNode.getValues());
        }
        catch (IOException error) { System.err.println("Failed to write insert to file"); }
        return true;
    }

    public String getResult() {
        return "";
    }
}

