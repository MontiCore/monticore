/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class InterfaceProdsToCDInterfaces implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTMCGrammar, ASTCDDefinition> link : rootLink.getLinks(ASTMCGrammar.class,
        ASTCDDefinition.class)) {
      for (ASTInterfaceProd interfaceProd : link.source().getInterfaceProdList()) {
        ASTCDInterface cdInterface = CD4AnalysisNodeFactory.createASTCDInterface();
        cdInterface.setModifier(CD4AnalysisNodeFactory.createASTModifier());
        link.target().getCDInterfaceList().add(cdInterface);
        new Link<>(interfaceProd, cdInterface, link);
      }
    }
    return rootLink;
  }
  
}
