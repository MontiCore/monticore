/* (c)  https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.assignmentexpressions._od.AssignmentExpressions2OD;
import de.monticore.commonexpressions._od.CommonExpressions2OD;
import de.monticore.expressionsbasis._od.ExpressionsBasis2OD;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.grammar.concepts.antlr.antlr._od.Antlr2OD;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar._od.Grammar2OD;
import de.monticore.grammar.grammar_withconcepts._od.Grammar_WithConcepts2OD;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsDelegatorVisitor;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.java.javadsl._od.JavaDSL2OD;
import de.monticore.javaclassexpressions._od.JavaClassExpressions2OD;
import de.monticore.lexicals.lexicals._od.Lexicals2OD;
import de.monticore.literals.literals._od.Literals2OD;
import de.monticore.mcbasics._od.MCBasics2OD;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.shiftexpressions._od.ShiftExpressions2OD;
import de.monticore.types.types._od.Types2OD;


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
    visitor.setJavaDSLVisitor(new JavaDSL2OD(printer, reporting));
    visitor.setLiteralsVisitor(new Literals2OD(printer, reporting));
    visitor.setTypesVisitor(new Types2OD(printer, reporting));
    visitor.setShiftExpressionsVisitor(new ShiftExpressions2OD(printer, reporting));
    visitor.setJavaClassExpressionsVisitor(new JavaClassExpressions2OD(printer, reporting));
    visitor.setMCBasicsVisitor(new MCBasics2OD(printer, reporting));
    visitor.setCommonExpressionsVisitor(new CommonExpressions2OD(printer, reporting));
    visitor.setAssignmentExpressionsVisitor(new AssignmentExpressions2OD(printer, reporting));
    visitor.setExpressionsBasisVisitor(new ExpressionsBasis2OD(printer, reporting));
    visitor.setLexicalsVisitor(new Lexicals2OD(printer, reporting));
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
