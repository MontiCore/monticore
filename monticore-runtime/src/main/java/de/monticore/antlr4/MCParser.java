/* (c) https://github.com/MontiCore/monticore */

package de.monticore.antlr4;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.se_rwth.commons.SourcePosition;

public abstract class MCParser extends Parser {
  
  protected List<Comment> comments = new ArrayList<Comment>();
  
  protected ASTNode activeastnode;
  
  public MCParser(TokenStream input) {
    super(input);
    removeErrorListeners();
    addErrorListener(new MCErrorListener(this));
  }
  
  public MCParser() {
    super(null);
    removeErrorListeners();
    addErrorListener(new MCErrorListener(this));
  }
  
  protected boolean hasErrors = false;
  
  protected String filename = "";
  
  public String getFilename() {
    return filename;
  }
  
  public de.se_rwth.commons.SourcePosition computeEndPosition(Token token) {
    if (token == null || token.getText() == null) {
      return SourcePosition.getDefaultSourcePosition();
    }
    return computeEndPosition(new SourcePosition(token.getLine(), token.getCharPositionInLine()),
        token.getText());
  }
  
  public de.se_rwth.commons.SourcePosition computeStartPosition(Token token) {
    if (token == null) {
      return null;
    }
    int line = token.getLine();
    int column = token.getCharPositionInLine();
    return new de.se_rwth.commons.SourcePosition(line, column, getFilename());
  }
  
  public SourcePosition computeEndPosition(SourcePosition start, String text) {
    int line = start.getLine();
    int column = start.getColumn();
    if (text == null) {
      throw new IllegalArgumentException("0xA0708 text was null!");
    }
    else if ("\n".equals(text)) {
      column += text.length();
    }
    else if (text.indexOf("\n") == -1) {
      column += text.length();
    }
    else {
      String[] splitted = text.split("\n", -1);
      line += splitted.length - 1;
      // +1: if there is 1 character on the last line, sourcepos must
      // be 2...
      column = splitted[splitted.length - 1].length() + 1;
    }
    return new de.se_rwth.commons.SourcePosition(line, column);
  }
  
  public boolean hasErrors() {
    return hasErrors;
  }
  
  public void setErrors(boolean val) {
    hasErrors = val;
  }
  
  public boolean checkMin(int actual, int reference) {
    return actual >= reference;
  }
  
  public boolean checkMax(int actual, int reference) {
    return reference < 0 || actual <= reference;
  }
  
  public void setFilename(String filename) {
    this.filename = filename;
  }
  
  public void addComment(Comment comment) {
    comments.add(comment);
  }
  
  protected <E> void addToIteratedAttributeIfNotNull(List<E> attribute, E value) {
    if (value != null) {
      attribute.add(value);
    }
  }
  
  public void setActiveASTNode(ASTNode n) {
    
    ListIterator<Comment> listIterator = comments.listIterator();
    while (listIterator.hasNext()) {
      Comment c = listIterator.next();
      if (this.activeastnode != null && this.activeastnode.get_SourcePositionEnd().getLine() == c
          .get_SourcePositionStart().getLine()) {
        this.activeastnode.get_PostCommentList().add(c);
        listIterator.remove();
      }
      else if (c.get_SourcePositionStart().compareTo(n.get_SourcePositionStart()) < 0) {
        n.get_PreCommentList().add(c);
        listIterator.remove();
      }
    }
    
    this.activeastnode = n;
  }
  
  public boolean noSpace() {
    org.antlr.v4.runtime.Token t1 = _input.LT(-1);
    org.antlr.v4.runtime.Token t2 = _input.LT(-2);
    if ((t1 == null) || (t2==null)) {
      return false;
    }
    // token are on same line
    // and columns differ exactly length of earlier token (t2)
    return ((t1.getLine() == t2.getLine()) &&
        (t1.getCharPositionInLine() == t2.getCharPositionInLine() + t2.getText().length()));
  }

  public boolean noSpace(Integer... is) {
    for (Integer i: is) {
      org.antlr.v4.runtime.Token t1 = _input.LT(i);
      org.antlr.v4.runtime.Token t2 = _input.LT(i - 1);
      if ((t1 == null) || (t2==null)) {
        return false;
      }
      // token are on same line
      // and columns differ exactly length of earlier token (t2)
      if (((t1.getLine() != t2.getLine()) ||
              (t1.getCharPositionInLine() != t2.getCharPositionInLine() + t2.getText().length()))) {
        return false;
      }
    }
    return true;
  }


  /*
   * Compare the string of token (counting from the current token) with the given strings
   */
  public boolean cmpToken(int i, String... str) {
    org.antlr.v4.runtime.Token t1 = _input.LT(i);
    if (t1==null) {
      return false;
    }
    for (String s: str) {
      if (t1.getText().equals(s)) {
        return true;
      }
    }
    return false;
  }

  /*
   * Returns if the string of the token (counting from the current token) matches the given string
   */
  public boolean cmpTokenRegEx(int i, String regEx) {
    org.antlr.v4.runtime.Token t1 = _input.LT(i);
    if (t1==null) {
      return false;
    }
    return t1.getText().matches(regEx);
  }

  /*
   * Compare the string of the actual token with the given strings
   */
  public boolean is(String... str) {
    org.antlr.v4.runtime.Token t1 = _input.LT(-1);
    if (t1==null) {
      return false;
    }
    for (int i = 0; i < str.length; i++) {
      if (t1.getText().equals(str[i])) {
        return true;
      }
    }
    return false;
  }

  /*
   * Compare the string of the next token with the given strings
   */
  public boolean next(String... str) {
    org.antlr.v4.runtime.Token t1 = _input.LT(1);
    if (t1==null) {
      return false;
    }
    for (int i = 0; i < str.length; i++) {
      if (t1.getText().equals(str[i])) {
        return true;
      }
    }
    return false;
  }

  /*
   * Returns the string of the token (counting from the current token). If the token does
   * not exist, an empty string is returned
   */
  public String token(int i) {
    org.antlr.v4.runtime.Token t1 = _input.LT(i);
    if (t1==null) {
      return "";
    }
    return t1.getText();
  }

}
