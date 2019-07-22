/* (c)  https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.expressions.assignmentexpressions._od.AssignmentExpressions2OD;
import de.monticore.expressions.bitexpressions._od.BitExpressions2OD;
import de.monticore.expressions.commonexpressions._od.CommonExpressions2OD;
import de.monticore.expressions.expressionsbasis._od.ExpressionsBasis2OD;
import de.monticore.expressions.javaclassexpressions._od.JavaClassExpressions2OD;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.grammar.concepts.antlr.antlr._od.Antlr2OD;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar._od.Grammar2OD;
import de.monticore.grammar.grammar_withconcepts._od.Grammar_WithConcepts2OD;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsDelegatorVisitor;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.javastatements._od.JavaStatements2OD;
import de.monticore.mcbasics._od.MCBasics2OD;
import de.monticore.mccommonliterals._od.MCCommonLiterals2OD;
import de.monticore.mcliteralsbasis._od.MCLiteralsBasis2OD;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._od.MCBasicTypes2OD;
import de.monticore.types.mccollectiontypes._od.MCCollectionTypes2OD;
import de.monticore.types.mcsimplegenerictypes._od.MCSimpleGenericTypes2OD;

public class GrammarWithConcepts2OD extends Grammar_WithConcepts2OD {

  private Grammar_WithConceptsVisitor realThis = this;

  private final Grammar_WithConceptsDelegatorVisitor visitor;

  private IndentPrinter printer;

  public GrammarWithConcepts2OD(IndentPrinter printer, ReportingRepository reporting) {
    super(printer, reporting);
    visitor = new Grammar_WithConceptsDelegatorVisitor();
    visitor.setGrammar_WithConceptsVisitor(this);
    visitor.setAntlrVisitor(new Antlr2OD(printer, reporting));
    visitor.setGrammarVisitor(new Grammar2OD(printer, reporting));
    visitor.setJavaStatementsVisitor(new JavaStatements2OD(printer, reporting));
    visitor.setBitExpressionsVisitor(new BitExpressions2OD(printer, reporting));
    visitor.setJavaClassExpressionsVisitor(new JavaClassExpressions2OD(printer, reporting));
    visitor.setMCBasicsVisitor(new MCBasics2OD(printer, reporting));
    visitor.setCommonExpressionsVisitor(new CommonExpressions2OD(printer, reporting));
    visitor.setAssignmentExpressionsVisitor(new AssignmentExpressions2OD(printer, reporting));
    visitor.setExpressionsBasisVisitor(new ExpressionsBasis2OD(printer, reporting));
    visitor.setMCSimpleGenericTypesVisitor(new MCSimpleGenericTypes2OD(printer, reporting));
    visitor.setMCCollectionTypesVisitor(new MCCollectionTypes2OD(printer, reporting));
    visitor.setMCBasicTypesVisitor(new MCBasicTypes2OD(printer, reporting));
    visitor.setMCCommonLiteralsVisitor(new MCCommonLiterals2OD(printer, reporting));
    visitor.setMCLiteralsBasisVisitor(new MCLiteralsBasis2OD(printer, reporting));
    this.printer = printer;
  }

  @Override
  public void setRealThis(Grammar_WithConceptsVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }

  @Override
  public Grammar_WithConceptsVisitor getRealThis() {
    return realThis;
  }
  
  public String printObjectDiagram(String modelName, ASTGrammarNode a) {
    printer.clearBuffer();
    printer.setIndentLength(2);
    printer.print("objectdiagram ");
    printer.print(modelName);
    printer.println(" {");
    printer.indent();
    a.accept(getRealThis());
    pp.print(";");
    printer.unindent();
    printer.println("}");
    return printer.getContent(); 
  }
  

}
