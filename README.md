# Python Subset Compiler

A compiler for a Python-subset language, built from scratch in Java.
Implements every stage of a real compiler pipeline with zero external libraries.

## Architecture
Source Code → Lexer → Parser → Semantic Analyzer → Compiler → VM → Output

## Pipeline

- **Lexer** — Tokenizes source code including Python-style indentation (INDENT/DEDENT tokens)
- **Parser** — Recursive descent parser builds an Abstract Syntax Tree
- **Semantic Analyzer** — Type checking and symbol table for undefined variable detection
- **Bytecode Compiler** — Walks AST and emits stack-based bytecode instructions
- **Virtual Machine** — Stack-based VM executes bytecode with support for jumps and loops

## Features

- Python-style syntax — indentation-based blocks, no braces
- Variables and arithmetic — `x = 10 + 5 * 2`
- Conditionals — `if / else`
- Loops — `while`
- Type checking — catches undefined variables and type mismatches at compile time
- String and number types with concatenation support

## Example

Input:
```python
x = 10 + 5
if x > 3:
    print(x)
i = 0
while i < 3:
    print(i)
    i = i + 1
```

Output:
15
0
1
2

## How to Run

```bash
javac *.java
java Main
```

## What I Learned

- How lexers handle indentation using a stack-based INDENT/DEDENT system
- Recursive descent parsing and operator precedence
- AST design using inheritance and the visitor pattern
- How stack-based virtual machines execute bytecode
- Type inference and symbol table design
