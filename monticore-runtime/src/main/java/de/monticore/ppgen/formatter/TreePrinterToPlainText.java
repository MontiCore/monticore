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

package de.monticore.ppgen.formatter;

import java.util.List;

import de.monticore.prettyprint.IndentPrinter;

/**
 * Converts a previously formatted pretty print tree to a string. It is the last
 * step of the pretty printing process.
 * 
 * @author diego
 */
//STATE SMELL? needed for pretty printer generation, maybe this should be part of generator project.
@Deprecated
public class TreePrinterToPlainText implements TreePrinterConverter {
  private IndentPrinter code;
  private char lastPrinted;
  private int minLenght;
  private int maxLenght;
  private int currentColumn;
  private boolean linebreakIndented;
  private int indentLevel;
  private int lastOptionalBreak;
  private int lastOptionalBreakLevel;
  private boolean lastOptionalBreakIndent;
  private StringBuilder linebuffer;
  
  /**
   * Constructor.
   * 
   * @param minLenght Minimum length to consider optional breaks.
   * @param maxLenght Maximum length of a line. When this length is reached, a
   *          new line is inserted, trying to find a suitable optional break if
   *          it is possible. For more information, @see
   *          #checkOptionalLineBreak(PPTNode, boolean).
   * @param indentLength Number of space per indent.
   */
  public TreePrinterToPlainText(int minLenght, int maxLenght, int indentLength) {
    lastPrinted = '\n';
    code = new IndentPrinter(new StringBuilder());
    code.setIndentLength(indentLength);
    linebuffer = new StringBuilder();
    this.minLenght = minLenght;
    this.maxLenght = maxLenght;
    currentColumn = 0;
    linebreakIndented = false;
    lastOptionalBreakLevel = 999999;
    lastOptionalBreak = -1;
    indentLevel = 0;
  }
  
  @Override
  public String convert(TreePrinter tp) {
    if (tp == null || tp.getRoot() == null)
      return "";
    
    printNode((PPTCNode) tp.getRoot());
    code.flushBuffer();
    
    return code.getContent();
  }
  
  /**
   * Converts a node to string.
   * 
   * @param n Node to convert.
   */
  private void printNode(PPTNode n) {
    // applies the pre-format rules
    printFormat(n.getPreFormat(), true, n);
    
    // print the node content
    printString(n.getValue());
    
    // print its children
    if (n instanceof PPTNodeList)
      printNode(((PPTNodeList) n).getChildren());
    
    // applies post-format rules
    printFormat(n.getPostFormat(), false, n);
  }
  
  private void printNode(List<PPTNode> list) {
    for (PPTNode n : list) {
      printNode(n);
    }
  }
  
  /**
   * Applies the format rules of a node. For example, if a format rule indicates
   * that a new line should be added, it adds a new line to the final string.
   * 
   * @param format Format rules to apply.
   * @param pre true if it is pre-format rules.
   * @param n The node the format rules are applied to.
   */
  private void printFormat(String format, boolean pre, PPTNode n) {
    for (int i = 0; i < format.length(); i++) {
      switch (format.charAt(i)) {
        case TreePrinter.INDENT:
          indent();
          
          break;
        
        case TreePrinter.UNINDENT:
          unindent();
          
          break;
        
        case TreePrinter.NEW_LINE:
          checkOptionalBreakBeforeNewLine();
          printNewLine();
          
          break;
        
        case TreePrinter.START_IN_NEW_LINE:
          if (pre) {
            if (!isLastNewLine())
              printNewLine();
          }
          else {
            checkOptionalBreakBeforeNewLine();
            printNewLine();
          }
          
          break;
        
        case TreePrinter.SPACE:
          printSpace();
          
          break;
        
        case TreePrinter.SEPARATE_WITH_SPACE:
          if (pre) {
            if (!isLastSpace())
              printSpace();
          }
          else {
            printSpace();
          }
          
          break;
        
        case TreePrinter.OPTIONAL_BREAK:
          checkOptionalLineBreak(n, true);
          
          break;
        
        case TreePrinter.OPTIONAL_BREAK_WITHOUT_INDENT:
          checkOptionalLineBreak(n, false);
          
          break;
      }
    }
  }
  
  /**
   * Indicates if the last printed character is a space or new line.
   * 
   * @return true if the last printed character is a spaced or new line.
   */
  private boolean isLastSpace() {
    if (lastPrinted == ' ' || lastPrinted == '\n')
      return true;
    
    return false;
  }
  
