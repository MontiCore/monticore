// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTKeyTerminal;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class KeyTerminalsToCDAttributes implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    
    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {
      for (ASTKeyTerminal terminal : ASTNodes.getSuccessors(link.source(),
          ASTKeyTerminal.class)) {
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
