/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * Creates the ASTCDAttributes corresponding to NonTerminals
 */
public class NonTerminalsToCDAttributes implements
        UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {



  Link<? extends ASTProd, ? extends ASTCDType> link;

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    Grammar_WithConceptsTraverser traverser = Grammar_WithConceptsMill.traverser();
    traverser.add4Grammar(new NonTerminalVisitor());

    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
            ASTCDClass.class)) {
      this.link = link;
      link.source().accept(traverser);
    }

    for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(ASTInterfaceProd.class,
            ASTCDInterface.class)) {
      this.link = link;
      link.source().accept(traverser);
    }

    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(ASTAbstractProd.class,
            ASTCDClass.class)) {
      this.link = link;
      link.source().accept(traverser);
    }

    return rootLink;
  }

  private class NonTerminalVisitor implements GrammarVisitor2 {

    public GrammarTraverser getTraverser() {
      return traverser;
    }

    public void setTraverser(GrammarTraverser traverser) {
      this.traverser = traverser;
    }

    GrammarTraverser traverser;

    @Override
    public void visit (ASTNonTerminal node){
      ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
      link.target().getCDAttributeList().add(cdAttribute);
      new Link<>(node, cdAttribute, link);

    }
  }
}
