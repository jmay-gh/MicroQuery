package Nodes;

import Visitors.ASTVisitor;

public class ConditionNode extends ASTNode {

    private ASTNode childNode;

    @Override
    public void addChild(ASTNode childNode) { this.childNode = childNode; }
    @Override
    public ASTNode getChild() { return childNode; }
    @Override
    public Boolean accept(ASTVisitor v) {
        return v.visit(this);
    }
}

