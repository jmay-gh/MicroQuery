package Nodes;

import Visitors.ASTVisitor;

public class ComparisonNode extends ASTNode {

    private final String left;
    private final String right;
    private final String compOperator;

    public ComparisonNode(String left, String operator, String right) {
        this.left = left;
        this.compOperator = operator;
        this.right = right;
    }

    public String getOp() { return compOperator; }
    public String getLeft() { return left; }
    public String getRight() { return right; }

    @Override
    public void addChild(ASTNode childNode) { }
    @Override
    public ASTNode getChild() { return null; }
    @Override
    public Boolean accept(ASTVisitor v) {
        return v.visit(this);
    }
}
