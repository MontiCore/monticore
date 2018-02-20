/* (c) https://github.com/MontiCore/monticore */

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
 *
 * @author Pedram Mir Seyed Nazari
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
