package Parser;

public class Token {

    private final String value;
    private final TokenType tokenType;

    public Token(TokenType tokenType, String value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    public TokenType getTokenType() { return tokenType; }
    public String getValue() { return value; }
}
