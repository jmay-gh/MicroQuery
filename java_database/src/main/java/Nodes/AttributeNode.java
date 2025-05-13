package Nodes;

import Visitors.ASTVisitor;

import java.util.ArrayList;

public class AttributeNode extends ASTNode {

    private ASTNode childNode;
    private String attributeName;
    private ArrayList<String> attributes;
    private String command;

    public AttributeNode(String attributeName) { this.attributeName = attributeName; }
    public AttributeNode(ArrayList<String> attributes) { this.attributes = attributes; }

    public AttributeNode(String attributeName, String command) {
        this.attributeName = attributeName;
        this.command = command;
    }

    public String getAttribute() { return attributeName; }
    public String getCommand() { return command; }
    public ArrayList<String> getAttributes() { return attributes; }

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
