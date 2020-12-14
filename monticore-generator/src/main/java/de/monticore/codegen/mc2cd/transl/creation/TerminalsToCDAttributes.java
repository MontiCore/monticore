/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class TerminalsToCDAttributes implements
        UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>>, Grammar_WithConceptsVisitor {

  Grammar_WithConceptsVisitor realThis = this;

  @Override
  public Grammar_WithConceptsVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(Grammar_WithConceptsVisitor realThis) {
    this.realThis = realThis;
  }

  Link<ASTClassProd, ASTCDClass> link;

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
            ASTCDClass.class)) {
      this.link = link;
      link.source().accept(getRealThis());
    }
    return rootLink;
  }

  @Override
  public void visit(ASTTerminal terminal) {
    if (terminal.isPresentUsageName()) {
      ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().uncheckedBuild();
      link.target().getCDAttributeList().add(cdAttribute);
      new Link<>(terminal, cdAttribute, link);
    }
  }

  @Override
  public void visit(ASTKeyTerminal terminal) {
    if (terminal.isPresentUsageName()) {
      ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().uncheckedBuild();
      link.target().getCDAttributeList().add(cdAttribute);
      new Link<>(terminal, cdAttribute, link);
    }
  }

  @Override
  public void visit(ASTTokenTerminal terminal) {
    if (terminal.isPresentUsageName()) {
      ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().uncheckedBuild();
      link.target().getCDAttributeList().add(cdAttribute);
      new Link<>(terminal, cdAttribute, link);
    }
  }
}
