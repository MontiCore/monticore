/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
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
        ASTCDInterface cdInterface = CD4AnalysisMill.cDInterfaceBuilder().
                setModifier(CD4AnalysisMill.modifierBuilder().setPublic(true).build())
                .uncheckedBuild();
        link.target().addCDElement(cdInterface);
        new Link<>(interfaceProd, cdInterface, link);
      }
    }
    return rootLink;
  }
  
}
