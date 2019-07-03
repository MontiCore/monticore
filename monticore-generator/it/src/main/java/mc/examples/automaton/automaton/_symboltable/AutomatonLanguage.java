/* (c) https://github.com/MontiCore/monticore */

package mc.examples.automaton.automaton._symboltable;

public class AutomatonLanguage extends AutomatonLanguageTOP {
  public static final String FILE_ENDING = "aut";

  public AutomatonLanguage() {
    super("Automaton Language", FILE_ENDING);

  }

  @Override
  protected AutomatonModelLoader provideModelLoader() {
    return new AutomatonModelLoader(this);
  }
}
