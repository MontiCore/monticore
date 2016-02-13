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

package de.monticore.symboltable.mocks.languages.statechart;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.mocks.languages.statechart.asts.ASTStateChart;
import de.monticore.symboltable.mocks.languages.statechart.asts.ASTStateChartCompilationUnit;
import de.se_rwth.commons.Names;

/**
 * TODO: Write me!
 *
 * @author Pedram Mir Seyed Nazari
 * @version $Revision$, $Date$
 */
public class StateChartParserMock extends MCConcreteParser {

  @Override
  public Optional<? extends ASTNode> parse(String path) throws IOException {
    String packageName = Names.getPackageFromPath(path);

    ASTStateChartCompilationUnit compilationUnit = new ASTStateChartCompilationUnit();
    compilationUnit.setPackageName(packageName);

    ASTStateChart stateChartNode = new ASTStateChart();
    compilationUnit.setStateChart(stateChartNode);

    stateChartNode.setName(Names.getSimpleName(path));
    return Optional.of(compilationUnit);
  }

  @Override
  public Optional<? extends ASTNode> parse(Reader reader) throws IOException {
    return null;
  }

}
