package Nodes;

import Visitors.ASTVisitor;

public class InsertNode extends ASTNode {

    private ASTNode childNode;

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
