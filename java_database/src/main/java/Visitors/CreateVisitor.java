package Visitors;

import DataStuctures.DBHandler;
import DataStuctures.Database;
import Nodes.*;

public class CreateVisitor implements ASTVisitor {

    private DBHandler dbHandler;

    public CreateVisitor(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public Boolean visit(CreateNode createNode) { return true; }

    @Override
    public Boolean visit(DatabaseNode dbNode) {
        dbHandler.addDatabase(dbNode.getDatabase());
        return true;
    }

    @Override
    public Boolean visit(TableNode tableNode) {
        Database currentDB = dbHandler.getCurrentDatabase();
        currentDB.addTable(tableNode.getTableName(), tableNode.getHeaders());
        return true;
    }

    @Override
    public String getResult() { return ""; }
}
