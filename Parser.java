import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int pos = 0;   // current token index

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    private Token current() {
        return tokens.get(pos);
    }
    private Token advance() {
        Token t = tokens.get(pos);
        pos++;
        return t;
    }
    private boolean check(TokenType type) {
        return current().type == type;
    }
    private boolean check(TokenType type, String value) {
        return current().type == type && current().value.equals(value);
    }
    private boolean match(TokenType type, String value) {
        if (check(type, value)) { advance(); return true; }
        return false;
    }
    private Token expect(TokenType type, String value) {
        if (!check(type, value)) {
            throw new RuntimeException(
                    "Line " + current().line +
                            ": Expected '" + value + "' but got '" + current().value + "'"
            );
        }
        return advance();
    }
    private void skipNewlines() {
        while (check(TokenType.NEWLINE)) advance();
    }
    public List<Node> parse() {
        List<Node> statements = new ArrayList<>();
        skipNewlines();
        while (!check(TokenType.EOF)) {
            statements.add(parseStatement());
            skipNewlines();
        }
        return statements;
    }
    private Node parseStatement() {
        if (check(TokenType.KEYWORD, "if"))    return parseIf();
        if (check(TokenType.KEYWORD, "while")) return parseWhile();
        if (check(TokenType.KEYWORD, "print")) return parsePrint();
        if (check(TokenType.IDENT)) {
            Token ident = advance();
            if (check(TokenType.ASSIGN, "=")) {
                return parseAssignment(ident);
            }
            throw new RuntimeException(
                    "Line " + ident.line + ": Expected '=' after identifier"
            );
        }

        throw new RuntimeException(
                "Line " + current().line + ": Unexpected '" + current().value + "'"
        );
    }
    private Node parseAssignment(Token identToken) {
        advance();
        Node value=parseExpression();
        return new LetStatement(identToken.value, value);
    }
    private Node parsePrint() {
        advance();
        expect(TokenType.LPAREN, "(");
        Node value = parseExpression();
        expect(TokenType.RPAREN, ")");
        return new PrintStatement(value);
    }
    private Node parseIf() {
        advance();
        Node condition = parseExpression();
        expect(TokenType.COLON, ":");
        List<Node> body = parseBlock();
        List<Node> elseBody = new ArrayList<>();
        skipNewlines();
        if (match(TokenType.KEYWORD, "else")) {
            expect(TokenType.COLON, ":");
            elseBody = parseBlock();
        }
        return new IfStatement(condition, body, elseBody);
    }
    private Node parseWhile() {
        advance();
        Node condition = parseExpression();
        expect(TokenType.COLON, ":");
        List<Node> body = parseBlock();
        return new WhileStatement(condition, body);
    }
    private List<Node> parseBlock() {
        List<Node> stmts = new ArrayList<>();
        skipNewlines();
        if (!check(TokenType.INDENT)) {
            throw new RuntimeException("Expected indented block");
        }
        advance();
        skipNewlines();
        while (!check(TokenType.DEDENT) && !check(TokenType.EOF)) {
            stmts.add(parseStatement());
            skipNewlines();
        }
        if (check(TokenType.DEDENT)) advance();
        return stmts;
    }
    private Node parseExpression() {
        return parseComparison();
    }
    private Node parseComparison() {
        Node left = parseAddSub();
        while (current().type == TokenType.EQ  || current().type == TokenType.NEQ ||
                current().type == TokenType.LT  || current().type == TokenType.GT  ||
                current().type == TokenType.LTE || current().type == TokenType.GTE) {
            String op = current().value;
            advance();
            Node right = parseAddSub();
            left = new BinaryOp(left, op, right);
        }
        return left;
    }
    private Node parseAddSub() {
        Node left = parseMulDiv();
        while (current().type == TokenType.PLUS || current().type == TokenType.MINUS) {
            String op = current().value;
            advance();
            Node right = parseMulDiv();
            left = new BinaryOp(left, op, right);
        }
        return left;
    }
    private Node parseMulDiv() {
        Node left = parsePrimary();
        while (current().type == TokenType.STAR || current().type == TokenType.SLASH) {
            String op = current().value;
            advance();
            Node right = parsePrimary();
            left = new BinaryOp(left, op, right);
        }
        return left;
    }
    private Node parsePrimary() {
        Token t = current();
        if (t.type == TokenType.NUMBER) {
            advance();
            return new NumberLiteral(Double.parseDouble(t.value));
        }
        if (t.type == TokenType.STRING) {
            advance();
            return new StringLiteral(t.value);
        }
        if (t.type == TokenType.KEYWORD && t.value.equals("true")) {
            advance(); return new BoolLiteral(true);
        }
        if (t.type == TokenType.KEYWORD && t.value.equals("false")) {
            advance(); return new BoolLiteral(false);
        }
        if (t.type == TokenType.IDENT) {
            advance();
            return new Identifier(t.value);
        }
        if (t.type == TokenType.LPAREN) {
            advance();
            Node expr = parseExpression();
            expect(TokenType.RPAREN, ")");
            return expr;
        }
        throw new RuntimeException(
                "Line " + t.line + ": Unexpected token '" + t.value + "'"
        );
    }
}