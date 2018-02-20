/* (c) https://github.com/MontiCore/monticore */

/*
 * WARNING: This file has been generated, don't modify !!
 */
package de.monticore;

import de.monticore.ast.Comment;


/**
 * This is the workaround for the ast-keyword bootstrap problem.
 *
 */
//TODO: handle this after the next releasing of MontiCore (4.5.4)
public class CommentExt extends Comment {
  
  public CommentExt() {}
  
  public CommentExt(String text) {
    super(text);
  }
  
}
