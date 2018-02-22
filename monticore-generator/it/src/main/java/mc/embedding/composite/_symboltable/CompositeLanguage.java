/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.composite._symboltable;

import de.monticore.EmbeddingModelingLanguage;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.embedding.composite._parser.CompositeParser;
import mc.embedding.embedded._symboltable.EmbeddedLanguage;
import mc.embedding.host._symboltable.HostLanguage;

import java.util.Optional;

public class CompositeLanguage extends EmbeddingModelingLanguage {

  public static final String FILE_ENDING = HostLanguage.FILE_ENDING;

  public CompositeLanguage() {
    super("Composite Language", FILE_ENDING, new HostLanguage(), new EmbeddedLanguage());

    modelLoader =  provideModelLoader();
    addResolvingFilter(new Text2ContentResolvingFilter());
  }

  @Override public MCConcreteParser getParser() {
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
