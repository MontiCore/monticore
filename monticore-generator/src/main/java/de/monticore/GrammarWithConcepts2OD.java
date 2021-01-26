/* (c) https://github.com/MontiCore/monticore */

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
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._od.Grammar_WithConcepts2OD;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.literals.mccommonliterals._od.MCCommonLiterals2OD;
import de.monticore.literals.mcliteralsbasis._od.MCLiteralsBasis2OD;
import de.monticore.mcbasics._od.MCBasics2OD;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcexceptionstatements._od.MCExceptionStatements2OD;
import de.monticore.statements.mcreturnstatements._od.MCReturnStatements2OD;
import de.monticore.types.mcbasictypes._od.MCBasicTypes2OD;
import de.monticore.types.mccollectiontypes._od.MCCollectionTypes2OD;
import de.monticore.types.mcsimplegenerictypes._od.MCSimpleGenericTypes2OD;

public class GrammarWithConcepts2OD  {

  private IndentPrinter printer;

  private Grammar_WithConceptsTraverser traverser;


  public GrammarWithConcepts2OD(IndentPrinter printer, ReportingRepository reporting) {
    traverser = Grammar_WithConceptsMill.traverser();
    traverser.add4Grammar_WithConcepts(new Grammar_WithConcepts2OD(printer, reporting));
    traverser.add4Antlr(new Antlr2OD(printer, reporting));
    traverser.add4Grammar(new Grammar2OD(printer, reporting));
    traverser.add4BitExpressions(new BitExpressions2OD(printer, reporting));
    traverser.add4JavaClassExpressions(new JavaClassExpressions2OD(printer, reporting));
    traverser.add4MCBasics(new MCBasics2OD(printer, reporting));
    traverser.add4CommonExpressions(new CommonExpressions2OD(printer, reporting));
    traverser.add4AssignmentExpressions(new AssignmentExpressions2OD(printer, reporting));
    traverser.add4ExpressionsBasis(new ExpressionsBasis2OD(printer, reporting));
    traverser.add4MCSimpleGenericTypes(new MCSimpleGenericTypes2OD(printer, reporting));
    traverser.add4MCCollectionTypes(new MCCollectionTypes2OD(printer, reporting));
    traverser.add4MCBasicTypes(new MCBasicTypes2OD(printer, reporting));
    traverser.add4MCCommonLiterals(new MCCommonLiterals2OD(printer, reporting));
    traverser.add4MCLiteralsBasis(new MCLiteralsBasis2OD(printer, reporting));
    traverser.add4MCExceptionStatements(new MCExceptionStatements2OD(printer, reporting));
    traverser.add4MCReturnStatements(new MCReturnStatements2OD(printer, reporting));
    this.printer = printer;
  }

  public String printObjectDiagram(String modelName, ASTGrammarNode a) {
    printer.clearBuffer();
    printer.setIndentLength(2);
    printer.print("objectdiagram ");
    printer.print(modelName);
    printer.println(" {");
    printer.indent();
    a.accept(traverser);
    printer.print(";");
    printer.unindent();
    printer.println("}");
    return printer.getContent(); 
  }

}
