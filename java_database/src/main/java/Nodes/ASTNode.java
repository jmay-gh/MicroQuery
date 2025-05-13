package Nodes;

import Visitors.ASTVisitor;

public abstract class ASTNode {
    public abstract Boolean accept(ASTVisitor visitor);
    public abstract void addChild(ASTNode astNode);
    public abstract ASTNode getChild();
}

