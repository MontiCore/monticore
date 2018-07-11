/* (c) Monticore license: https://github.com/MontiCore/monticore */
package automaton._symboltable;

public class AutomatonLanguage extends AutomatonLanguageTOP {
  public static final String FILE_ENDING = "aut";
  
  public AutomatonLanguage() {
    super("Automaton Language", FILE_ENDING);

    setModelNameCalculator(new AutomatonModelNameCalculator());
  }
  

  @Override
  protected AutomatonModelLoader provideModelLoader() {
    return new AutomatonModelLoader(this);
  }
}
