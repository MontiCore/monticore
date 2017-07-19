/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

package de.monticore.codegen.mc2cd.transl.creation;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * This class can be viewed as a special syntactic translation, whereas other translations are of
 * semantic nature. It only concerns itself with mirroring the MC AST structure over to the CD AST
 * and building the corresponding Link structure as it goes along.
 *
 * @author Sebastian Oberhoff
 */
public class CDASTCreator implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    return new GrammarToCDDefinition()
        .andThen(new ClassProdsToCDClasses())
        .andThen(new AbstractProdsToCDClasses())
        .andThen(new InterfaceProdsToCDInterfaces())
        .andThen(new EnumProdsToCDEnums())
        .andThen(new ExternalProdsToCDInterfaces())
        .andThen(new ASTRulesToCDClassesAndCDInterfaces())
        .andThen(new AttributeInASTsToCDAttributes())
        .andThen(new NonTerminalsToCDAttributes())
        .andThen(new TerminalsToCDAttributes())
        .apply(rootLink);
  }
}
