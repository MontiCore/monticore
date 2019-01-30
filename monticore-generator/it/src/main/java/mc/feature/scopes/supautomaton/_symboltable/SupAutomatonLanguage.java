package mc.feature.scopes.supautomaton._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;

public class SupAutomatonLanguage extends SupAutomatonLanguageTOP{

  public static final String FILE_ENDING = "aut";

  public SupAutomatonLanguage() {
    super("SupAutomatonModel", FILE_ENDING);

  }
  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return null;
  }
}
