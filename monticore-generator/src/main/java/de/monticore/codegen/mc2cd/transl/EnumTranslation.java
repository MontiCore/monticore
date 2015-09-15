/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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

import de.monticore.grammar.LexNamer;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * @author Sebastian Oberhoff
 */
public class EnumTranslation implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTEnumProd, ASTCDEnum> link : rootLink
        .getLinks(ASTEnumProd.class, ASTCDEnum.class)) {
      link.source().getConstants().stream()
          .map(ASTConstant::getName)
          .map(name -> {
            ASTCDEnumConstant enumConstant = CD4AnalysisNodeFactory.createASTCDEnumConstant();
            enumConstant.setName(LexNamer.createGoodName(name));
            return enumConstant;
          })
          .forEach(cdEnumConstant -> link.target().getCDEnumConstants().add(cdEnumConstant));
    }
    return rootLink;
  }
}
