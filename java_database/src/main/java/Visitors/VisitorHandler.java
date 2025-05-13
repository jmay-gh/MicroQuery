package Visitors;

import DataStuctures.DBHandler;
import Parser.Token;

import java.util.ArrayList;

public class VisitorHandler {

    public ASTVisitor selectVisitor(ArrayList<Token> tokens, DBHandler dbHandler) {
        return switch (tokens.get(0).getTokenType()) {
            case USE -> new UseVisitor(dbHandler);
            case CREATE -> new CreateVisitor(dbHandler);
            case DROP -> new DropVisitor(dbHandler);
            case ALTER -> new AlterVisitor(dbHandler);
            case INSERT -> new InsertVisitor(dbHandler);
            case SELECT -> new SelectVisitor(dbHandler);
            case UPDATE -> new UpdateVisitor(dbHandler);
            case DELETE -> new DeleteVisitor(dbHandler);
            case JOIN -> new JoinVisitor(dbHandler);
            default -> throw new IllegalArgumentException("Visitor not recognised");
        };
    }
}

