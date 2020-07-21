/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.symbolTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

import static de.monticore.cd.facade.CDModifier.*;

public class AttributeInSymbolRuleToCDAttribute implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTSymbolRule, ASTCDClass> link : rootLink.getLinks(ASTSymbolRule.class,
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
    return rootLink;
  }

}
