/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import de.monticore.ast.ASTNode;

public interface IScopeSpanningSymbol<S extends IScope, A extends ASTNode> extends ISymbol<A> {

  /**
   * @return the scope spanned by this symbol.
   */
  S getSpannedScope();

}
