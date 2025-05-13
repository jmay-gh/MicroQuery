package edu.uob;

import Parser.Lexer;
import Parser.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTests {

    private Lexer lexer;

    @BeforeEach
    public void setup() {
        lexer = new Lexer();
    }

    @Test
    public void SpacingTests() {
        // Assert lacking spacing fails reserved words
        String query = "CREATE DATABASEDB1;";
        lexer.parseTokens(query);
        String result = lexer.getTokens().stream().map(Token::getValue).collect(Collectors.joining(", "));
        assertEquals("CREATE, DATABASEDB1, ;, EOF", result);
        // Assert spacing doesn't affect other tokens
        query = "     CREATE    DATABASE   DB1  ;    ";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream().map(Token::getValue).collect(Collectors.joining(", "));
        assertEquals("CREATE, DATABASE, DB1, ;, EOF", result);
        // Assert spacing required for attributes
        query = "SELECT*FROM TABLE1;";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream().map(Token::getValue).collect(Collectors.joining(", "));
        assertEquals("SELECT*FROM, TABLE1, ;, EOF", result);
        // Assert spacing doesn't affect conditions
        query = "SELECT * FROM TABLE1 WHERE (NAME==JACK)AND(AGE<=5);";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream().map(Token::getValue).collect(Collectors.joining(", "));
        assertEquals("SELECT, *, FROM, TABLE1, WHERE, (, NAME, ==, JACK, ), AND, (, AGE, <=, 5, ), ;, EOF", result);
        // Assert spacing doesn't affect conditions
        query = "SELECT * FROM TABLE1 WHERE (   NAME   ==  JACK  )    AND(  AGE<=  5);";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream().map(Token::getValue).collect(Collectors.joining(", "));
        assertEquals("SELECT, *, FROM, TABLE1, WHERE, (, NAME, ==, JACK, ), AND, (, AGE, <=, 5, ), ;, EOF", result);
        // Assert spacing required for booleans without brackets
        query = "SELECT * FROM TABLE1 WHERE NAME==JACKANDAGE<=5;";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream().map(Token::getValue).collect(Collectors.joining(", "));
        assertEquals("SELECT, *, FROM, TABLE1, WHERE, NAME, ==, JACKANDAGE, <=, 5, ;, EOF", result);
    }

    @Test
    public void DoubleTests() {
        // Assert positive double accepted
        String query = "+0.5";
        lexer.parseTokens(query);
        String result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("DOUBLE, EOF", result);
        // Assert negative double accepted
        query = "-0.5";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("DOUBLE, EOF", result);
        // Assert numbers required before
        query = ".5";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("UNIDENTIFIED, EOF", result);
        // Assert numbers required after
        query = "0.";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("UNIDENTIFIED, EOF", result);
        // Assert all digits allowed before and after
        query = "0123456789.0123456789";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("DOUBLE, EOF", result);
    }

    @Test
    public void IntegerTests() {
        // Assert positive double accepted
        String query = "+0";
        lexer.parseTokens(query);
        String result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("INT, EOF", result);
        // Assert negative double accepted
        query = "-0";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("INT, EOF", result);
        // Assert all digits allowed
        query = "0123456789";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("INT, EOF", result);
    }

    @Test
    public void LiteralTests() {
        // Assert characters in literals are accepted
        String query = "'^AS$ER%T_' 'CH#@ARA-(C)TE=RS+' '!IN\\' '-LI/TE.RA,LS' 'AR;E' 'A`C<C>E[P]T{E}D:'";
        lexer.parseTokens(query);
        String result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("LITERAL, LITERAL, LITERAL, LITERAL, " +
                "LITERAL, LITERAL, EOF", result);
        // Assert empty string literals are allowed
        query = "''";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("LITERAL, EOF", result);
        // Assert " doesn't work
        query = "SELECT * FROM TABLE1 WHERE NAME==\"BEN\"";
        String finalQuery1 = query;
        assertThrows(IllegalArgumentException.class, () -> lexer.parseTokens(finalQuery1));
        // Assert literal can be at end of query without removal from split
        query = "SELECT * FROM TABLE1 WHERE NAME=='BEN'";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("SELECT, WILD, FROM, IDENTIFIER, WHERE, IDENTIFIER, DEQ, LITERAL, EOF", result);
    }

    @Test
    public void UnidentifierTests() {
        // Assert characters in identifiers are unidentified
        String query = "AS$SE#RT@ C]HA.R%AC^TERS &IN\\ ID*ENT-IFI+ERS +ARE -UNI[DE`NT{IF}IED~:";
        lexer.parseTokens(query);
        String result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("UNIDENTIFIED, UNIDENTIFIED, UNIDENTIFIED, " +
                "UNIDENTIFIED, UNIDENTIFIED, UNIDENTIFIED, EOF", result);
    }

    @Test
    public void CaseTests() {
        // Assert all lower case is accepted
        String query = "use create drop alter insert " +
                "select update delete join table database " +
                "and on true false null add into where values " +
                "set or";
        lexer.parseTokens(query);
        String result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("USE, CREATE, DROP, ALTER, INSERT, " +
                "SELECT, UPDATE, DELETE, JOIN, TABLE, DATABASE, " +
                "AND, ON, TRUE, FALSE, NULL, ADD, INTO, WHERE, VALUES, " +
                "SET, OR, EOF", result);
        // Assert mixed case is accepted
        query = "uSe CrEatE DrOp AlTeR InSeRt " +
                "sEleCt UpDatE DeLeTe JoIn TabLe DatAbaSe " +
                "aNd On trUe faLse NulL aDd IntO wHeRE VAluEs " +
                "sEt oR";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("USE, CREATE, DROP, ALTER, INSERT, " +
                "SELECT, UPDATE, DELETE, JOIN, TABLE, DATABASE, " +
                "AND, ON, TRUE, FALSE, NULL, ADD, INTO, WHERE, VALUES, " +
                "SET, OR, EOF", result);
    }

    @Test
    public void InvalidTests() {
        // Assert |, " are banned
        String query = "SELECT * FROM TABLE1 WHERE NAME=='BE|N';";
        String finalQuery1 = query;
        assertThrows(IllegalArgumentException.class, () -> lexer.parseTokens(finalQuery1));
        query = "SELECT * FROM TAB|LE1 WHERE NAME=='BEN';";
        String finalQuery2 = query;
        assertThrows(IllegalArgumentException.class, () -> lexer.parseTokens(finalQuery2));
        query = "SELECT * FROM TABLE1 WHERE NAME=='BE|N';";
        String finalQuery3 = query;
        assertThrows(IllegalArgumentException.class, () -> lexer.parseTokens(finalQuery3));
        query = "SELECT * FROM TAB\"LE1 WHERE NAME=='BEN';";
        String finalQuery4 = query;
        assertThrows(IllegalArgumentException.class, () -> lexer.parseTokens(finalQuery4));
        // Assert incomplete string literal '
        query = "SELECT * FROM TABLE1 WHERE NAME=='BEN;";
        String finalQuery5 = query;
        assertThrows(IllegalArgumentException.class, () -> lexer.parseTokens(finalQuery5));
        query = "SELECT * F'ROM TABLE'1 WHERE NAME=='BEN;";
        String finalQuery6 = query;
        assertThrows(IllegalArgumentException.class, () -> lexer.parseTokens(finalQuery6));
    }

    @Test
    public void EmptyTests() {
        // Assert empty input returns just EOF
        String query = "";
        lexer.parseTokens(query);
        String result = lexer.getTokens().stream().map(Token::getValue).collect(Collectors.joining(", "));
        assertEquals("EOF", result);
        // Assert spaced input returns just EOF
        query = "     ";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream().map(Token::getValue).collect(Collectors.joining(", "));
        assertEquals("EOF", result);
        // Assert tab input returns just EOF
        query = "\t";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream().map(Token::getValue).collect(Collectors.joining(", "));
        assertEquals("EOF", result);
    }

    @Test
    public void ComparatorTests() {
        // Assert == is read as ==
        String query = "==";
        lexer.parseTokens(query);
        String result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("DEQ, EOF", result);
        // Assert = = is read as 2x =
        query = "= =";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("EQ, EQ, EOF", result);
        // Assert >= is read as >=
        query = ">=";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("GTE, EOF", result);
        // Assert > = is read as > and =
        query = "> =";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("GT, EQ, EOF", result);
        // Assert <= is read as <=
        query = "<=";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("LTE, EOF", result);
        // Assert < = is read as < and =
        query = "< =";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("LT, EQ, EOF", result);
        // Assert >== is read as >= and =
        query = ">==";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("GTE, EQ, EOF", result);
        // Assert === is read as == and =
        query = "===";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("DEQ, EQ, EOF", result);
    }

    @Test
    public void ConditionTests() {
        String query = "(NAME == 'BEN');";
        lexer.parseTokens(query);
        String result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("L_PAREN, IDENTIFIER, DEQ, LITERAL, R_PAREN, S_COLON, EOF", result);
        query = "(NAME == 'BEN') AND (AGE!=NULL);";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("L_PAREN, IDENTIFIER, DEQ, LITERAL, R_PAREN, AND, L_PAREN, " +
                "IDENTIFIER, NEQ, NULL, R_PAREN, S_COLON, EOF", result);
        query = "(NAME=='BEN')AND(AGE!=-0.2);";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("L_PAREN, IDENTIFIER, DEQ, LITERAL, R_PAREN, AND, L_PAREN, " +
                "IDENTIFIER, NEQ, DOUBLE, R_PAREN, S_COLON, EOF", result);
        query = "(NAME==TRUE)AND(AGE!=FALSE);";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("L_PAREN, IDENTIFIER, DEQ, TRUE, R_PAREN, AND, L_PAREN, " +
                "IDENTIFIER, NEQ, FALSE, R_PAREN, S_COLON, EOF", result);
        query = "(NAME>= - 3)AND(AGE!= -4.3);";
        lexer.parseTokens(query);
        result = lexer.getTokens().stream()
                .map(token -> token.getTokenType().toString())
                .collect(Collectors.joining(", "));
        assertEquals("L_PAREN, IDENTIFIER, GTE, UNIDENTIFIED, INT, R_PAREN, AND, L_PAREN, " +
                "IDENTIFIER, NEQ, DOUBLE, R_PAREN, S_COLON, EOF", result);
    }
}

