/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.grammar.LexNamer;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

public class EnumTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTEnumProd, ASTCDEnum> link : rootLink
        .getLinks(ASTEnumProd.class, ASTCDEnum.class)) {
      for (ASTConstant constant : link.source().getConstantList()) {
        String name;
        if (constant.isPresentUsageName()) {
          name = constant.getUsageName();
        } else {
           name = constant.getName();
        }
        final String goodName = LexNamer.createSimpleGoodName(name);
        ASTCDEnumConstant enumConstant = CD4AnalysisMill.cDEnumConstantBuilder().
                setName(goodName).uncheckedBuild();
        boolean constantAlreadyExists = link.target().getCDEnumConstantList().stream()
            .filter(existing -> existing.getName().equals(goodName))
            .findAny().isPresent();
        if (!constantAlreadyExists) {
          link.target().getCDEnumConstantList().add(enumConstant);
        }
      }
    }
    return rootLink;
  }
}
