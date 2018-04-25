package mc.feature.referencesymbol.reference._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;

public class ReferenceLanguage extends ReferenceLanguageTOP {
  public static final String FILE_ENDING = "ref";

  public ReferenceLanguage() {
    super("ReferenceModel", FILE_ENDING);

  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new ReferenceModelLoader(this);  }
}
