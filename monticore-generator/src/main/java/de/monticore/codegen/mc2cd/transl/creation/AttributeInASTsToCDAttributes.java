/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class AttributeInASTsToCDAttributes implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTASTRule, ASTCDClass> link : rootLink.getLinks(ASTASTRule.class,
        ASTCDClass.class)) {
      for (ASTAdditionalAttribute attributeInAST : ASTNodes.getSuccessors(link.source(),
          ASTAdditionalAttribute.class)) {
        ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
        link.target().getCDAttributeList().add(cdAttribute);
        new Link<>(attributeInAST, cdAttribute, link);
      }
    }
    
    for (Link<ASTASTRule, ASTCDInterface> link : rootLink.getLinks(ASTASTRule.class,
        ASTCDInterface.class)) {
      for (ASTAdditionalAttribute attributeInAST : ASTNodes.getSuccessors(link.source(),
          ASTAdditionalAttribute.class)) {
        ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
        link.target().getCDAttributeList().add(cdAttribute);
        new Link<>(attributeInAST, cdAttribute, link);
      }
    }
    
    return rootLink;
  }
  
}
