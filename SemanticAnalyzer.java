import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticAnalyzer {
    private final Map<String, String> symbolTable = new HashMap<>();
    public void analyze(List<Node> statements) {
        for (Node stmt : statements) {
            analyzeStatement(stmt);
        }
    }
    private void analyzeStatement(Node node) {
        if (node instanceof LetStatement s) {
            analyzeLet(s);
        } else if (node instanceof PrintStatement s) {
            analyzePrint(s);
        } else if (node instanceof IfStatement s) {
            analyzeIf(s);
        } else if (node instanceof WhileStatement s) {
            analyzeWhile(s);
        }
    }

    private void analyzeLet(LetStatement s) {
        String type = inferType(s.value);
        symbolTable.put(s.name, type);
        System.out.println("  [SA] Variable '" + s.name + "' declared as " + type);
    }

    private void analyzePrint(PrintStatement s) {
        inferType(s.value);
    }

    private void analyzeIf(IfStatement s) {
        String condType = inferType(s.condition);
        if (!condType.equals("bool")) {
            throw new RuntimeException(
                    "If condition must be boolean, got " + condType
            );
        }
        for (Node stmt : s.body) analyzeStatement(stmt);
        for (Node stmt : s.elseBody) analyzeStatement(stmt);
    }

    private void analyzeWhile(WhileStatement s) {
        String condType = inferType(s.condition);
        if (!condType.equals("bool")) {
            throw new RuntimeException(
                    "While condition must be boolean, got " + condType
            );
        }
        for (Node stmt : s.body) analyzeStatement(stmt);
    }

    private String inferType(Node node) {
        if (node instanceof NumberLiteral) {
            return "number";
        }
        if (node instanceof StringLiteral) {
            return "string";
        }
        if (node instanceof BoolLiteral) {
            return "bool";
        }
        if (node instanceof Identifier id) {
            if (!symbolTable.containsKey(id.name)) {
                throw new RuntimeException(
                        "Undefined variable '" + id.name + "'"
                );
            }
            return symbolTable.get(id.name);
        }
        if (node instanceof BinaryOp op) {
            String leftType  = inferType(op.left);
            String rightType = inferType(op.right);
            if (op.op.equals("==") || op.op.equals("!=") ||
                    op.op.equals("<")  || op.op.equals(">")  ||
                    op.op.equals("<=") || op.op.equals(">=")) {
                if (!leftType.equals(rightType)) {
                    throw new RuntimeException(
                            "Cannot compare " + leftType + " with " + rightType
                    );
                }
                return "bool";
            }
            if (op.op.equals("+") || op.op.equals("-") ||
                    op.op.equals("*") || op.op.equals("/")) {
                if (op.op.equals("+") && leftType.equals("string")
                        && rightType.equals("string")) {
                    return "string";
                }

                if (!leftType.equals("number") || !rightType.equals("number")) {
                    throw new RuntimeException(
                            "Arithmetic requires numbers, got "
                                    + leftType + " and " + rightType
                    );
                }
                return "number";
            }
        }

        throw new RuntimeException("Unknown node type: " + node.getClass().getSimpleName());
    }
}
