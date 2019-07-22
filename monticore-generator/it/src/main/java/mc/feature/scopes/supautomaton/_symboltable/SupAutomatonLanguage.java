package mc.feature.scopes.supautomaton._symboltable;

public class SupAutomatonLanguage extends SupAutomatonLanguageTOP{

  public static final String FILE_ENDING = "aut";

  public SupAutomatonLanguage() {
    super("SupAutomatonModel", FILE_ENDING);

  }
  @Override
  protected SupAutomatonModelLoader provideModelLoader() {
    return new SupAutomatonModelLoader(this);
  }
}
