package mc.feature.referencesymbol.reference._symboltable;

public class ReferenceLanguage extends ReferenceLanguageTOP {
  public static final String FILE_ENDING = "ref";

  public ReferenceLanguage() {
    super("ReferenceModel", FILE_ENDING);

  }

  @Override
  protected ReferenceModelLoader provideModelLoader() {
    return new ReferenceModelLoader(this);  }
}
