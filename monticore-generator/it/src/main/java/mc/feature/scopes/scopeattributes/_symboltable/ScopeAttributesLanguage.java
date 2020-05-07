// (c) https://github.com/MontiCore/monticore
package mc.feature.scopes.scopeattributes._symboltable;

public class ScopeAttributesLanguage extends ScopeAttributesLanguageTOP {
  public static final String FILE_ENDING = "sc";

  public ScopeAttributesLanguage() {
    super("ScopeAttributesModel", FILE_ENDING);
  }

  @Override
  protected ScopeAttributesModelLoader provideModelLoader() {
    return new ScopeAttributesModelLoader(this);
  }
}
