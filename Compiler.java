import java.util.ArrayList;
import java.util.List;

public class Compiler {
    private final List<Instruction> instructions = new ArrayList<>();
    private void emit(Instruction.Op op) {
        instructions.add(new Instruction(op));
    }

    private void emit(Instruction.Op op, Object operand) {
        instructions.add(new Instruction(op, operand));
    }
    private int currentPos() {
        return instructions.size();
    }
    public List<Instruction> compile(List<Node> statements) {
        for (Node stmt : statements) {
            compileStatement(stmt);
        }
        emit(Instruction.Op.HALT);
        return instructions;
    }
    private void compileStatement(Node node) {
        if (node instanceof LetStatement s)   compileLet(s);
        else if (node instanceof PrintStatement s) compilePrint(s);
        else if (node instanceof IfStatement s)    compileIf(s);
        else if (node instanceof WhileStatement s) compileWhile(s);
    }

    private void compileLet(LetStatement s) {
        compileExpression(s.value);
        emit(Instruction.Op.STORE, s.name);
    }

    private void compilePrint(PrintStatement s) {
        compileExpression(s.value);
        emit(Instruction.Op.PRINT);
    }

    private void compileIf(IfStatement s) {
        compileExpression(s.condition);
        int jumpIfFalsePos = currentPos();
        emit(Instruction.Op.JUMP_IF_FALSE, -1);
        for (Node stmt : s.body) compileStatement(stmt);
        int jumpPos = currentPos();
        if (!s.elseBody.isEmpty()) {
            emit(Instruction.Op.JUMP, -1);
        }
        instructions.set(jumpIfFalsePos,
                new Instruction(Instruction.Op.JUMP_IF_FALSE, currentPos()));
        if (!s.elseBody.isEmpty()) {
            for (Node stmt : s.elseBody) compileStatement(stmt);
            instructions.set(jumpPos,
                    new Instruction(Instruction.Op.JUMP, currentPos()));
        }
    }

    private void compileWhile(WhileStatement s) {
        int loopStart = currentPos();
        compileExpression(s.condition);
        int jumpIfFalsePos = currentPos();
        emit(Instruction.Op.JUMP_IF_FALSE, -1);
        for (Node stmt : s.body) compileStatement(stmt);
        emit(Instruction.Op.JUMP, loopStart);
        instructions.set(jumpIfFalsePos,
                new Instruction(Instruction.Op.JUMP_IF_FALSE, currentPos()));
    }

    private void compileExpression(Node node) {
        if (node instanceof NumberLiteral n) {
            emit(Instruction.Op.PUSH, n.value);

        } else if (node instanceof StringLiteral n) {
            emit(Instruction.Op.PUSH, n.value);

        } else if (node instanceof BoolLiteral n) {
            emit(Instruction.Op.PUSH, n.value);

        } else if (node instanceof Identifier n) {
            emit(Instruction.Op.LOAD, n.name);

        } else if (node instanceof BinaryOp n) {
            compileExpression(n.left);
            compileExpression(n.right);
            switch (n.op) {
                case "+"  -> emit(Instruction.Op.ADD);
                case "-"  -> emit(Instruction.Op.SUB);
                case "*"  -> emit(Instruction.Op.MUL);
                case "/"  -> emit(Instruction.Op.DIV);
                case "==" -> emit(Instruction.Op.EQ);
                case "!=" -> emit(Instruction.Op.NEQ);
                case "<"  -> emit(Instruction.Op.LT);
                case ">"  -> emit(Instruction.Op.GT);
                case "<=" -> emit(Instruction.Op.LTE);
                case ">=" -> emit(Instruction.Op.GTE);
            }
        }
    }
}
