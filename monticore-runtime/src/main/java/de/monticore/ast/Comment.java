/* (c)  https://github.com/MontiCore/monticore */

/*
 * WARNING: This file has been generated, don't modify !!
 */
package de.monticore.ast;

import de.se_rwth.commons.SourcePosition;


/**
 * Class represents a comment (contains the comment and the start- and end-position)
 *
 */
public class Comment {
  
  protected SourcePosition start = SourcePosition.getDefaultSourcePosition();
  
  protected SourcePosition end = SourcePosition.getDefaultSourcePosition();
  
  public SourcePosition get_SourcePositionEnd() {
    return end;
  }
  
  public void set_SourcePositionEnd(SourcePosition end) {
    this.end = end;
  }
  
  public SourcePosition get_SourcePositionStart() {
    return start;
  }
  
  public void set_SourcePositionStart(SourcePosition start) {
    this.start = start;
  }
  
  public String toString() {
    return text;
  }
  
  protected String text;
  
  public Comment() {
     text = "";
  }
  
  public Comment(String text) {
    setText(text);
  }
  
  public String getText() {
    return this.text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public boolean equals(Object o) {
    if (o instanceof Comment) {
      return this.text.equals(((Comment) o).text);
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    if (start == null) {
      return super.hashCode();
    }
    else {
      return start.getLine() * 1024 + start.getColumn();
    }
  }
  
}
