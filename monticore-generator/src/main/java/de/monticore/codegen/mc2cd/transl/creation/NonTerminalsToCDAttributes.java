/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * Creates the ASTCDAttributes corresponding to NonTerminals
 */
public class NonTerminalsToCDAttributes implements
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

  Link<? extends ASTProd, ? extends ASTCDType> link;

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
            ASTCDClass.class)) {
      this.link = link;
      link.source().accept(getRealThis());
    }

    for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(ASTInterfaceProd.class,
            ASTCDInterface.class)) {
      this.link = link;
      link.source().accept(getRealThis());
    }

    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(ASTAbstractProd.class,
            ASTCDClass.class)) {
      this.link = link;
      link.source().accept(getRealThis());
    }

    return rootLink;
  }

  @Override
  public void visit(ASTNonTerminal node) {
    ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().uncheckedBuild();
    link.target().getCDAttributeList().add(cdAttribute);
    new Link<>(node, cdAttribute, link);

  }
}
