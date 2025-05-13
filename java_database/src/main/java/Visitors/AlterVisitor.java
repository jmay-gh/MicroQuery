package Visitors;

import DataStuctures.DBHandler;
import DataStuctures.Database;
import DataStuctures.Table;
import Nodes.*;

public class AlterVisitor implements ASTVisitor {

    private DBHandler dbHandler;
    private Table table;

    public AlterVisitor(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public Boolean visit(AlterNode alterNode) { return true; }

    @Override
    public Boolean visit(TableNode tableNode) {
        Database currentDB = dbHandler.getCurrentDatabase();
        table = currentDB.getTable(tableNode.getTableName());
        return true;
    }

    @Override
    public Boolean visit(AttributeNode attributeNode) {
        if (attributeNode.getCommand().equals("ADD")) {
            table.addColumn(attributeNode.getAttribute());
        }
        else if (attributeNode.getCommand().equals("DROP")) {
            table.dropColumn(attributeNode.getAttribute());
        }
        return true;
    }

    public String getResult() {
        return "";
    }
}
