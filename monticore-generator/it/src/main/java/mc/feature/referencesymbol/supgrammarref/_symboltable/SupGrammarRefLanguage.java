package mc.feature.referencesymbol.supgrammarref._symboltable;

public class SupGrammarRefLanguage extends SupGrammarRefLanguageTOP {
  public static final String FILE_ENDING = "ref";

  public SupGrammarRefLanguage() {
    super("SupReferenceModel", FILE_ENDING);

  }

  @Override
  protected SupGrammarRefModelLoader provideModelLoader() {
    return new SupGrammarRefModelLoader(this);  }
}
