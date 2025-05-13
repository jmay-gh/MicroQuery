package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Parser.TokenType.*;

public class Lexer {

    private ArrayList<Token> tokens;
    private int tokenIndex;
    private int inputIndex;

    public Lexer() {
        this.tokens = new ArrayList<>();
        this.tokenIndex = 0;
    }

    public void parseTokens(String query) {
        tokens.clear();
        // Ensure ' are matched
        checkLiterals(query);
        // Split on ' for string literals
        String[] fragments = query.trim().split("'", -1);
        for (int i = 0; i < fragments.length; i++) {
            if (i%2 == 0) {
                // Split and iterate non-string literal input and add tokens
                if (fragments[i].trim().isEmpty()) continue;
                List<String> input = splitTokens(fragments[i]);
                iterateTokens(input);
            }
            else {
                // Check string literal valid and add
                checkValidity(fragments[i]);
                tokens.add(new Token(LITERAL, "'" + fragments[i] + "'"));
            }
        }
        tokens.add(new Token(EOF, "EOF"));
    }

    public List<String> splitTokens(String input) {
        String[] specialChars = new String[]{"(", ")", ",", ";", "<=", ">=", "==", "!="};
        // Blind replace spaced special chars
        for (String specialChar : specialChars) {
            if (input.contains(specialChar)) {
                input = input.replaceAll("\\" + specialChar, " " + specialChar + " ");
            }
        }
        input = input.replaceAll("(?<![<>!=])=(?!=)", " = ");
        input = input.replaceAll("(>(?!=))", " > ");
        input = input.replaceAll("(<(?!=))", " < ");
        // Blind replace multiple spaces and return split list
        input = input.trim().replaceAll("\\s+", " ");
        return Arrays.asList(input.split(" "));
    }
    
    public void iterateTokens(List<String> input) {
        inputIndex = 0;
        while (inputIndex < input.size()) {
            tokens.add(scanTokens(input));
            inputIndex++;
        }
    }
    
    public Token scanTokens(List<String> input) {
        String token = input.get(inputIndex);
        return switch (input.get(inputIndex).toUpperCase()) {
            case "CREATE" -> new Token(CREATE, "CREATE");
            case "SELECT" -> new Token(SELECT, "SELECT");
            case "ALTER" -> new Token(ALTER, "ALTER");
            case "INSERT" -> new Token(INSERT, "INSERT");
            case "UPDATE" -> new Token(UPDATE, "UPDATE");
            case "DELETE" -> new Token(DELETE, "DELETE");
            case "VALUES" -> new Token(VALUES, "VALUES");
            case "JOIN" -> new Token(JOIN, "JOIN");
            case "USE" -> new Token(USE, "USE");
            case "DROP" -> new Token(DROP, "DROP");
            case "ADD" -> new Token(ADD, "ADD");
            case "FROM" -> new Token(FROM, "FROM");
            case "WHERE" -> new Token(WHERE, "WHERE");
            case "ON" -> new Token(ON, "ON");
            case "AND" -> new Token(AND, "AND");
            case "OR" -> new Token(OR, "OR");
            case "DATABASE" -> new Token(DATABASE, "DATABASE");
            case "TABLE" -> new Token(TABLE, "TABLE");
            case "INTO" -> new Token(INTO, "INTO");
            case "SET" -> new Token(SET, "SET");
            case "LIKE" -> new Token(LIKE, "LIKE");
            case "TRUE" -> new Token(TRUE, "TRUE");
            case "FALSE" -> new Token(FALSE, "FALSE");
            case "NULL" -> new Token(NULL, "FALSE");
            case "(" -> new Token(L_PAREN, "(");
            case ")" -> new Token(R_PAREN, ")");
            case ";" -> new Token(S_COLON, ";");
            case "*" -> new Token(WILD, "*");
            case "," -> new Token(COMMA, ",");
            case ">=" -> new Token(GTE, ">=");
            case "<=" -> new Token(LTE, "<=");
            case "!=" -> new Token(NEQ, "!=");
            case "==" -> new Token(DEQ, "==");
            case ">" -> new Token(GT, ">");
            case "<" -> new Token(LT, "<");
            case "=" -> new Token(EQ, "=");
//            case ">" -> (matchNext("=", input)) ? new Token(GTE, ">=") : new Token(GT, ">");
//            case "<" -> (matchNext("=", input)) ? new Token(LTE, "<=") : new Token(LT, "<");
//            case "!" -> (matchNext("=", input)) ? new Token(NEQ, "!=") : new Token(IDENTIFIER, "!");
//            case "=" -> (matchNext("=", input)) ? new Token(DEQ, "==") : new Token(EQ, "=");
            default -> scanRemaining(token);
        };
    }

    public Token scanRemaining(String input) {
        // Check for integers or doubles
        if (input.matches("[+-]?[0-9]+")) return new Token(INT, input);
        if (input.matches("[+-]?[0-9]+\\.?[0-9]+")) return new Token(DOUBLE, input);
        // Check for identifier
        if (input.chars().allMatch(Character::isLetterOrDigit)) return new Token(IDENTIFIER, input);
        checkValidity(input);
        return new Token(UNIDENTIFIED, input);
    }

    public void checkValidity(String input) {
        String[] disallowedChars = new String[]{"|", "'", "\""};
        for (String disallowedChar : disallowedChars) {
            if (input.contains(disallowedChar)) {
                throw new IllegalArgumentException("Invalid character found: " + disallowedChar);
            }
        }
    }

    public void checkLiterals(String input) {
        int count = 0;
        for (char ch : input.toCharArray()) {
            if (ch == '\'') count++;
        }
        if (count%2 != 0) throw new IllegalArgumentException("Unmatched ' found in query");
    }

    public void advance() { tokenIndex++; }
    public Token getToken() {
        return tokenIndex < tokens.size() ? tokens.get(tokenIndex) : null;
    }
    public ArrayList<Token> getTokens() { return tokens; }
}
