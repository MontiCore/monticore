package de.monticore.codegen.mc2cd.symbolTransl;

import com.google.common.collect.Iterables;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
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
    rootLink.getLinks(ASTClassProd.class, ASTCDClass.class).forEach(link -> addASTRuleLink(rootLink, link, matchedASTRules));
    rootLink.getLinks(ASTAbstractProd.class, ASTCDClass.class).forEach(link -> addASTRuleLink(rootLink, link, matchedASTRules));
    rootLink.getLinks(ASTInterfaceProd.class, ASTCDClass.class).forEach(link -> addASTRuleLink(rootLink, link, matchedASTRules));
    rootLink.getLinks(ASTExternalProd.class, ASTCDClass.class).forEach(link -> addASTRuleLink(rootLink, link, matchedASTRules));

    return matchedASTRules;
  }


  private void addASTRuleLink(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink, Link<? extends ASTProd, ASTCDClass> link,
                              Set<ASTSymbolRule> matchedASTRules) {
    ASTNodes.getSuccessors(rootLink.source(), ASTSymbolRule.class).stream()
        .filter(astRule -> astRule.getType().equals(link.source().getName()))
        .forEach(matchedASTRule -> {
          matchedASTRules.add(matchedASTRule);
          new Link<>(matchedASTRule, link.target(), link.parent());
        });
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
