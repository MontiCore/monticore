/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class TerminalsToCDAttributes implements
        UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  Link<ASTClassProd, ASTCDClass> link;

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    Grammar_WithConceptsTraverser traverser = Grammar_WithConceptsMill.traverser();
    traverser.add4Grammar(new TerminalVisitor());
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
            ASTCDClass.class)) {
      this.link = link;
      link.source().accept(traverser);
    }
    return rootLink;
  }

  private class TerminalVisitor implements GrammarVisitor2 {
    public GrammarTraverser getTraverser() {
      return traverser;
    }

    public void setTraverser(GrammarTraverser traverser) {
      this.traverser = traverser;
    }

    GrammarTraverser traverser;

    @Override
    public void visit(ASTTerminal terminal) {
      if (terminal.isPresentUsageName()) {
        ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().uncheckedBuild();
        link.target().addCDMember(cdAttribute);
        new Link<>(terminal, cdAttribute, link);
      }
    }

    @Override
    public void visit(ASTKeyTerminal terminal) {
      if (terminal.isPresentUsageName()) {
        ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().uncheckedBuild();
        link.target().addCDMember(cdAttribute);
        new Link<>(terminal, cdAttribute, link);
      }
    }

    @Override
    public void visit(ASTTokenTerminal terminal) {
      if (terminal.isPresentUsageName()) {
        ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().uncheckedBuild();
        link.target().addCDMember(cdAttribute);
        new Link<>(terminal, cdAttribute, link);
      }
    }
  }
}
