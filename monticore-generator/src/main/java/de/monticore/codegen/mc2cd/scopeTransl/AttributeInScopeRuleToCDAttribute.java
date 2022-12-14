/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

import static de.monticore.cd.facade.CDModifier.PROTECTED;

public class AttributeInScopeRuleToCDAttribute implements
        UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTScopeRule, ASTCDClass> link : rootLink.getLinks(ASTScopeRule.class,
            ASTCDClass.class)) {
      for (ASTAdditionalAttribute attributeInAST : link.source().getAdditionalAttributeList()) {
        createAttributeLink(attributeInAST, link);
      }
    }

    for (Link<ASTScopeRule, ASTCDInterface> link : rootLink.getLinks(ASTScopeRule.class,
            ASTCDInterface.class)) {
      for (ASTAdditionalAttribute attributeInAST : link.source().getAdditionalAttributeList()) {
        createAttributeLink(attributeInAST, link);
      }
    }

    return rootLink;
  }

  protected void createAttributeLink(ASTAdditionalAttribute attributeInAST, Link<ASTScopeRule, ? extends ASTCDType> link) {
    ASTCDAttribute cdAttribute = CD4AnalysisMill.cDAttributeBuilder().
            setName(attributeInAST.getName()).
            setModifier(PROTECTED.build()).uncheckedBuild();
    link.target().addCDMember(cdAttribute);
    new Link<>(attributeInAST, cdAttribute, link);
  }
}
