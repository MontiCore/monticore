/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore;

import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.grammar.concepts.antlr.antlr._od.Antlr2OD;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar._od.Grammar2OD;
import de.monticore.grammar.grammar_withconcepts._od.Grammar_WithConcepts2OD;
import de.monticore.grammar.grammar_withconcepts._visitor.CommonGrammar_WithConceptsDelegatorVisitor;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsDelegatorVisitor;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.literals.literals._od.Literals2OD;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.types._od.Types2OD;


public class GrammarWithConcepts2OD extends Grammar_WithConcepts2OD {
    
  private Grammar_WithConceptsVisitor realThis = this;
  
  private final Grammar_WithConceptsDelegatorVisitor visitor;
  
  private IndentPrinter printer;
  
  /**
   * Constructor for de.monticore.GrammarWithConcepts2OD.
   * @param symbol
   * @param identHelper
   */
  public GrammarWithConcepts2OD(IndentPrinter printer, ReportingRepository reporting) {
    super(printer, reporting);
    visitor = new CommonGrammar_WithConceptsDelegatorVisitor();
    visitor.set_de_monticore_grammar_grammar_withconcepts__visitor_Grammar_WithConceptsVisitor(this);
    visitor.set_de_monticore_grammar_concepts_antlr_antlr__visitor_AntlrVisitor(new Antlr2OD(printer, reporting));
    visitor.set_de_monticore_grammar_grammar__visitor_GrammarVisitor(new Grammar2OD(printer, reporting));
    // TODO Remove comment if correct JavaDSL2OD exists
    //visitor.set_de_monticore_java_javadsl__visitor_JavaDSLVisitor(new JavaDSL2OD(printer, reporting));
    visitor.set_de_monticore_literals_literals__visitor_LiteralsVisitor(new Literals2OD(printer, reporting));
    visitor.set_de_monticore_types_types__visitor_TypesVisitor(new Types2OD(printer, reporting));
    this.printer = printer;
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
  
  public String printObjectDiagram(String modelName, ASTGrammarNode a) {
    printer.clearBuffer();
    printer.setIndentLength(2);
    printer.print("astobjectdiagram ");
    printer.print(modelName);
    printer.println(" {");
    printer.indent();
    a.accept(getRealThis());
    printer.unindent();
    printer.println("}");
    return printer.getContent(); 
  }
  

}
