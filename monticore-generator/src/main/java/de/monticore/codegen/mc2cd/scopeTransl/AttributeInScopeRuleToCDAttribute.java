package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class AttributeInScopeRuleToCDAttribute implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTScopeRule, ASTCDClass> link : rootLink.getLinks(ASTScopeRule.class,
        ASTCDClass.class)) {
      for (ASTAdditionalAttribute attributeInAST : ASTNodes.getSuccessors(link.source(),
          ASTAdditionalAttribute.class)) {
        ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
        cdAttribute.setName(attributeInAST.getName());
        cdAttribute.setModifier(PROTECTED.build());
        link.target().getCDAttributeList().add(cdAttribute);
        new Link<>(attributeInAST, cdAttribute, link);
      }
    }

    for (Link<ASTScopeRule, ASTCDInterface> link : rootLink.getLinks(ASTScopeRule.class,
        ASTCDInterface.class)) {
      for (ASTAdditionalAttribute attributeInAST : ASTNodes.getSuccessors(link.source(),
          ASTAdditionalAttribute.class)) {
        ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
        cdAttribute.setName(attributeInAST.getName());
        cdAttribute.setModifier(PROTECTED.build());
        link.target().getCDAttributeList().add(cdAttribute);
        new Link<>(attributeInAST, cdAttribute, link);
      }
    }

    return rootLink;
  }
}
