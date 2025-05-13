package Visitors;

import DataStuctures.Row;
import Nodes.*;

import java.util.ArrayList;

public class ConditionVisitor implements ASTVisitor {

    private ArrayList<String> headers;
    private Row row;

    public void setHeaders(ArrayList<String> headers) { this.headers = headers; }
    public void setRow(Row row) { this.row = row; }

    @Override
    public Boolean visit(ConditionNode conditionNode) {
        return conditionNode.getChild().accept(this);
    }

    @Override
    public Boolean visit(BoolNode boolNode) {
        Boolean left = boolNode.getLeft().accept(this);
        Boolean right = boolNode.getRight().accept(this);

        // Return boolean evaluation
        return switch (boolNode.getOp()) {
            case "AND" -> left && right;
            case "OR" -> left || right;
            default -> throw new IllegalArgumentException("Invalid boolean operator: " + boolNode.getOp());
        };
    }

    @Override
    public Boolean visit(ComparisonNode compNode) {
        // Get correct header (case matched), else toss out if doesn't exist
        String matchedHeader = null;
        for (String header : headers) {
            if (header.equalsIgnoreCase(compNode.getLeft())) {
                matchedHeader = header;
                break;
            }
        }
        if (matchedHeader == null) throw new IllegalArgumentException("Attribute '" + compNode.getLeft() + "' does not exist");

        // Get primary values of comparison node
        String strLeft = row.getData(matchedHeader);
        String strRight = compNode.getRight();

        // Compare ints, strings is possible else return false
        if (checkInt(strLeft) && checkInt(strRight)) {
            return compareInts(Double.parseDouble(strLeft), Double.parseDouble(strRight), compNode.getOp());
        } else if (!checkInt(strLeft) && !checkInt(strRight)) {
            return compareStrings(strLeft, strRight, compNode.getOp());
        } else {
            return false;
        }
    }

    public boolean checkInt(String value) {
        try {
            Double.parseDouble(value);
            return true;
        }
        catch (Exception error) {
            return false;
        }
    }

    public boolean compareInts(Double left, Double right, String comparator) {
        return switch (comparator) {
            case "==" -> left.equals(right);
            case "!=" -> !left.equals(right);
            case "<=" -> left <= right;
            case ">=" -> left >= right;
            case ">" -> left > right;
            case "<" -> left < right;
            case "LIKE" -> left.toString().contains(right.toString());
            default -> false;
        };
    }

    public boolean compareStrings(String left, String right, String comparator) {
        String leftLower = left.toLowerCase();
        String rightLower = right.toLowerCase();
        return switch (comparator) {
            case "==" -> leftLower.equals(rightLower);
            case "!=" -> !leftLower.equals(rightLower);
            case "LIKE" -> {
                if (rightLower.startsWith("'") && rightLower.endsWith("'")) {
                    rightLower = rightLower.substring(1, rightLower.length() - 1);
                }
                yield leftLower.contains(rightLower);
            }
            default -> false;
        };
    }

    @Override
    public String getResult() { return ""; }
}
