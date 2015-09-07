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

package de.monticore.ppgen;

import de.monticore.ast.ASTNode;
import de.monticore.ppgen.formatter.PPTComment;
import de.monticore.ppgen.formatter.PPTConstantGroup;
import de.monticore.ppgen.formatter.PPTLexRule;
import de.monticore.ppgen.formatter.PPTNode;
import de.monticore.ppgen.formatter.PPTNodeList;
import de.monticore.ppgen.formatter.PPTRule;
import de.monticore.ppgen.formatter.PPTTerminal;
import de.monticore.ppgen.formatter.PPTText;
import de.monticore.ppgen.formatter.PPTVariable;

/**
 * TreePrinter class is used by the generated pretty printers to print the code
 * instead of IndentPrinter. The tree contains the strings to print in the file
 * in a tree structure with information about the rules, types, etc. That allows
 * to add format rules in specific places. In this way, the pretty printing
 * process is split in three steps: pretty printing of the AST in a tree,
 * formating of the tree, and conversion of the tree to a simple string.
 * 
 * @author diego
 */
//STATE SMELL? needed for pretty printer generation, maybe this should be part of generator project.
@Deprecated
public class TreePrinter {
  // format rules that can be inserted before or after a node
  static public final char INDENT = 'i';
  static public final char UNINDENT = 'u';
  static public final char NEW_LINE = 'l';
  static public final char START_IN_NEW_LINE = 'e';
  static public final char SPACE = 's';
  static public final char SEPARATE_WITH_SPACE = '_';
  static public final char OPTIONAL_BREAK = 'b';
  static public final char OPTIONAL_BREAK_WITHOUT_INDENT = 'r';
  
  /**
   * Root of the tree. It is usually the root rule of the grammar or the
   * external rule when embedding.
   */
  private PPTNode root;
  
  /**
   * It saves the current parent so when new nodes are added, their parent is
   * automatically set.
   */
  private PPTNode currentParent;
  
  /**
   * Constructor.
   */
  public TreePrinter() {
    
  }
  
  /**
   * Returns the root of the tree.
   * 
   * @return Root node of the tree.
   */
  public PPTNode getRoot() {
    return root;
  }
  
  /**
   * Sets the root of the tree.
   * 
   * @param root Root node of the tree.
   */
  public void setRoot(PPTNode root) {
    this.root = root;
  }
  
  /**
   * Returns the current parent of the tree. It will be used as parent for new
   * nodes.
   * 
   * @return Current parent of the tree.
   */
  public PPTNode getCurrentParent() {
    return currentParent;
  }
  
  /**
   * Sets the current parent of the tree. It will be used as parent for new
   * nodes.
   * 
   * @param currentParent Current parent of the tree.
   */
  public void setCurrentParent(PPTNode currentParent) {
    this.currentParent = currentParent;
  }
  
  /**
   * Adds a new node of type PPTTerminal without a name. It should be used to
   * print terminal elements like "{" or ",".
   * 
   * @param value String to be printed.
   * @return The added PPTTerminal node.
   */
  public PPTTerminal printTerminal(String value) {
    return printTerminal(value, null);
  }
  
  /**
   * Adds a new node of type PPTTerminal. It should be used to print terminal
   * elements like "{", "," or Option:"visible".
   * 
   * @param value String to be printed.
   * @param name Name of the terminal in the rule. For example, in the terminal
   *          Optional:"visible", the name is Optional.
   * @return The added PPTTerminal node.
   */
  public PPTTerminal printTerminal(String value, String name) {
    PPTTerminal n = new PPTTerminal();
    n.setValue(value);
    n.setName(name);
    n.setParent(currentParent);
    n.setLevel(currentParent.getLevel() + 1);
    n.setAstClass(currentParent.getAstClass());
    return n;
  }
  
  /**
   * Adds a new PPTRule node to the tree only with its name. It should be called
   * before printing a rule. An example of its use is in
   * 
   * @see mc.mcgrammar.ppgen.PrettyPrinterBaseConcreteVisitor#printRule
   * @param nonTerminalName Name of the non terminal in the grammar. For example,
   *          if the non terminal is Alts:Alt, the name is Alts. The name can be
   *          null and this is usually the case of root nodes.
   * @return The added PPTRule node.
   */
  public PPTRule startRule(String nonTerminalName) {
    PPTRule n = new PPTRule();
    n.setName(nonTerminalName);
    
    // if the parent was not set, it means this is the first node, so we
    // set it as currentParent.
    if (currentParent == null) {
      root = n;
      n.setLevel(0);
    }
    else {
      n.setParent(currentParent);
      n.setLevel(currentParent.getLevel() + 1);
    }
    
    currentParent = n;
    return n;
  }
  
  /**
   * Sets the rule name and the AST class of that rule in a PPTRule node, which
   * has to be the current parent. This method should be called once the print
   * method for the rule has been started. It is used in the generated
   * ownVisit() methods of the pretty printing concrete visitors.
   * 
   * @param ruleName Name of the rule.
   * @param astClass AST class of that rule.
   */
  public void setCurrentRuleProperties(String ruleName, Class<? extends ASTNode> astClass) {
    if (currentParent instanceof PPTRule) {
      PPTRule r = (PPTRule) currentParent;
      r.setType(ruleName);
      r.setAstClass(astClass);
    }
  }
  
  /**
   * It changes the current parent to the previous one. It should be called
   * after a rule has been pretty printed.
   */
  public void finishRule() {
    if (currentParent != null)
      currentParent = currentParent.getParent();
  }
  
