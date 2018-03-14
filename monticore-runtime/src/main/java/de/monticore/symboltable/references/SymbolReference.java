/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.references;

import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;

/**
 * Represents a symbol reference and is the super type of all symbol references. Do not
 * implement this interface directly. Instead, use one of its subtypes.
 *
 * @author Pedram Mir Seyed Nazari
 */
public interface SymbolReference<T extends Symbol> {

  /**
   * @return the reference name
   */
  String getName();

  /**
   * @return the corresponding ast node
   */
  Optional<ASTNode> getAstNode();

  /**
   * @param node the corresponding ast node
   */
  void setAstNode(ASTNode node);

  /**
   * @return the referenced symbol
   */
  T getReferencedSymbol();

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
  Scope getEnclosingScope();

}
