/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
    
    if (a.get_PreComments() != null && a.get_PreComments().size() > 0) {
      
      // Additional line break for comments if necessary (that means if
      // already text exists in current line)
      if (!p.isStartOfLine()) {
        p.println();
      }
      
      // print all comments
      for (Comment c : a.get_PreComments()) {
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
    
    if (a.get_PostComments() != null && a.get_PostComments().size() > 0) {
      
      // Additional line break for comments if necessary (that means if
      // already text exists in current line)
      if (!p.isStartOfLine()) {
        p.println();
      }

      // print all comments
      for (Comment c : a.get_PostComments()) {
        p.println(c.getText());
      }
    }
  }
}
