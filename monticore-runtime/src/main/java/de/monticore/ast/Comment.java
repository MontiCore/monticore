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
      return start.getLine() * 100 + start.getColumn();
    }
  }
  
}
