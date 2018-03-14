/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import java.util.Optional;

import javax.annotation.Nullable;

import de.monticore.CommonModelingLanguage;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class MontiCoreGrammarLanguage extends CommonModelingLanguage {

  public static final String FILE_ENDING = "mc4";

  public MontiCoreGrammarLanguage() {
    super("Essential Grammar Language", FILE_ENDING);
    
    addResolvingFilter(CommonResolvingFilter.create(MCGrammarSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(MCProdSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(MCProdComponentSymbol.KIND));
    addResolvingFilter(CommonResolvingFilter.create(MCProdAttributeSymbol.KIND));
  }
  
  @Override
  public Grammar_WithConceptsParser getParser() {
    return new Grammar_WithConceptsParser();
  }
  
  @Override
  public Optional<MontiCoreGrammarSymbolTableCreator> getSymbolTableCreator(
      ResolvingConfiguration resolvingConfiguration, @Nullable MutableScope enclosingScope) {
    return Optional.of(new MontiCoreGrammarSymbolTableCreator(
        resolvingConfiguration, enclosingScope));
  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new MontiCoreGrammarModelLoader(this);
  }
}
