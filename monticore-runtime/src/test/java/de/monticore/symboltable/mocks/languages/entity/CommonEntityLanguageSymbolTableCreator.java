/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;

public class CommonEntityLanguageSymbolTableCreator extends CommonSymbolTableCreator implements EntityLanguageSymbolTableCreator {

  public CommonEntityLanguageSymbolTableCreator(final ResolvingConfiguration resolverConfig, final
  MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

}
