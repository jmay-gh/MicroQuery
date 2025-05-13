package Nodes;

import Visitors.ASTVisitor;

public class BoolNode extends ASTNode {

    private final ASTNode left;
    private final ASTNode right;
    private final String boolOperator;

    public BoolNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.boolOperator = operator;
        this.right = right;
    }

    public String getOp() { return boolOperator; }
    public ASTNode getLeft() { return left; }
    public ASTNode getRight() { return right; }

    @Override
    public void addChild(ASTNode childNode) { }
    @Override
    public ASTNode getChild() { return null; }
    @Override
    public Boolean accept(ASTVisitor v) {
        return v.visit(this);
    }
}
