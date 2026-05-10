import java.util.List;

public abstract class Node {}
class LetStatement extends Node {
    public final String name;
    public final Node value;
    public LetStatement(String name, Node value) {
        this.name = name;
        this.value = value;
    }
}
class PrintStatement extends Node {
    public final Node value;
    public PrintStatement(Node value) {
        this.value = value;
    }
}
class IfStatement extends Node {
    public final Node condition;
    public final List<Node> body;
    public final List<Node> elseBody;
    public IfStatement(Node condition, List<Node> body, List<Node> elseBody) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }
}
class WhileStatement extends Node {
    public final Node condition;
    public final List<Node> body;
    public WhileStatement(Node condition, List<Node> body) {
        this.condition = condition;
        this.body = body;
    }
}
class BinaryOp extends Node {
    public final Node left;
    public final String op;
    public final Node right;
    public BinaryOp(Node left, String op, Node right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }
}
class NumberLiteral extends Node {
    public final double value;
    public NumberLiteral(double value) {
        this.value = value;
    }
}
class StringLiteral extends Node {
    public final String value;
    public StringLiteral(String value) {
        this.value = value;
    }
}
class BoolLiteral extends Node {
    public final boolean value;
    public BoolLiteral(boolean value) {
        this.value = value;
    }
}
class Identifier extends Node {
    public final String name;
    public Identifier(String name) {
        this.name = name;
    }
}
