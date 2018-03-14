/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
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

  private void createEnumProdToCDEnumLinks(Link<ASTMCGrammar, ASTCDDefinition> link) {
    for (ASTEnumProd enumProd : link.source().getEnumProdList()) {
      ASTCDEnum cdEnum = CD4AnalysisNodeFactory.createASTCDEnum();
      cdEnum.setModifier(CD4AnalysisNodeFactory.createASTModifier());
      link.target().getCDEnumList().add(cdEnum);
      new Link<>(enumProd, cdEnum, link);
    }
  }
}
