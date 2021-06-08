/* (c) https://github.com/MontiCore/monticore */
package de.monticore.ast;

import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

/**
 * Builder for RTE class {@link Comment}
 */
public class CommentBuilder {
  
  protected String text = null;
  
  protected SourcePosition start = SourcePosition.getDefaultSourcePosition();
  
  protected SourcePosition end = SourcePosition.getDefaultSourcePosition();
  
  public Comment build() {
    if (isValid()) {
      Comment res = new Comment(text);
      res.set_SourcePositionStart(start);
      res.set_SourcePositionEnd(end);
      return res;
    }
    else {
      Log.error("0xA4322 text of type String must not be null");
      throw new IllegalStateException();
    }
  }
  
  public boolean isValid() {
    return this.text != null;
  }
  
  public SourcePosition get_SourcePositionEnd() {
    return end;
  }
  
  public CommentBuilder set_SourcePositionEnd(SourcePosition end) {
    this.end = end;
    return this;
  }
  
  public SourcePosition get_SourcePositionStart() {
    return start;
  }
  
  public CommentBuilder set_SourcePositionStart(SourcePosition start) {
    this.start = start;
    return this;
  }
  
  public String getText() {
    return this.text;
  }
  
  public CommentBuilder setText(String text) {
    this.text = text;
    return this;
  }
}
