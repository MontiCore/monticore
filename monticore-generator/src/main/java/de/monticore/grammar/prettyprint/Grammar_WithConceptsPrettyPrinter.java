/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.prettyprint;

import de.monticore.assignmentexpressions._ast.ASTAssignmentExpressionsNode;
import de.monticore.commonexpressions._ast.ASTCommonExpressionsNode;
import de.monticore.expressions.prettyprint.*;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrNode;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar_withconcepts._ast.ASTGrammar_WithConceptsNode;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsDelegatorVisitor;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.java.javadsl._ast.ASTJavaDSLNode;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.javaclassexpressions._ast.ASTJavaClassExpressionsNode;
import de.monticore.literals.prettyprint.LiteralsPrettyPrinterConcreteVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.shiftexpressions._ast.ASTShiftExpressionsNode;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;

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
    visitor.setLiteralsVisitor(new LiteralsPrettyPrinterConcreteVisitor(out));
    visitor.setAssignmentExpressionsVisitor(new AssignmentExpressionsPrettyPrinter(out));
    visitor.setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(out));
    visitor.setJavaClassExpressionsVisitor(new JavaClassExpressionsPrettyPrinter(out));
    visitor.setShiftExpressionsVisitor(new ShiftExpressionsPrettyPrinter(out));
    visitor.setTypesVisitor(new TypesPrettyPrinterConcreteVisitor(out));
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

  public String prettyprint(ASTShiftExpressionsNode a) {
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
