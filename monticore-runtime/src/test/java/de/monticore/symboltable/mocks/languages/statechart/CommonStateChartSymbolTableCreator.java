/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart;

import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ResolvingConfiguration;

public class CommonStateChartSymbolTableCreator extends CommonSymbolTableCreator implements StateChartLanguageSymbolTableCreator {


  public CommonStateChartSymbolTableCreator(ResolvingConfiguration resolverConfig, Scope
      enclosingScope) {
    super(resolverConfig, enclosingScope);
  }




}
