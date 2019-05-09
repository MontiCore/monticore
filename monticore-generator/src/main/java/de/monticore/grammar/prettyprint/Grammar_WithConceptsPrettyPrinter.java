/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.prettyprint;


import de.monticore.MCBasicLiteralsPrettyPrinter;
import de.monticore.MCJavaLiteralsPrettyPrinter;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionsNode;
import de.monticore.expressions.bitexpressions._ast.ASTBitExpressionsNode;
import de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.javaclassexpressions._ast.ASTJavaClassExpressionsNode;
import de.monticore.expressions.prettyprint2.*;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrNode;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar_withconcepts._ast.ASTGrammar_WithConceptsNode;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsDelegatorVisitor;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.java.javadsl._ast.ASTJavaDSLNode;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;


public class Grammar_WithConceptsPrettyPrinter implements Grammar_WithConceptsVisitor {
    
  private IndentPrinter printer;
  
  private Grammar_WithConceptsVisitor realThis = this;
  
  public final Grammar_WithConceptsDelegatorVisitor visitor;

  public Grammar_WithConceptsPrettyPrinter(IndentPrinter out) {
    printer = out;
    out.setIndentLength(2);
    visitor = new Grammar_WithConceptsDelegatorVisitor();
    visitor.setGrammar_WithConceptsVisitor(this);
    visitor.setAntlrVisitor(new AntlrPrettyPrinter(out));
    visitor.setGrammarVisitor(new GrammarPrettyPrinter(out));
    visitor.setJavaDSLVisitor(new JavaDSLPrettyPrinter(out));
    visitor.setAssignmentExpressionsVisitor(new AssignmentExpressionsPrettyPrinter(out));
    visitor.setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(out));
    visitor.setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(out));
    visitor.setMCBasicsVisitor(new MCBasicsPrettyPrinter(out));
    visitor.setJavaClassExpressionsVisitor(new JavaClassExpressionsPrettyPrinter(out));
    visitor.setBitExpressionsVisitor(new BitExpressionsPrettyPrinter(out));
    visitor.setMCBasicLiteralsVisitor(new MCBasicLiteralsPrettyPrinter(out));
    visitor.setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(out));
    visitor.setMCCollectionTypesVisitor(new MCCollectionTypesPrettyPrinter(out));
    visitor.setMCFullGenericTypesVisitor(new MCFullGenericTypesPrettyPrinter(out));
    visitor.setMCJavaLiteralsVisitor(new MCJavaLiteralsPrettyPrinter(out));
    visitor.setMCSimpleGenericTypesVisitor(new MCSimpleGenericTypesPrettyPrinter(out));
  }
  
  @Override public void setRealThis(Grammar_WithConceptsVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }

  @Override
  public Grammar_WithConceptsVisitor getRealThis() {
    return realThis;
  }
  
  public String prettyprint(ASTGrammar_WithConceptsNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }
  
  public String prettyprint(ASTGrammarNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }
  
  public String prettyprint(ASTAntlrNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }
 
  public String prettyprint(ASTJavaDSLNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }

  public String prettyprint(ASTExpressionsBasisNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }

  public String prettyprint(ASTBitExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }

  public String prettyprint(ASTCommonExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }

  public String prettyprint(ASTAssignmentExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }

  public String prettyprint(ASTJavaClassExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }
}
