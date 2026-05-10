public class Main {
    public static void main(String[] args) {

        String source = """
        x = 10 + 5
        name = "hello"
        if x > 3:
            print(x)
            print(name)
        i = 0
        while i < 3:
            print(i)
            i = i + 1
        """;

        Lexer lexer = new Lexer(source);
        var tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        var tree = parser.parse();
        SemanticAnalyzer sa = new SemanticAnalyzer();
        sa.analyze(tree);
        Compiler compiler = new Compiler();
        var bytecode = compiler.compile(tree);
        System.out.println("=== Bytecode ===");
        for (int i = 0; i < bytecode.size(); i++) {
            System.out.println(i + ": " + bytecode.get(i));
        }

        System.out.println("\n=== Output ===");
        VM vm = new VM(bytecode);
        vm.run();
    }
}