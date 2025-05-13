package edu.uob;

import Nodes.*;
import Parser.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ASTTests {

    private Parser parser;

    @BeforeEach
    public void setup() {
        parser = new Parser();
    }

    @Test
    public void UseASTTests() {
        parser.parse("USE DB1;");
        ASTNode node1 = parser.getRootNode();
        assertInstanceOf(UseNode.class, node1);
        ASTNode node2 = node1.getChild();
        assertInstanceOf(DatabaseNode.class, node2);
        assertNull(node2.getChild());
    }

    @Test
    public void CreateASTTests() {
        parser.parse("CREATE DATABASE DB1;");
        ASTNode node1 = parser.getRootNode();
        assertInstanceOf(CreateNode.class, node1);
        ASTNode node2 = node1.getChild();
        assertInstanceOf(DatabaseNode.class, node2);
        assertNull(node2.getChild());
    }

    @Test
    public void DropASTTests() {
        parser.parse("DROP DATABASE DB1;");
        ASTNode node1 = parser.getRootNode();
        assertInstanceOf(DropNode.class, node1);
        ASTNode node2 = node1.getChild();
        assertInstanceOf(DatabaseNode.class, node2);
        assertNull(node2.getChild());
        parser.parse("DROP TABLE DB1;");
        node1 = parser.getRootNode();
        assertInstanceOf(DropNode.class, node1);
        node2 = node1.getChild();
        assertInstanceOf(TableNode.class, node2);
        assertNull(node2.getChild());
    }

    @Test
    public void UpdateASTTests() {
        parser.parse("UPDATE TABLE1 SET NAME='Anakin' WHERE JOB=='Jedi';");
        ASTNode node1 = parser.getRootNode();
        assertInstanceOf(UpdateNode.class, node1);
        ASTNode node2 = node1.getChild();
        assertInstanceOf(TableNode.class, node2);
        ASTNode node3 = node2.getChild();
        assertInstanceOf(ValueNode.class, node3);
        ASTNode node4 = node3.getChild();
        assertInstanceOf(ConditionNode.class, node4);
        ASTNode node5 = node4.getChild();
        assertInstanceOf(ComparisonNode.class, node5);
        parser.parse("UPDATE TABLE1 SET NAME='Anakin' WHERE JOB=='Jedi' AND SPECIES=='Human';");
        node1 = parser.getRootNode();
        assertInstanceOf(UpdateNode.class, node1);
        node2 = node1.getChild();
        assertInstanceOf(TableNode.class, node2);
        node3 = node2.getChild();
        assertInstanceOf(ValueNode.class, node3);
        node4 = node3.getChild();
        assertInstanceOf(ConditionNode.class, node4);
        node5 = node4.getChild();
        assertInstanceOf(BoolNode.class, node5);
    }

    @Test
    public void AlterASTTests() {
        parser.parse("ALTER TABLE Jedis DROP Lightsabers;");
        ASTNode node1 = parser.getRootNode();
        assertInstanceOf(AlterNode.class, node1);
        ASTNode node2 = node1.getChild();
        assertInstanceOf(TableNode.class, node2);
        ASTNode node3 = node2.getChild();
        assertInstanceOf(AttributeNode.class, node3);
        assertNull(node3.getChild());
        parser.parse("ALTER TABLE Clones ADD Blasters;");
        node1 = parser.getRootNode();
        assertInstanceOf(AlterNode.class, node1);
        node2 = node1.getChild();
        assertInstanceOf(TableNode.class, node2);
        node3 = node2.getChild();
        assertInstanceOf(AttributeNode.class, node3);
        assertNull(node3.getChild());
    }

    @Test
    public void InsertASTTests() {
        parser.parse("INSERT INTO Jedis VALUES ('Yoda', 'Green', 'Jedi');");
        ASTNode node1 = parser.getRootNode();
        assertInstanceOf(InsertNode.class, node1);
        ASTNode node2 = node1.getChild();
        assertInstanceOf(TableNode.class, node2);
        ASTNode node3 = node2.getChild();
        assertInstanceOf(ValueNode.class, node3);
        assertNull(node3.getChild());
    }

    @Test
    public void DeleteASTTests() {
        parser.parse("DELETE FROM Jedis WHERE JOB!='Jedi';");
        ASTNode node1 = parser.getRootNode();
        assertInstanceOf(DeleteNode.class, node1);
        ASTNode node2 = node1.getChild();
        assertInstanceOf(TableNode.class, node2);
        ASTNode node3 = node2.getChild();
        assertInstanceOf(ConditionNode.class, node3);
        ASTNode node4 = node3.getChild();
        assertInstanceOf(ComparisonNode.class, node4);
        parser.parse("DELETE FROM Jedis WHERE JOB!='Jedi' AND SPECIES=='Human';");
        node1 = parser.getRootNode();
        assertInstanceOf(DeleteNode.class, node1);
        node2 = node1.getChild();
        assertInstanceOf(TableNode.class, node2);
        node3 = node2.getChild();
        assertInstanceOf(ConditionNode.class, node3);
        node4 = node3.getChild();
        assertInstanceOf(BoolNode.class, node4);
    }

    @Test
    public void JoinASTTests() {
        parser.parse("JOIN Jedis AND Sith ON Job AND Species;");
        ASTNode node1 = parser.getRootNode();
        assertInstanceOf(JoinNode.class, node1);
        ASTNode node2 = node1.getChild();
        assertInstanceOf(TableNode.class, node2);
        ASTNode node3 = node2.getChild();
        assertInstanceOf(TableNode.class, node3);
        ASTNode node4 = node3.getChild();
        assertInstanceOf(AttributeNode.class, node4);
        ASTNode node5 = node4.getChild();
        assertInstanceOf(AttributeNode.class, node5);
        assertNull(node5.getChild());
    }

    @Test
    public void SelectASTTests() {
        parser.parse("SELECT * FROM Jedis WHERE JOB!='Jedi';");
        ASTNode node1 = parser.getRootNode();
        assertInstanceOf(SelectNode.class, node1);
        ASTNode node2 = node1.getChild();
        assertInstanceOf(AttributeNode.class, node2);
        ASTNode node3 = node2.getChild();
        assertInstanceOf(TableNode.class, node3);
        ASTNode node4 = node3.getChild();
        assertInstanceOf(ConditionNode.class, node4);
        ASTNode node5 = node4.getChild();
        assertInstanceOf(ComparisonNode.class, node5);
        parser.parse("SELECT * FROM Jedis WHERE JOB!='Jedi' AND SPECIES=='Human';");
        node1 = parser.getRootNode();
        assertInstanceOf(SelectNode.class, node1);
        node2 = node1.getChild();
        assertInstanceOf(AttributeNode.class, node2);
        node3 = node2.getChild();
        assertInstanceOf(TableNode.class, node3);
        node4 = node3.getChild();
        assertInstanceOf(ConditionNode.class, node4);
        node5 = node4.getChild();
        assertInstanceOf(BoolNode.class, node5);
        parser.parse("SELECT Name, Lightsaber FROM Jedis WHERE JOB!='Jedi';");
        node1 = parser.getRootNode();
        assertInstanceOf(SelectNode.class, node1);
        node2 = node1.getChild();
        assertInstanceOf(AttributeNode.class, node2);
        node3 = node2.getChild();
        assertInstanceOf(TableNode.class, node3);
        node4 = node3.getChild();
        assertInstanceOf(ConditionNode.class, node4);
        node5 = node4.getChild();
        assertInstanceOf(ComparisonNode.class, node5);
        parser.parse("SELECT Name, Lightsaber FROM Jedis WHERE JOB!='Jedi' AND SPECIES=='Human';");
        node1 = parser.getRootNode();
        assertInstanceOf(SelectNode.class, node1);
        node2 = node1.getChild();
        assertInstanceOf(AttributeNode.class, node2);
        node3 = node2.getChild();
        assertInstanceOf(TableNode.class, node3);
        node4 = node3.getChild();
        assertInstanceOf(ConditionNode.class, node4);
        node5 = node4.getChild();
        assertInstanceOf(BoolNode.class, node5);
    }
}

