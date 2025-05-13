package Nodes;

import Visitors.ASTVisitor;

import java.io.IOException;
import java.util.ArrayList;

public class ValueNode extends ASTNode {

    private ASTNode childNode;
    private final ArrayList<String> values;

    public ValueNode(ArrayList<String> values) {
        this.values = values;
    }

    public ArrayList<String> getValues() { return values; }

    @Override
    public void addChild(ASTNode childNode) { this.childNode = childNode; }
    @Override
    public ASTNode getChild() { return childNode; }
    @Override
    public Boolean accept(ASTVisitor v) {
        try {
            v.visit(this);
            if (childNode != null) { childNode.accept(v); }
        }
        catch (IOException error) {
            System.err.println("File writing failed: " + error.getMessage());
        }
        return true;
    }
}