  /**
   * Indicates if the last printed character is a new line.
   * 
   * @return true if the last printed character is a new line.
   */
  private boolean isLastNewLine() {
    if (lastPrinted == '\n')
      return true;
    
    return false;
  }
  
  /**
   * Indent the resulting string.
   */
  private void indent() {
    code.indent();
    indentLevel++;
    currentColumn += code.getIndentLength();
  }
  
  /**
   * Unindent the resulting string.
   */
  private void unindent() {
    code.unindent();
    if (indentLevel > 0)
      indentLevel--;
    currentColumn -= code.getIndentLength();
  }
  
  /**
   * Prints a new line in the resulting string.
   */
  private void printNewLine() {
    // flush the line buffer to the final string
    lastPrinted = '\n';
    code.println(linebuffer.toString());
    linebuffer.delete(0, linebuffer.length());
    // updates the current column according to the indentation level
    currentColumn = code.getIndentLength() * indentLevel;
    // clear the current optional break
    lastOptionalBreak = -1;
    lastOptionalBreakLevel = 999999;
    
    // if a line break and a new indentation level were added because the
    // line was too long, now we have to unindent the code as the new line
    // stated by the format rules was found
    if (linebreakIndented) {
      code.unindent();
      linebreakIndented = false;
    }
  }
  
  /**
   * Prints a space.
   */
  private void printSpace() {
    lastPrinted = ' ';
    linebuffer.append(" ");
    currentColumn++;
  }
  
  /**
   * Prints a string taking care of new lines. If a new line is found, the
   * indentation for that line is added as well.
   * 
   * @param s String to be printed.
   */
  private void printString(String s) {
    if (s != null && s.length() > 0) {
      lastPrinted = s.charAt(s.length() - 1);
      
      // checks if the string contains a new line
      int pos = s.indexOf('\n');
      
      if (pos != -1) {
        // as the string contains a new line, we have to split it
        linebuffer.append(s.substring(0, pos));
        printNewLine();
        
        s = s.substring(pos);
        pos = s.indexOf('\n');
        
        while (pos != -1) {
          code.printWithoutProcessing(s.substring(0, pos + 1));
          s = s.substring(pos + 1);
          pos = s.indexOf('\n');
        }
        
        linebuffer.append(s);
        currentColumn += linebuffer.length();
        lastOptionalBreak = -1;
        lastOptionalBreakLevel = 999999;
      }
      else {
        // appends the string to the resulting string
        linebuffer.append(s);
        currentColumn += s.length();
      }
    }
  }
  
  /**
   * Checks if the optional break in the format rules of the node should insert
   * a new line.
   * 
   * @param n Node where the optional break is.
   * @param indent
   */
  private void checkOptionalLineBreak(PPTNode n, boolean indent) {
    // if the max length of a line has been reached, we have to insert
    // a new line
    if (currentColumn > maxLenght) {
      // if no optional break has been found from minLength to maxLength,
      // we insert a new line now. if not, we insert the new line in the
      // better found optional break
      if (lastOptionalBreak == -1) {
        printNewLine();
        indent();
        linebreakIndented = true;
      }
      else {
        printOptionalBreak();
      }
    }
    else if (currentColumn > minLenght) {
      // the criterion is to prioritize optional breaks which are in upper
      // levels of the tree. in this way code blocks are more compact.
      if (lastOptionalBreak == -1 || n.getLevel() < lastOptionalBreakLevel) {
        lastOptionalBreak = linebuffer.length();
        lastOptionalBreakLevel = n.getLevel();
        lastOptionalBreakIndent = indent;
      }
    }
  }
  
  /**
   * Inserts a new line in the position of the better optional break found.
   */
  private void printOptionalBreak() {
    // split the linebuffer by a new line inserted in the position of
    // the better optional break
    String s = linebuffer.substring(lastOptionalBreak, linebuffer.length());
    linebuffer.delete(lastOptionalBreak, linebuffer.length());
    printNewLine();
    
    // if the last optional break is with identation, we do it now and set
    // the lineBreakIndented as true so when we print a new line, the code is
    // unindented again
    if (lastOptionalBreakIndent) {
      indent();
      linebreakIndented = true;
    }
    
    linebuffer.append(s);
    currentColumn += linebuffer.length();
    lastOptionalBreak = -1;
    lastOptionalBreakLevel = 999999;
  }
  
  /**
   * Checks if it is worth to print the last found optional break.
   */
  private void checkOptionalBreakBeforeNewLine() {
    if (lastOptionalBreak != -1) {
      if ((currentColumn - lastOptionalBreak) >= minLenght) {
        printOptionalBreak();
      }
    }
  }
}
