/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolTableCreator;
import de.monticore.symboltable.resolving.ResolvingFilter;

import java.util.Collection;
import java.util.Optional;

/**
 * Super interface for languages. Provides access to language-related functionality,
 * like parsing, symbol table creation, model analysis and code generation.
 *
 * @author  Pedram Mir Seyed Nazari
 * 
 */
public interface ModelingLanguage {

  /**
   * @return the name of the modeling language, e.g., "MontiCore Grammar Language"
   */
  String getName();

  /**
   * @return the file ending, e.g., ".cd"
   */
  String getFileExtension();

  /**
   * @return the parser for models of this language
   */
  MCConcreteParser getParser();

  /**
   * @return default resolvering filters for this language
   */
  Collection<ResolvingFilter<? extends Symbol>> getResolvingFilters();

  /**
   *
   *
   * @param resolvingConfiguration the {@link ResolvingConfiguration}
   * @param enclosingScope the enclosing scope of the top level symbol's spanned scope. In other
   *                       words, the scope in which the top level symbol should be defined.
   * @return the {@link de.monticore.symboltable.CommonSymbolTableCreator} for this language.
   */
  // TODO PN change to mandatory
  Optional<? extends SymbolTableCreator> getSymbolTableCreator
  (ResolvingConfiguration resolvingConfiguration, MutableScope enclosingScope);

  ModelingLanguageModelLoader<? extends ASTNode> getModelLoader();

  ModelNameCalculator getModelNameCalculator();

}
