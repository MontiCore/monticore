/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import org.antlr.v4.runtime.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FormattingPrinter extends IndentPrinter {
  
  protected Stack<String> productionStack = new Stack<>();
  
  protected Stack<Integer> indentationStack = new Stack<>();
  
  protected List<TokenToBePrinted> tokenQueueFromEmit = new ArrayList<>();
  
  protected final IFormatter formatter;
  
  public FormattingPrinter() {
    this(new IFormatter.DefaultIFormatter());
  }
  
  public FormattingPrinter(IFormatter formatter) {
    super();
    this.formatter = formatter;
    indentationStack.push(0);
  }
  
  public FormattingPrinter(StringBuilder writtenbuffer, IFormatter formatter) {
    super(writtenbuffer);
    this.formatter = formatter;
    indentationStack.push(0);
  }
  
  /**
   * Increase the depth
   *
   * @param productionName
   */
  public void startProduction(String productionName) {
    productionStack.push(productionName);
    indentationStack.push(indentationStack.peek() + 1);
    tokenQueueFromEmit.add(new TokenToBePrinted(productionName, TokenToBePrinted.START_PROD));
  }
  
  /**
   * Enforce the indentation level is reset (report if necessary)
   */
  public void endProduction() {
    tokenQueueFromEmit.add(new TokenToBePrinted(productionStack.peek(), TokenToBePrinted.END_PROD));
    productionStack.pop();
    indentationStack.pop();
  }
  
  
  /**
   * Emits a new token to the buffer
   * @param string the {@link Token#getText()}
   * @param tokenType the symbolic name of the token
   * @param position the position/usage name
   */
  public void emit(String string, String tokenType, String position) {
    tokenQueueFromEmit.add(new TokenToBePrinted(string, tokenType, position, productionStack.peek()));
  }
  
  boolean currentlyEmitting = false; // lock
  protected Stack<Integer> depthFromQueueStack = new Stack<>();
  
  protected void workEmitQueue() {
    if (currentlyEmitting || tokenQueueFromEmit.isEmpty()) {
      return;
    }
    currentlyEmitting = true;
    // Print the current token stream to the printer
    TokenToBePrinted last = null, next = null;
    for (int i = 0, l = tokenQueueFromEmit.size(); i < l; i++) {
      var item = tokenQueueFromEmit.get(i);
      if (!item.isToken() && item.indentLevel == TokenToBePrinted.START_PROD) {
        depthFromQueueStack.push(this.getIndentation());
      }
      
      next = null;
      int nextIndex = i + 1;
      while (nextIndex < l && (next == null || !next.isToken())) {
        next = tokenQueueFromEmit.get(nextIndex);
        nextIndex++;
      }
      if (item.isToken()) {
        this.handleToken(
            item.string,
            item.tokenType,
            item.position,
            item.productionName,
            last == null ? null : last.tokenType,
            last == null ? null : last.string,
            next == null || !next.isToken() ? null : next.tokenType,
            next == null || !next.isToken() ? null : next.string,
            -42);
        last = item;
      }
      else if (item.indentLevel == TokenToBePrinted.END_PROD) {
        int indent = depthFromQueueStack.pop();
        if (this.getIndentation() != indent) {
          // The indentation was not reset - warn/report
          this.handleInvalidIndentationState(indent);
        }
      }
    }
    tokenQueueFromEmit.clear();
    currentlyEmitting = false;
  }
  
  protected void handleInvalidIndentationState(int indentPreRule) {
    System.err.println("Detected invalid indentation state pre=" + indentPreRule + " vs current=" + getIndentation());
    this.indent(indentPreRule-getIndentation());
    this.println();
  }
  
  protected boolean lastLineBreak; // avoid 2x linebreaks
  
  protected void handleToken(String token, String tokenType, String position,
      String productionName,
      String lastTokenType,
      String lastTokenString,
      String nextTokenType,
      String nextTokenString,
      int depth) {
    int fmt = formatter.getFormatOptions(token, tokenType, position, productionName,
        lastTokenType, lastTokenString, nextTokenType, nextTokenString, depth);
    
    if ((fmt & IFormatter.LINEBREAK_PRE) == IFormatter.LINEBREAK_PRE && !lastLineBreak)
      this.println();
    lastLineBreak = false;
    
    if ((fmt & IFormatter.UNINDENT) == IFormatter.UNINDENT) {
      System.err.println("##unindent");
      this.unindent();
    }
    
    
    print(token);
    
    if ((fmt & IFormatter.SPACE_FOLLOWING) == IFormatter.SPACE_FOLLOWING)
      print(" ");
    
    
    //TODO: noSpace
    if ((fmt & IFormatter.LINEBREAK_POST) == IFormatter.LINEBREAK_POST) {
      this.println();
      lastLineBreak = true;
    }
    if ((fmt & IFormatter.INDENT) == IFormatter.INDENT) {
      this.indent();
      System.err.println("##indent");
    }
  }
  
  @Override
  public String getContent() {
    this.workEmitQueue();
    return super.getContent();
  }
  
  @Override
  public void flushBuffer() {
    this.workEmitQueue();
    super.flushBuffer();
  }
  
  @Override
  protected void doPrint(String s) {
    this.workEmitQueue();
    super.doPrint(s);
  }
  
  // Entry in the buffer
  protected static class TokenToBePrinted {
    
    String string;
    String tokenType;
    String position;
    String productionName;
    static final int UNKNOWN = -1, START_PROD = -2, END_PROD = -3;
    int indentLevel;
    
    boolean isToken() {
      return tokenType != null;
    }
    
    public TokenToBePrinted(String string, String tokenType, String position, String productionName,
        int indentLevel) {
      this.string = string;
      this.tokenType = tokenType;
      this.position = position;
      this.productionName = productionName;
      this.indentLevel = indentLevel;
    }
    
    public TokenToBePrinted(String productionName, int indentLevel) {
      this(null, null, null, productionName, indentLevel);
    }
    
    public TokenToBePrinted(String string, String tokenType, String position,
        String productionName) {
      this(string, tokenType, position, productionName, UNKNOWN);
    }
    
    @Override
    public String toString() {
      return "TokenToBePrinted{" + "string='" + string + '\'' + ", tokenType='" + tokenType + '\''
          + ", position='" + position + '\'' + ", productionName='" + productionName + '\''
          + ", indentLevel=" + indentLevel + '}';
    }
  }
  
}
