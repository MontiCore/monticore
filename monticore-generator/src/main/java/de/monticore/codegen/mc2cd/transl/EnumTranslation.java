/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import java.util.function.UnaryOperator;

import de.monticore.grammar.LexNamer;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.Link;

/**
 * @author Sebastian Oberhoff, Robert Heim
 */
public class EnumTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTEnumProd, ASTCDEnum> link : rootLink
        .getLinks(ASTEnumProd.class, ASTCDEnum.class)) {
      for (ASTConstant constant : link.source().getConstantList()) {
        String name = constant.getHumanNameOpt().orElse(constant.getName());
        final String goodName = LexNamer.createGoodName(name);
        ASTCDEnumConstant enumConstant = CD4AnalysisNodeFactory.createASTCDEnumConstant();
        enumConstant.setName(goodName);
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