  /**
   * Adds a PPTLexRule to the tree. It should be used to print non terminal
   * whose type is a lexical rule, like Name:IDENT.
   * 
   * @param value String to be printed.
   * @param nonTerminalName Name of the non terminal. For example, in the non
   *          terminal Num:INT, the name is Num.
   * @param lexRuleName Name of the lexical rule. For example, in the non
   *          terminal Num:INT, the name of the lexical rule is INT.
   * @return The added PPTLexRule node.
   */
  public PPTLexRule printLexRule(String value, String nonTerminalName, String lexRuleName) {
    PPTLexRule n = new PPTLexRule();
    n.setType(lexRuleName);
    n.setValue(value);
    n.setName(nonTerminalName);
    n.setAstClass(currentParent.getAstClass());
    n.setParent(currentParent);
    n.setLevel(currentParent.getLevel() + 1);
    return n;
  }
  
  /**
   * Adds a PPTVariable node to the tree. This is mark to print the content of
   * the variable later when it is found.
   * 
   * @param name Name of the variable in the rule. For example, in the variable
   *          Modifiers=SingleModifier*, the name is Modifiers.
   * @return The added PPTVariable node.
   */
  public PPTVariable printVariable(String name) {
    PPTVariable n = new PPTVariable();
    n.setName(name);
    n.setAstClass(currentParent.getAstClass());
    n.setParent(currentParent);
    n.setLevel(currentParent.getLevel() + 1);
    return n;
  }
  
  /**
   * Adds a PPTConstantGroup node to the tree. It should be used to print
   * boolean attributes or enumerations.
   * 
   * @param value Value to be printed. For example, in the boolean attribute
   *          Option:["final"], the value is final.
   * @param name Name of in the rule. For example, in the boolean attribute
   *          Option:["final"], the name is Option.
   * @return The added PPTConstantGorup node.
   */
  public PPTConstantGroup printConstantGroup(String value, String name) {
    PPTConstantGroup n = new PPTConstantGroup();
    n.setValue(value);
    n.setName(name);
    n.setParent(currentParent);
    n.setLevel(currentParent.getLevel() + 1);
    n.setAstClass(currentParent.getAstClass());
    return n;
  }
  
  /**
   * Adds a PPTComment to the tree. It should be used to print comments.
   * 
   * @param value Content of the comment.
   * @return The added PPTComment node.
   */
  public PPTComment printComment(String value) {
    PPTComment n = new PPTComment();
    n.setValue(value);
    n.setParent(currentParent);
    n.setLevel(currentParent.getLevel() + 1);
    n.setAstClass(currentParent.getAstClass());
    
    return n;
  }
  
  /**
   * Adds a PPTText to the tree. It has no special type and it is usually used
   * to add previously formatted strings. For example, it is used in the class @see
   * TreeCompatibleIndentPrinter to print code from old pretty printers.
   * 
   * @param value String to be pretty printed.
   * @param astClass AST class where the node belongs to. It is usually the AST
   *          class of the parent.
   * @return The added PPTText node.
   */
  public PPTText printText(String value, Class<? extends ASTNode> astClass) {
    PPTText n = new PPTText();
    n.setValue(value);
    n.setParent(currentParent);
    n.setLevel(currentParent.getLevel() + 1);
    n.setAstClass(astClass);
    return n;
  }
  
  /**
   * Removes optional break codes form a string.
   * 
   * @param s Format string.
   * @return Format string without optional break codes.
   */
  static private String removeOptionalBreaks(String s) {
    String res = s.replaceAll(Character.toString(TreePrinter.OPTIONAL_BREAK), "");
    res = s.replaceAll(Character.toString(TreePrinter.OPTIONAL_BREAK_WITHOUT_INDENT), "");
    
    return res;
  }
  
  /**
   * Adds a pre-format rule to the node, for example @see
   * {@link TreePrinter#INDENT}.
   * 
   * @param format Format to be added. They are defined in @see TreePrinter.
   * @param n Node where the pre-format is going to be inserted.
   */
  static public void addPreFormat(char format, PPTNode n) {
    if (format == NEW_LINE || format == START_IN_NEW_LINE) {
      n.setPreFormat(removeOptionalBreaks(n.getPreFormat()));
    }
    else if ((format == OPTIONAL_BREAK || format == OPTIONAL_BREAK_WITHOUT_INDENT) &&
        n.getPreFormat().indexOf(NEW_LINE) != -1) {
      return;
    }
    
    n.setPreFormat(n.getPreFormat() + Character.toString(format));
  }
  
  /**
   * Adds a post-format rule to the node, for example @see
   * {@link TreePrinter#INDENT}.
   * 
   * @param format Format to be added. They are defined in @see TreePrinter.
   * @param n Node where the post-format is going to be inserted.
   */
  static public void addPostFormat(char format, PPTNode n) {
    if (format == NEW_LINE || format == START_IN_NEW_LINE) {
      n.setPostFormat(removeOptionalBreaks(n.getPostFormat()));
    }
    else if ((format == OPTIONAL_BREAK || format == OPTIONAL_BREAK_WITHOUT_INDENT) &&
        n.getPostFormat().indexOf(NEW_LINE) != -1) {
      return;
    }
    
    n.setPostFormat(n.getPostFormat() + Character.toString(format));
  }
  
  /**
   * Returns the estimated length of a node reading its children. It is
   * estimated because spaces or new lines specified in the format are not taken
   * into account.
   * 
   * @param n Node to compute its length.
   * @return Estimated length of the node.
   */
  static public int estimatedLength(PPTNode n) {
    int estimatedLength = 0;
    
    // adds length of the node
    estimatedLength += n.getValue().length();
    
    // if the node has children, adds its length
    if (n instanceof PPTNodeList) {
      for (PPTNode child : ((PPTNodeList) n).getChildren()) {
        estimatedLength += estimatedLength(child);
      }
    }
    
    return estimatedLength;
  }
}
