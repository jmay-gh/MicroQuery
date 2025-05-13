package Visitors;

import Nodes.*;

import java.io.IOException;

public interface ASTVisitor {

    default Boolean visit(UseNode useNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(CreateNode createNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(DropNode dropNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(DatabaseNode dbNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(ValueNode valueNode) throws IOException {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(TableNode tableNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(AttributeNode attributeNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(AlterNode alterNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(DeleteNode deleteNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(UpdateNode updateNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(InsertNode insertNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(SelectNode selectNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(ConditionNode conditionNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(JoinNode joinNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(ComparisonNode comparisonNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    default Boolean visit(BoolNode boolNode) {
        throw new RuntimeException("Unexpected token in query");
    }
    String getResult();
}




