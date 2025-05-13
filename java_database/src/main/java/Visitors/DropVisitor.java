package Visitors;

import DataStuctures.DBHandler;
import DataStuctures.Database;
import Nodes.DatabaseNode;
import Nodes.DropNode;
import Nodes.TableNode;

public class DropVisitor implements ASTVisitor {

    private final DBHandler dbHandler;

    public DropVisitor(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public Boolean visit(DropNode dropNode) { return true; }

    @Override
    public Boolean visit(DatabaseNode dbNode) {
        dbHandler.dropDatabase(dbNode.getDatabase());
        return true;
    }

    @Override
    public Boolean visit(TableNode tableNode) {
        Database currentDB = dbHandler.getCurrentDatabase();
        currentDB.dropTable(tableNode.getTableName());
        return true;
    }

    @Override
    public String getResult() { return ""; }
}
