/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import com.google.common.collect.Iterables;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.utils.Link;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Creates Links from ASTRules to CDClasses and CDInterfaces. The CDClass/CDInterface will
 * correspond to the ClassProd/AbstractProd/InterfaceProd referred to by the ASTRule.
 *
 */
public class ASTRulesToCDClassesAndCDInterfaces implements
        UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    Set<ASTASTRule> matchedASTRules = createLinksForMatchedASTRules(rootLink);
    createLinksForUnmatchedASTRules(matchedASTRules, rootLink);

    return rootLink;
  }

  protected Set<ASTASTRule> createLinksForMatchedASTRules(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    Set<ASTASTRule> matchedASTRules = new LinkedHashSet<>();
    // creates Links from ASTRules to the CDClasses of corresponding ClassProds
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
            ASTCDClass.class)) {

      rootLink.source().getASTRuleList().stream()
              .filter(astRule -> astRule.getType().equals(link.source().getName()))
              .forEach(matchedASTRule -> {
                matchedASTRules.add(matchedASTRule);
                new Link<>(matchedASTRule, link.target(), link.parent());
              });
    }

    // creates Links from ASTRules to the CDClasses of corresponding AbstractProds
    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(ASTAbstractProd.class,
            ASTCDClass.class)) {

      rootLink.source().getASTRuleList().stream()
              .filter(astRule -> astRule.getType().equals(link.source().getName()))
              .forEach(matchedASTRule -> {
                matchedASTRules.add(matchedASTRule);
                new Link<>(matchedASTRule, link.target(), link.parent());
              });
    }

    // creates Links from ASTRules to the CDInterfaces of corresponding InterfaceProds
    for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(ASTInterfaceProd.class,
            ASTCDInterface.class)) {

      rootLink.source().getASTRuleList().stream()
              .filter(astRule -> astRule.getType().equals(link.source().getName()))
              .forEach(matchedASTRule -> {
                matchedASTRules.add(matchedASTRule);
                new Link<>(matchedASTRule, link.target(), link.parent());
              });
    }
    // creates Links from ASTRules to the CDInterfaces of corresponding ExternalProds
    for (Link<ASTExternalProd, ASTCDInterface> link : rootLink.getLinks(ASTExternalProd.class,
            ASTCDInterface.class)) {

      rootLink.source().getASTRuleList().stream()
              .filter(astRule -> astRule.getType().equals(link.source().getName()))
              .forEach(matchedASTRule -> {
                matchedASTRules.add(matchedASTRule);
                new Link<>(matchedASTRule, link.target(), link.parent());
              });
    }
    return matchedASTRules;
  }

  protected void createLinksForUnmatchedASTRules(Set<ASTASTRule> matchedASTRules,
                                               Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (ASTASTRule astRule : rootLink.source().getASTRuleList()) {
      if (!matchedASTRules.contains(astRule)) {
        ASTCDClass cdClass = CD4AnalysisMill.cDClassBuilder().
                setModifier(CD4AnalysisMill.modifierBuilder().setPublic(true).build()).
                uncheckedBuild();

        Link<ASTMCGrammar, ASTCDDefinition> parentLink = Iterables.getOnlyElement(rootLink
                .getLinks(ASTMCGrammar.class, ASTCDDefinition.class));
        parentLink.target().addCDElement(cdClass);
        new Link<>(astRule, cdClass, parentLink);
      }
    }
  }
}
