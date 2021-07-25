/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class EnumProdsToCDEnums implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTMCGrammar, ASTCDDefinition> link : rootLink.getLinks(ASTMCGrammar.class,
            ASTCDDefinition.class)) {
      createEnumProdToCDEnumLinks(link);
    }
    return rootLink;
  }

  protected void createEnumProdToCDEnumLinks(Link<ASTMCGrammar, ASTCDDefinition> link) {
    for (ASTEnumProd enumProd : link.source().getEnumProdList()) {
      ASTCDEnum cdEnum = CD4AnalysisMill.cDEnumBuilder().
              setModifier(CD4AnalysisMill.modifierBuilder().build()).uncheckedBuild();
      link.target().addCDElement(cdEnum);
      new Link<>(enumProd, cdEnum, link);
    }
  }
}
