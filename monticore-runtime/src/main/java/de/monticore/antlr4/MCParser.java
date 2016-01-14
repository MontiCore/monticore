/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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

abstract public class MCParser extends Parser {
  
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
    return computeEndPosition(new SourcePosition(token.getLine(), token.getCharPositionInLine()), token.getText());
  }
  
  public de.se_rwth.commons.SourcePosition computeStartPosition(Token token) {
    if (token == null) {
      return null;
    }
    int line = token.getLine();
    int column = token.getCharPositionInLine();
    de.se_rwth.commons.SourcePosition pos = new de.se_rwth.commons.SourcePosition(line, column);
    return pos;
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
      String[] splitted = text.split("\n");
      line += splitted.length - 1;
      // +1: if there is 1 character on the last line, sourcepos must
      // be 2...
      column = splitted[splitted.length - 1].length() + 1;
    }
    de.se_rwth.commons.SourcePosition pos = new de.se_rwth.commons.SourcePosition(line, column);
    return pos;
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
    return (reference < 0 || actual <= reference);
  }
  
  public void setFilename(String filename) {
    this.filename = filename;
  }
  
  public void addComment(Comment comment) {
    comments.add(comment);
  }
  
  public void setActiveASTNode(ASTNode n) {
   
    ListIterator<Comment> listIterator = comments.listIterator();
    while (listIterator.hasNext()) {
      Comment c = listIterator.next(); 
      if (this.activeastnode != null && this.activeastnode.get_SourcePositionEnd().getLine() == c.get_SourcePositionStart().getLine()) {
        this.activeastnode.get_PostComments().add(c);
        listIterator.remove();
      }
      else if (c.get_SourcePositionStart().compareTo(n.get_SourcePositionStart()) < 0) {
        n.get_PreComments().add(c);
        listIterator.remove();
      }
    }
    
    this.activeastnode = n;
  }
  
}
