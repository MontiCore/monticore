/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external.composite._symboltable;

import de.monticore.EmbeddingModelingLanguage;
import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.embedding.external.composite._parser.CompositeParser;
import mc.embedding.external.embedded._symboltable.EmbeddedLanguage;
import mc.embedding.external.host._symboltable.HostLanguage;

import java.util.Optional;

public class CompositeLanguage extends EmbeddingModelingLanguage {

  public static final String FILE_ENDING = HostLanguage.FILE_ENDING;

  public CompositeLanguage() {
    super("Composite Language", FILE_ENDING, new HostLanguage(), new EmbeddedLanguage());

    modelLoader =  provideModelLoader();
    addResolvingFilter(new Text2ContentResolvingFilter());
  }

  @Override public CompositeParser getParser() {
    return new CompositeParser();
  }

  @Override public Optional<CompositeSymbolTableCreator> getSymbolTableCreator(
      ResolvingConfiguration resolvingConfiguration, MutableScope enclosingScope) {
    return Optional.of(new CompositeSymbolTableCreator(resolvingConfiguration, enclosingScope));
  }

  @Override protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new CompositeModelLoader(this);
  }
}
