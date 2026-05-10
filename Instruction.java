public class Instruction {
    public enum Op {
        PUSH,
        STORE,
        LOAD,
        ADD,
        SUB,
        MUL,
        DIV,
        EQ,
        NEQ,
        LT,
        GT,
        LTE,
        GTE,
        PRINT,
        JUMP,
        JUMP_IF_FALSE,
        HALT
    }

    public final Op op;
    public final Object operand;
    public Instruction(Op op, Object operand) {
        this.op = op;
        this.operand = operand;
    }
    public Instruction(Op op) {
        this(op, null);
    }

    @Override
    public String toString() {
        return operand != null ? op + " " + operand : op.toString();
    }
}
