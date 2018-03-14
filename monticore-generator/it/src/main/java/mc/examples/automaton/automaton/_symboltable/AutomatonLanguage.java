/* (c) https://github.com/MontiCore/monticore */

package mc.examples.automaton.automaton._symboltable;

import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class AutomatonLanguage extends AutomatonLanguageTOP {
  public static final String FILE_ENDING = "aut";

  public AutomatonLanguage() {
    super("Automaton Language", FILE_ENDING);

    addResolvingFilter(CommonResolvingFilter.create(AutomatonSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(StateSymbol.KIND));
  }

  @Override
  protected AutomatonModelLoader provideModelLoader() {
    return new AutomatonModelLoader(this);
  }
}
