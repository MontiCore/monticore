/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import java.util.function.UnaryOperator;

import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

/**
 * Creates the ASTCDAttributes corresponding to NonTerminals
 * 
 * @author Sebastian Oberhoff
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
