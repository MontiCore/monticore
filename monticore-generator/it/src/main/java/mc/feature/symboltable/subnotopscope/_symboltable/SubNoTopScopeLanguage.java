package mc.feature.symboltable.subnotopscope._symboltable;

import mc.feature.symboltable.notopscope._symboltable.NoTopScopeModelLoader;

public class SubNoTopScopeLanguage extends SubNoTopScopeLanguageTOP {
  public static final String FILE_ENDING = "st";

  public SubNoTopScopeLanguage() {
    super("SubNoTopScope", FILE_ENDING);
  }

  @Override
  protected SubNoTopScopeModelLoader provideModelLoader() {
    return new SubNoTopScopeModelLoader(this);
  }
}
