/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import java.util.function.UnaryOperator;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.Link;

/**
 * Creates the CDDefinition corresponding to the ASTMCGrammar
 * 
 * @author Sebastian Oberhoff
 */
class GrammarToCDDefinition implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTMCGrammar, ASTCDCompilationUnit> link : rootLink.getLinks(ASTMCGrammar.class,
        ASTCDCompilationUnit.class)) {
      ASTCDDefinition cdDefinition = CD4AnalysisNodeFactory.createASTCDDefinition();
      link.target().setCDDefinition(cdDefinition);
      new Link<>(link.source(), cdDefinition, link);
    }
    
    return rootLink;
  }
}
