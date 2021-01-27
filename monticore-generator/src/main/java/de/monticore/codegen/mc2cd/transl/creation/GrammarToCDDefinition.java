/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * Creates the CDDefinition corresponding to the ASTMCGrammar
 * 
 */
public class GrammarToCDDefinition implements
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
