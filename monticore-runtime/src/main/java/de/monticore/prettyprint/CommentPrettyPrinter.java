/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;

public  class CommentPrettyPrinter {
  

  /**
   * Print all comments before an ASTNode
   * 
   * @param a ASTNode that precomments shall be printed
   * @param p IndentPrinter that should be used for printing
   */
  public static void printPreComments(ASTNode a, IndentPrinter p) {
    
    if (a.get_PreCommentList() != null && a.get_PreCommentList().size() > 0) {
      
      // Additional line break for comments if necessary (that means if
      // already text exists in current line)
      if (!p.isStartOfLine()) {
        p.println();
      }
      
      // print all comments
      for (Comment c : a.get_PreCommentList()) {
        p.println(c.getText());
      }
    }
  }
  
  /**
   * Print all comments after an ASTNode
   * 
   * @param a ASTNode that postcomments shall be printed
   * @param p IndentPrinter that should be used for printing
   */
  public static void printPostComments(ASTNode a, IndentPrinter p) {
    
    if (a.get_PostCommentList() != null && a.get_PostCommentList().size() > 0) {
      
      // Additional line break for comments if necessary (that means if
      // already text exists in current line)
      if (!p.isStartOfLine()) {
        p.println();
      }

      // print all comments
      for (Comment c : a.get_PostCommentList()) {
        p.println(c.getText());
      }
    }
  }
}
