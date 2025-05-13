package Parser;

import Nodes.*;
import static Parser.TokenType.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

    private Lexer lexer;
    private ASTNode rootNode;
    private final boolean advanceAfter = true;

    public Parser() { }

    // Get token value
    public void advance() { lexer.advance(); }
    public String getToken() { return lexer.getToken().getValue(); }
    public TokenType getTokenType() { return lexer.getToken().getTokenType(); }
    public ArrayList<Token> getTokens() { return lexer.getTokens(); }
    public ASTNode getRootNode() { return rootNode; }

    // Checking token values
    public boolean match(TokenType... expectedToken) {
        for (TokenType token : expectedToken) {
            if (getTokenType().equals(token)) {
                return true;
            }
        }
        return false;
    }

    public void expect(boolean advanceAfter, TokenType... expectedTokens) {
        for (TokenType expToken : expectedTokens) {
            if (expToken == NOT_NULL && getTokenType() != EOF) {
                throw new IllegalArgumentException("Unexpected end of query");
            }
            if (match(expToken)) {
                if (advanceAfter) { lexer.advance(); }
                return;
            }
        }
        throw new IllegalArgumentException("Expected " + Arrays.toString(expectedTokens) +
                " but recieved '" + getToken() + "'");
    }

    public void expect(TokenType... expectedToken) {
        expect(false, expectedToken);
    }

    public void parse(String query) {
        this.lexer = new Lexer();
        lexer.parseTokens(query);
        this.rootNode = parseCommand();
        expect(advanceAfter, S_COLON);
        expect(EOF);
    }

    public ASTNode parseCommand() {
        return switch (getTokenType()) {
            case USE -> parseUseCommand();
            case CREATE -> parseCreateCommand();
            case DROP -> parseDropCommand();
            case DELETE -> parseDeleteCommand();
            case ALTER -> parseAlterCommand();
            case INSERT -> parseInsertCommand();
            case SELECT -> parseSelectCommand();
            case UPDATE -> parseUpdateCommand();
            case JOIN -> parseJoinCommand();
            default -> throw new IllegalArgumentException("Command type not recognised");
        };
    }

    public ASTNode parseUseCommand() {
        expect(advanceAfter, USE);
        ASTNode node1 = new UseNode();
        expect(IDENTIFIER, INT);
        node1.addChild(new DatabaseNode(getToken()));
        advance();
        return node1;
    }

    public ASTNode parseCreateCommand() {
        expect(advanceAfter, CREATE);
        ASTNode node1 = new CreateNode();
        switch (getTokenType()) {
            case DATABASE:
                advance();
                expect(IDENTIFIER, INT);
                node1.addChild(new DatabaseNode(getToken()));
                advance();
                return node1;
            case TABLE:
                advance();
                expect(IDENTIFIER, INT);
                node1.addChild(new TableNode(getToken(), parseAttList()));
                return node1;
            default:
                throw new IllegalArgumentException("Expected either 'DATABASE' or 'TABLE' but recieved " + getToken());
        }
    }

    public ASTNode parseDropCommand() {
        expect(advanceAfter, DROP);
        ASTNode node1 = new DropNode();
        switch (getTokenType()) {
            case DATABASE:
                advance();
                expect(IDENTIFIER, INT);
                node1.addChild(new DatabaseNode(getToken()));
                advance();
                return node1;
            case TABLE:
                advance();
                expect(IDENTIFIER, INT);
                node1.addChild(new TableNode(getToken()));
                advance();
                return node1;
            default:
                throw new IllegalArgumentException("Expected either 'DATABASE' or 'TABLE' but recieved " + getToken());
        }
    }

    public ASTNode parseAlterCommand() {
        expect(advanceAfter, ALTER);
        ASTNode node1 = new AlterNode();
        expect(advanceAfter, TABLE);
        expect(IDENTIFIER, INT);
        ASTNode node2 = new TableNode(getToken());
        node1.addChild(node2);
        advance();
        ASTNode node3;
        switch (getTokenType()) {
            case ADD:
                advance();
                expect(IDENTIFIER, INT);
                node3 = new AttributeNode(getToken(), "ADD");
                node2.addChild(node3);
                advance();
                return node1;
            case DROP:
                advance();
                expect(IDENTIFIER, INT);
                node3 = new AttributeNode(getToken(), "DROP");
                node2.addChild(node3);
                advance();
                return node1;
            default:
                throw new IllegalArgumentException("Expected 'ADD' or 'DROP' but received " + getToken());
        }
    }

    public ASTNode parseDeleteCommand() {
        expect(advanceAfter, DELETE);
        ASTNode node1 = new DeleteNode();
        expect(advanceAfter, FROM);
        expect(IDENTIFIER, INT);
        ASTNode node2 = new TableNode(getToken());
        node1.addChild(node2);
        advance();
        expect(advanceAfter, WHERE);
        ASTNode node3 = parseCondition();
        node2.addChild(node3);
        return node1;
    }

    public ASTNode parseSelectCommand() {
        expect(advanceAfter, SELECT);
        ASTNode node1 = new SelectNode();
        ASTNode node2 = new AttributeNode(parseWildList());
        node1.addChild(node2);
        expect(advanceAfter, FROM);
        expect(IDENTIFIER, INT);
        ASTNode node3 = new TableNode(getToken());
        node2.addChild(node3);
        advance();
        if (match(WHERE)) {
            advance();
            ASTNode node4 = parseCondition();
            node3.addChild(node4);
        }
        return node1;
    }

    public ASTNode parseUpdateCommand() {
        expect(advanceAfter, UPDATE);
        ASTNode node1 = new UpdateNode();
        expect(IDENTIFIER, INT);
        ASTNode node2 = new TableNode(getToken());
        advance();
        node1.addChild(node2);
        expect(advanceAfter, SET);
        ASTNode node3 = new ValueNode(parseNameValueList());
        node2.addChild(node3);
        if (match(WHERE)) {
            advance();
            ASTNode node4 = parseCondition();
            node3.addChild(node4);
        }
        return node1;
    }

    public ASTNode parseInsertCommand() {
        expect(advanceAfter, INSERT);
        ASTNode node1 = new InsertNode();
        expect(advanceAfter, INTO);
        expect(IDENTIFIER, INT);
        ASTNode node2 = new TableNode(getToken());
        node1.addChild(node2);
        advance();
        expect(advanceAfter, VALUES);
        ASTNode node3 = new ValueNode(parseValueList());
        advance();
        node2.addChild(node3);
        return node1;
    }

    public ASTNode parseJoinCommand() {
        expect(advanceAfter, JOIN);
        ASTNode node1 = new JoinNode();
        expect(IDENTIFIER, INT);
        ASTNode node2 = new TableNode(getToken());
        node1.addChild(node2);
        advance();
        expect(advanceAfter, AND);
        expect(IDENTIFIER, INT);
        ASTNode node3 = new TableNode(getToken());
        node2.addChild(node3);
        advance();
        expect(advanceAfter, ON);
        expect(IDENTIFIER, INT);
        ASTNode node4 = new AttributeNode(getToken());
        node3.addChild(node4);
        advance();
        expect(advanceAfter, AND);
        expect(IDENTIFIER, INT);
        ASTNode node5 = new AttributeNode(getToken());
        node4.addChild(node5);
        advance();
        return node1;
    }

    public ArrayList<String> parseAttList() {
        advance();
        ArrayList<String> list = new ArrayList<>();
        if (match(L_PAREN)) {
            do {
                advance();
                expect(IDENTIFIER, INT);
                list.add(getToken());
                advance();
            } while (match(COMMA));
            expect(R_PAREN);
            advance();
        }
        return list;
    }

    public ArrayList<String> parseValueList() {
        ArrayList<String> valueList = new ArrayList<>();
        expect(L_PAREN);
        do {
            advance();
            if (!match(LITERAL, TRUE, FALSE, NULL, DOUBLE, INT)) {
                throw new IllegalArgumentException("Invalid token received: " + getToken());
            }
            valueList.add(getToken());
            advance();
        } while (match(COMMA));
        expect(R_PAREN);
        return valueList;
    }

    public ArrayList<String> parseNameValueList() {
        ArrayList<String> valueList = new ArrayList<>();
        expect(IDENTIFIER, INT);
        valueList.add(getToken());
        advance();
        expect(advanceAfter, EQ);
        expect(DOUBLE, INT, TRUE, FALSE, NULL, LITERAL);
        valueList.add(getToken());
        advance();
        while (match(COMMA)) {
            advance();
            expect(IDENTIFIER, INT);
            valueList.add(getToken());
            advance();
            expect(advanceAfter, EQ);
            expect(DOUBLE, INT, TRUE, FALSE, NULL, LITERAL);
            valueList.add(getToken());
            advance();
        }
        return valueList;
    }

    public ArrayList<String> parseWildList() {
        ArrayList<String> list = new ArrayList<>();
        if (match(WILD)) {
            advance();
            return list;
        }
        expect(IDENTIFIER, INT);
        list.add(getToken());
        advance();
        while (match(COMMA)) {
            advance();
            expect(IDENTIFIER, INT);
            list.add(getToken());
            advance();
        }
        return list;
    }

    public ASTNode parseCondition() {
        ASTNode condition = new ConditionNode();
        condition.addChild(parseBoolean());
        return condition;
    }

    public ASTNode parseBoolean() {
        ASTNode left;
        // If bracketed, start new boolean parse
        if (match(L_PAREN)) {
            advance();
            left = parseBoolean();
            expect(advanceAfter, R_PAREN);
        }
        else {
            left = parseComparison();
        }
        // Parse boolean expression
        if (match(AND, OR)) {
            String bool = getToken();
            advance();
            ASTNode right = parseCondition();
            return new BoolNode(left, bool, right);
        }
        return left;
    }

    public ASTNode parseComparison() {
        if (!match(IDENTIFIER, INT)) {
            throw new IllegalArgumentException("Invalid attribute: " + getToken());
        }
        String attribute = getToken();
        advance();
        if (!match(GT, GTE, LT, LTE, DEQ, NEQ, LIKE)) {
            throw new IllegalArgumentException("Invalid comparator: " + getToken());
        }
        String comparator = getToken();
        advance();
        if (!match(TRUE, FALSE, NULL, DOUBLE, LITERAL, INT)) {
            throw new IllegalArgumentException("Invalid value: " + getToken());
        }
        String value = getToken();
        advance();
        return new ComparisonNode(attribute, comparator, value);
    }
}

