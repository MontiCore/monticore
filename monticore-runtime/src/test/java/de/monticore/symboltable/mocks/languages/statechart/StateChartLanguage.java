/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart;

import de.monticore.CommonModelingLanguage;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class StateChartLanguage extends CommonModelingLanguage {
  
  public StateChartLanguage() {
    super("StateChart Language Mock", "sc");

    addResolvingFilter(CommonResolvingFilter.create(StateChartSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(StateSymbol.KIND));

  }
  
  @Override
  public MCConcreteParser getParser() {
    return new StateChartParserMock();
  }


  @Override
  public Optional<StateChartLanguageSymbolTableCreator> getSymbolTableCreator(
      final ResolvingConfiguration resolvingConfiguration, final MutableScope enclosingScope) {
    return Optional.of(new CommonStateChartSymbolTableCreator(Log.errorIfNull(resolvingConfiguration),
        Log.errorIfNull(enclosingScope)));
  }

  @Override
  public StateChartLanguageModelLoader getModelLoader() {
    return (StateChartLanguageModelLoader) super.getModelLoader();
  }

  @Override
  protected StateChartLanguageModelLoader provideModelLoader() {
    return new StateChartLanguageModelLoader(this);
  }
}
