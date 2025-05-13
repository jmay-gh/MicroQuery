package Visitors;

import DataStuctures.DBHandler;
import Nodes.DatabaseNode;
import Nodes.UseNode;

public class UseVisitor implements ASTVisitor {

    private DBHandler dbHandler;

    public UseVisitor(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public Boolean visit(UseNode useNode) { return true; }

    public Boolean visit(DatabaseNode databaseNode) {
        dbHandler.useDatabase(databaseNode.getDatabase());
        return true;
    }

    @Override
    public String getResult() {
        return "";
    }
}
