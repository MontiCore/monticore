/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import java.util.function.UnaryOperator;

import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

public class TerminalsToCDAttributes implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      for (ASTTerminal terminal : ASTNodes.getSuccessors(link.source(),
          ASTTerminal.class)) {
        if (terminal.isPresentUsageName()) {
          ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
          link.target().getCDAttributeList().add(cdAttribute);
          new Link<>(terminal, cdAttribute, link);
        }
      }
    }
    return rootLink;
  }
  
}
