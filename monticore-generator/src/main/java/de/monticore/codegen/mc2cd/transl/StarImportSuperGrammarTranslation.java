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

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.TypesMill;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

/**
 * Adds the fully qualified names of supergrammars as star-imports to the ASTCDCompilationUnit of
 * the CD AST.
 * 
 * @author Sebastian Oberhoff
 */
public class StarImportSuperGrammarTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    ASTMCGrammar grammar = rootLink.source();
    if (grammar.getSymbol().isPresent()) {
      MCGrammarSymbol symbol = (MCGrammarSymbol) grammar.getSymbol().get();
      for (MCGrammarSymbol superSymbol : symbol.getSuperGrammarSymbols()) {
        List<String> names = Arrays.asList(superSymbol.getFullName().split("\\."));
        ASTImportStatement importStatement = TypesMill.importStatementBuilder().setImportList(names)
            .setStar(true).build();
        ;
        rootLink.target().getImportStatementList().add(importStatement);
      }
    }  
    return rootLink;
  }
  
}
