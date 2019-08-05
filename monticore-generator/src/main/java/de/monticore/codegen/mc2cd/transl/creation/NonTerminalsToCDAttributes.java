/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * Creates the ASTCDAttributes corresponding to NonTerminals
 * 
 */
class NonTerminalsToCDAttributes implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      for (ASTNonTerminal nonTerminal : ASTNodes.getSuccessors(link.source(),
          ASTNonTerminal.class)) {
        ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
        link.target().getCDAttributeList().add(cdAttribute);
        new Link<>(nonTerminal, cdAttribute, link);
      }
    }
    
    for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(ASTInterfaceProd.class,
        ASTCDInterface.class)) {
      for (ASTNonTerminal nonTerminal : ASTNodes.getSuccessors(link.source(),
          ASTNonTerminal.class)) {
        ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
        link.target().getCDAttributeList().add(cdAttribute);
        new Link<>(nonTerminal, cdAttribute, link);
      }
    }
    
    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(ASTAbstractProd.class,
        ASTCDClass.class)) {
      for (ASTNonTerminal nonTerminal : ASTNodes.getSuccessors(link.source(),
          ASTNonTerminal.class)) {
        ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
        link.target().getCDAttributeList().add(cdAttribute);
        new Link<>(nonTerminal, cdAttribute, link);
      }
    }
    
    return rootLink;
  }
}
