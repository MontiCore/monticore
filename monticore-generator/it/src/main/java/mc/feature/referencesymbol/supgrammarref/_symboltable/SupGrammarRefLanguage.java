package mc.feature.referencesymbol.supgrammarref._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;

public class SupGrammarRefLanguage extends SupGrammarRefLanguageTOP {
  public static final String FILE_ENDING = "ref";

  public SupGrammarRefLanguage() {
    super("SupReferenceModel", FILE_ENDING);

  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new SupGrammarRefModelLoader(this);  }
}
