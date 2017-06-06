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

package de.monticore.grammar.prettyprint;

import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrNode;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar_withconcepts._ast.ASTGrammar_WithConceptsNode;
import de.monticore.grammar.grammar_withconcepts._visitor.CommonGrammar_WithConceptsDelegatorVisitor;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.java.javadsl._ast.ASTJavaDSLNode;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.literals.prettyprint.LiteralsPrettyPrinterConcreteVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;

public class Grammar_WithConceptsPrettyPrinter implements Grammar_WithConceptsVisitor {
    
  private IndentPrinter printer;
  
  private Grammar_WithConceptsVisitor realThis = this;
  
  public final CommonGrammar_WithConceptsDelegatorVisitor visitor;

  public Grammar_WithConceptsPrettyPrinter(IndentPrinter out) {
    printer = out;
    out.setIndentLength(2);
    visitor = new CommonGrammar_WithConceptsDelegatorVisitor();
    visitor.set_de_monticore_grammar_grammar_withconcepts__visitor_Grammar_WithConceptsVisitor(this);
    visitor.set_de_monticore_grammar_concepts_antlr_antlr__visitor_AntlrVisitor(new AntlrPrettyPrinter(out));
    visitor.set_de_monticore_grammar_grammar__visitor_GrammarVisitor(new GrammarPrettyPrinter(out));
    visitor.set_de_monticore_java_javadsl__visitor_JavaDSLVisitor(new JavaDSLPrettyPrinter(out));
    visitor.set_de_monticore_literals_literals__visitor_LiteralsVisitor(new LiteralsPrettyPrinterConcreteVisitor(out));
    visitor.set_de_monticore_types_types__visitor_TypesVisitor(new TypesPrettyPrinterConcreteVisitor(out));
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

}
