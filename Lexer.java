import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class Lexer{
  private String source;
  private int line=1;
  private int pos=0;

  private final List<Token> tokens = new ArrayList<>();
  private final java.util.Stack<Integer> indentStack=new java.util.Stack<>();
  private int currentIndent=0;
  private static final Set<String> KEYWORDS = Set.of(
          "if", "else", "while", "print",
          "def", "return", "true", "false"
  );

  public Lexer(String source){
    this.source=source;
  }
  private char curr(){
    if(pos<source.length()) return source.charAt(pos);
    return 0;
  }
  private char peek(){
    if(pos+1<source.length()) return source.charAt(pos+1);
    return 0;
  }
    private char advance(){
        if(pos >= source.length()) return 0;
        char ch=source.charAt(pos);
        pos++;
        if(ch=='\n'){
            line++;
        }
        return ch;
    }
  private void addToken(TokenType type,String value){
    tokens.add(new Token(type,value,line));
  }

  private void readNumber(){
    int start=pos;
    while(curr()!=0 && (Character.isDigit(curr()) || curr()=='.')){
      advance();
    }
    addToken(TokenType.NUMBER,source.substring(start,pos));

  }
  private void readString(){
    advance();
    int start=pos;
    while(curr()!=0 && curr()!='"'){
      advance();
    }
    if(curr()==0){
      throw new RuntimeException("Line "+line+": Unterminated String literal");

    }
    String value=source.substring(start,pos);
    advance();
    addToken(TokenType.STRING,value);
  }
  private void readIdentOrKeyword(){
    int start = pos;
    while (curr() != 0 && (Character.isLetterOrDigit(curr()) || curr() == '_')) {
      advance();
    }
    String word = source.substring(start, pos);
    TokenType type = KEYWORDS.contains(word) ? TokenType.KEYWORD : TokenType.IDENT;
    addToken(type, word);
  }
  private void handleIndent(int spaces){
      if(indentStack.isEmpty()){
          indentStack.push(0);
      }
      if(spaces>indentStack.peek()){
          indentStack.push(spaces);
          addToken(TokenType.INDENT,"INDENT");
      }else {
          while (!indentStack.isEmpty() && spaces < indentStack.peek()) {
              indentStack.pop();
              addToken(TokenType.DEDENT, "DEDENT");
          }
      }
  }
  public List<Token> tokenize(){
    while(pos<source.length()){
      char ch=curr();
      if(ch==' '||ch=='\t'){
        advance();
        continue;
      }
      if(ch=='#'){
        while(curr()!=0 && curr()!='\n') advance();
        continue;
      }
      if(ch=='\n'){
        addToken(TokenType.NEWLINE,"\\n");
        advance();
        int spaces=0;
        while(curr()==' '){
            spaces++;
            advance();
        }
        handleIndent(spaces);
        continue;
      }
      if(Character.isDigit(ch)){
        readNumber();
        continue;
      }
      if(Character.isLetterOrDigit(ch)||ch=='_'){
        readIdentOrKeyword();
        continue;
      }
      if(ch=='"'){
        readString();
        continue;
      }
      String two = "" + ch + peek();
            switch (two) {
                  case "==" -> { addToken(TokenType.EQ,  "=="); advance(); advance(); continue; }
                  case "!=" -> { addToken(TokenType.NEQ, "!="); advance(); advance(); continue; }
                  case "<=" -> { addToken(TokenType.LTE, "<="); advance(); advance(); continue; }
                  case ">=" -> { addToken(TokenType.GTE, ">="); advance(); advance(); continue; }
                }
            switch (ch) {
                case '+' -> addToken(TokenType.PLUS,   "+");
                case '-' -> addToken(TokenType.MINUS,  "-");
                case '*' -> addToken(TokenType.STAR,   "*");
                case '/' -> addToken(TokenType.SLASH,  "/");
                case '=' -> addToken(TokenType.ASSIGN, "=");
                case '<' -> addToken(TokenType.LT,     "<");
                case '>' -> addToken(TokenType.GT,     ">");
                case '(' -> addToken(TokenType.LPAREN, "(");
                case ')' -> addToken(TokenType.RPAREN, ")");
                case '{' -> addToken(TokenType.LBRACE, "{");
                case '}' -> addToken(TokenType.RBRACE, "}");
                case ',' -> addToken(TokenType.COMMA,  ",");
                case ':' -> addToken(TokenType.COLON,  ":");
                default  -> throw new RuntimeException(
                    "Line " + line + ": Unexpected character '" + ch + "'"
                );
            }
            advance();
        }
        addToken(TokenType.EOF, "EOF");
        return tokens;
    }
}