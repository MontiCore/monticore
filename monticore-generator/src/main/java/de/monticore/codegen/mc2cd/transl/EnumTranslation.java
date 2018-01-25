/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
