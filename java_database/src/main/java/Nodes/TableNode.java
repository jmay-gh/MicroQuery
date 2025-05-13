package Nodes;

import Visitors.ASTVisitor;

import java.util.ArrayList;

public class TableNode extends ASTNode {

    private ASTNode childNode;
    private final String tableName;
    private ArrayList<String> headers;

    public TableNode(String tableName, ArrayList<String> headers) {
        this.tableName = tableName;
        this.headers = headers;
    }

    public TableNode(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() { return tableName; }
    public ArrayList<String> getHeaders() { return headers; }

    @Override
    public void addChild(ASTNode childNode) { this.childNode = childNode; }
    @Override
    public ASTNode getChild() { return childNode; }
    @Override
    public Boolean accept(ASTVisitor v) {
        v.visit(this);
        if (childNode != null) { childNode.accept(v); }
        return true;
    }
}

