/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.visitor.IVisitor;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * We use the visit mechanism to map the AST to a list of showing the AST-Nodes
 * as tree. As a basis we use ast2idents that maps each node into a string in a
 * compact form. Result is store as list for print
 * 
 */
public class TreePrintVisitor implements IVisitor {
  private ReportingRepository repo;
  
  // output to be stored here:
  protected List<String> treeResult;
  
  Stack<String> indents;
  
  static final String INITALINDENT = "";
  
  static final String INDENT1 = "+--";
  
  static final String INDENT2 = "|  ";
  
  static final String INDENT3 = "   ";
  
  // contains possible decorations for each entry in the tree
  // printed at the end of the line
  // null is a valid value (= no decoration)
  Map<String, String> endLineDecoration;
  
  // contains possible extra infos for each entry in the tree
  // printed in individual lines (and indented)
  // null is a valid value (= no extra info) and no empty line
  Map<String, List<String>> astNodeExtraInfos;
  
  /* visits all nodes and prints them as one liner with correct indentation */
  @Override
  public void visit(ASTNode a) {   
    // prepare the output
    String nodeId = repo.getASTNodeNameFormatted(a);
    String out = indents.peek() + INDENT1 + nodeId;
    if (endLineDecoration != null && endLineDecoration.containsKey(nodeId)) {
      String decor = endLineDecoration.get(nodeId);
      out = Layouter.padleft(out, 60) + " " + decor;
    }
    treeResult.add(out);
    
    String nextIndent = (indents.size() == 1) ? INDENT3 : INDENT2;

    // care for potential children:
    indents.push(indents.peek() + nextIndent);
    
    // print the extra infos
    if (astNodeExtraInfos != null && astNodeExtraInfos.containsKey(nodeId)) {
      List<String> extras = astNodeExtraInfos.get(nodeId);
      for (String s : extras) {
        treeResult.add(Layouter.padleft(indents.peek(), 20) + "      "
            + s);
      }
    }
  }
  
  @Override
  public void endVisit(ASTNode a) {
    // remove children stuff
    indents.pop();
  }

  public void setRepo(ReportingRepository repo) {
    this.repo = repo;
  }

  public void setEndLineDecoration(Map<String, String> endLineDecoration) {
    this.endLineDecoration = endLineDecoration;
  }

  public void setAstNodeExtraInfos(Map<String, List<String>> astNodeExtraInfos) {
    this.astNodeExtraInfos = astNodeExtraInfos;
  }

  /**
   * produces the raw tree without any decoration
   * 
   */
  public TreePrintVisitor() {
    this.treeResult = Lists.newArrayList();
    indents = new Stack<String>();
    indents.add(INITALINDENT);
  }
  
  /**
   * produces the tree with an inline decoration (at the end of each line)
   * 
   */
  public TreePrintVisitor(ReportingRepository repo,
      Map<String, String> endLineDecoration,
      Map<String, List<String>> astNodeExtraInfos) {
    super();
    this.repo = repo;
    this.treeResult = Lists.newArrayList();
    this.endLineDecoration = endLineDecoration;
    this.astNodeExtraInfos = astNodeExtraInfos;
    indents = new Stack<String>();
    indents.add(INITALINDENT); 
  }
  
  public List<String> getTreeResult() {
    return treeResult;
  }

  public void clear() {
    this.treeResult.clear();
    this.indents.clear();
    indents.add(INITALINDENT);
  }
}
