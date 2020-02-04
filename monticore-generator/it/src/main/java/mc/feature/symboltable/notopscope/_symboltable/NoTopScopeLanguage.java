package mc.feature.symboltable.notopscope._symboltable;

import mc.feature.referencesymbol.reference._symboltable.ReferenceModelLoader;

public class NoTopScopeLanguage extends NoTopScopeLanguageTOP {


  public static final String FILE_ENDING = "st";

  public NoTopScopeLanguage() {
    super("NoTopScope", FILE_ENDING);
  }

  @Override
  protected NoTopScopeModelLoader provideModelLoader() {
    return new NoTopScopeModelLoader(this);
  }
}
