package Parser;

public enum TokenType {
    // Commands
    USE, CREATE, DROP, ALTER, INSERT, SELECT, UPDATE, DELETE, JOIN,
    // Keywords
    DATABASE, TABLE, VALUES, INTO, FROM, WHERE, ON, SET, ADD,
    // Special chars
    L_PAREN, R_PAREN, S_COLON, WILD, COMMA,
    // Comparators
    EQ, DEQ, NEQ, GT, GTE, LT, LTE, LIKE, AND, OR,
    // Values
    IDENTIFIER, TRUE, FALSE, NULL, DOUBLE, LITERAL, INT,
    // Other
    EOF, UNIDENTIFIED, NOT_NULL
}
