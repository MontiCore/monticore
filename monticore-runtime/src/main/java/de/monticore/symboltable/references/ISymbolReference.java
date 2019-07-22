/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.references;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

import java.util.Optional;

/**
 * Represents a symbol reference and is the super type of all symbol references. Do not
 * implement this interface directly. Instead, use one of its subtypes.
 *
 */
public interface ISymbolReference {

  /**
   * @return the reference name
   */
  String getName();

  /**
   * @return the corresponding ast node
   */
  Optional<? extends ASTNode> getAstNode();


  /**
   * @return the referenced symbol
   */
  ISymbol getReferencedSymbol();

  /**
   * @return true, if the referenced symbol exists.
   */
  boolean existsReferencedSymbol();

  /**
   * @return true, if the referenced symbol is loaded
   */
  boolean isReferencedSymbolLoaded();

  /**
   * @return the enclosing scope of the reference itself
   */
  IScope getEnclosingScope();

}
