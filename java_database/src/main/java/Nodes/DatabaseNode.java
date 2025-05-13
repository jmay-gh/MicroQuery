package Nodes;

import Visitors.ASTVisitor;

public class DatabaseNode extends ASTNode {

    private final String databaseName;
    private ASTNode childNode;

    public DatabaseNode(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabase() { return databaseName; }

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
