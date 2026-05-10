import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
public class VM {

    private final List<Instruction> instructions;
    private final Stack<Object> stack = new Stack<>();
    private final Map<String, Object> variables = new HashMap<>();
    private int ip = 0;

    public VM(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public void run() {
        while (ip < instructions.size()) {
            Instruction instr = instructions.get(ip);
            ip++;

            switch (instr.op) {

                case PUSH -> stack.push(instr.operand);

                case STORE -> {
                    Object val = stack.pop();
                    variables.put((String) instr.operand, val);
                }

                case LOAD -> {
                    String name = (String) instr.operand;
                    if (!variables.containsKey(name)) {
                        throw new RuntimeException("Undefined variable: " + name);
                    }
                    stack.push(variables.get(name));
                }

                case ADD -> {
                    Object b = stack.pop();
                    Object a = stack.pop();
                    if (a instanceof String || b instanceof String) {
                        stack.push(a.toString() + b.toString());
                    } else {
                        stack.push(toDouble(a) + toDouble(b));
                    }
                }

                case SUB -> {
                    double b = toDouble(stack.pop());
                    double a = toDouble(stack.pop());
                    stack.push(a - b);
                }

                case MUL -> {
                    double b = toDouble(stack.pop());
                    double a = toDouble(stack.pop());
                    stack.push(a * b);
                }

                case DIV -> {
                    double b = toDouble(stack.pop());
                    double a = toDouble(stack.pop());
                    if (b == 0) throw new RuntimeException("Division by zero");
                    stack.push(a / b);
                }

                case EQ  -> { Object b=stack.pop(); stack.push(stack.pop().equals(b)); }
                case NEQ -> { Object b=stack.pop(); stack.push(!stack.pop().equals(b)); }
                case LT  -> { double b=toDouble(stack.pop()); stack.push(toDouble(stack.pop()) < b); }
                case GT  -> { double b=toDouble(stack.pop()); stack.push(toDouble(stack.pop()) > b); }
                case LTE -> { double b=toDouble(stack.pop()); stack.push(toDouble(stack.pop()) <= b); }
                case GTE -> { double b=toDouble(stack.pop()); stack.push(toDouble(stack.pop()) >= b); }

                case PRINT -> {
                    Object val = stack.pop();
                    if (val instanceof Double d && d == Math.floor(d)) {
                        System.out.println((long)(double) d);
                    } else {
                        System.out.println(val);
                    }
                }

                case JUMP -> ip = (int) instr.operand;

                case JUMP_IF_FALSE -> {
                    Object val = stack.pop();
                    if (val instanceof Boolean b && !b) {
                        ip = (int) instr.operand;
                    }
                }

                case HALT -> { return; }
            }
        }
    }
    private double toDouble(Object o) {
        if (o instanceof Double d) return d;
        if (o instanceof Boolean b) return b ? 1.0 : 0.0;
        throw new RuntimeException("Expected number, got: " + o);
    }
}
