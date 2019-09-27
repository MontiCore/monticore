package de.monticore.codegen.mc2cd.symbolTransl;

import com.google.common.collect.Iterables;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class SymbolRulesToCDClassAndCDInterface implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    Set<ASTSymbolRule> matchedRules = createLinksForMatchedASTRules(rootLink);
    createLinksForUnmatchedASTRules(matchedRules, rootLink);
    return rootLink;
  }

  private Set<ASTSymbolRule> createLinksForMatchedASTRules(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    Set<ASTSymbolRule> matchedASTRules = new LinkedHashSet<>();
    // creates Links from ASTRules to the CDClasses of corresponding ClassProds
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {

      ASTNodes.getSuccessors(rootLink.source(), ASTSymbolRule.class).stream()
          .filter(astRule -> astRule.getType().equals(link.source().getName()))
          .forEach(matchedASTRule -> {
            matchedASTRules.add(matchedASTRule);
            new Link<>(matchedASTRule, link.target(), link.parent());
          });
    }

    // creates Links from ASTRules to the CDClasses of corresponding AbstractProds
    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(ASTAbstractProd.class,
        ASTCDClass.class)) {

      ASTNodes.getSuccessors(rootLink.source(), ASTSymbolRule.class).stream()
          .filter(astRule -> astRule.getType().equals(link.source().getName()))
          .forEach(matchedASTRule -> {
            matchedASTRules.add(matchedASTRule);
            new Link<>(matchedASTRule, link.target(), link.parent());
          });
    }

    // creates Links from ASTRules to the CDInterfaces of corresponding InterfaceProds
    for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(ASTInterfaceProd.class,
        ASTCDInterface.class)) {

      ASTNodes.getSuccessors(rootLink.source(), ASTSymbolRule.class).stream()
          .filter(astRule -> astRule.getType().equals(link.source().getName()))
          .forEach(matchedASTRule -> {
            matchedASTRules.add(matchedASTRule);
            new Link<>(matchedASTRule, link.target(), link.parent());
          });
    }
    // creates Links from ASTRules to the CDInterfaces of corresponding ExternalProds
    for (Link<ASTExternalProd, ASTCDInterface> link : rootLink.getLinks(ASTExternalProd.class,
        ASTCDInterface.class)) {

      ASTNodes.getSuccessors(rootLink.source(), ASTSymbolRule.class).stream()
          .filter(astRule -> astRule.getType().equals(link.source().getName()))
          .forEach(matchedASTRule -> {
            matchedASTRules.add(matchedASTRule);
            new Link<>(matchedASTRule, link.target(), link.parent());
          });
    }
    return matchedASTRules;
  }

  private void createLinksForUnmatchedASTRules(Set<ASTSymbolRule> matchedASTRules,
                                               Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (ASTSymbolRule symbolRule : ASTNodes.getSuccessors(rootLink.source(), ASTSymbolRule.class)) {
      if (!matchedASTRules.contains(symbolRule)) {
        ASTCDClass cdClass = CD4AnalysisNodeFactory.createASTCDClass();
        cdClass.setModifier(CD4AnalysisNodeFactory.createASTModifier());

        Link<ASTMCGrammar, ASTCDDefinition> parentLink = Iterables.getOnlyElement(rootLink
            .getLinks(ASTMCGrammar.class, ASTCDDefinition.class));
        parentLink.target().getCDClassList().add(cdClass);
        new Link<>(symbolRule, cdClass, parentLink);
      }
    }
  }
}
