package mc.feature.referencesymbol.supgrammarref._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import mc.feature.referencesymbol.reference._symboltable.RandResolvingFilter;
import mc.feature.referencesymbol.reference._symboltable.TestResolvingFilter;

public class SupGrammarRefLanguage extends SupGrammarRefLanguageTOP {
  public static final String FILE_ENDING = "ref";

  public SupGrammarRefLanguage() {
    super("SupReferenceModel", FILE_ENDING);

  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new SupGrammarRefModelLoader(this);  }

  protected void initResolvingFilters() {
    addResolvingFilter(new RandResolvingFilter());
    addResolvingFilter(new TestResolvingFilter());
  }
}
