package de.monticore.tf.runtime.inc;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.function.Supplier;

public class DelayedNodeSupplier<Y extends ISymbol> implements Supplier<ASTNode> {

  protected final Supplier<Optional<Y>> symbolSupplier;

  public DelayedNodeSupplier(Supplier<Optional<Y>> symbolSupplier) {
    this.symbolSupplier = symbolSupplier;
  }

  @Override
  public ASTNode get() {
    Optional<Y> targetSymbol = this.symbolSupplier.get();
    if (targetSymbol.isEmpty()) {
      Log.error("Could not resolve symbol");
    }
    else if (!targetSymbol.get().isPresentAstNode()) {
      Log.error("ASTNode not present");
    }
    return targetSymbol.get().getAstNode();
  }
}
