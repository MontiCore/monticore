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

package de.monticore.codegen.mc2cd;

import de.monticore.codegen.mc2cd.manipul.CDManipulation;
import de.monticore.codegen.mc2cd.transl.MC2CDTranslation;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.IterablePath;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.utils.Link;

import java.util.function.Function;

/**
 * This is the top-level function accepting a MC AST and taking it all the way through to the
 * finished CD AST.
 *
 * @author Sebastian Oberhoff
 */
public class MC2CDTransformation implements Function<ASTMCGrammar, ASTCDCompilationUnit> {

  private IterablePath targetPath;

  private GlobalExtensionManagement glex;

  public MC2CDTransformation(GlobalExtensionManagement glex, IterablePath targetPath) {
    this(glex);
    this.targetPath = targetPath;
  }

  public MC2CDTransformation(GlobalExtensionManagement glex) {
    this.glex = glex;
    this.targetPath = IterablePath.empty();
  }

  @Override
  public ASTCDCompilationUnit apply(ASTMCGrammar grammar) {
    Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink = new Link<>(grammar,
        CD4AnalysisNodeFactory.createASTCDCompilationUnit(), null);

    return new MC2CDTranslation(glex)
        .andThen(Link::target)
        .andThen(new CDManipulation())
        .apply(rootLink);
  }

}
