// (c) https://github.com/MontiCore/monticore
package de.monticore.types.check;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.ISymbol;

import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * Stores information where a SymTypeExpression comes from.
 * This additional information can be used in CoCos/code-generation, etc.
 * <p>
 * There will be no info available if using TypeCheck1.
 */
public class SymTypeSourceInfo {

  protected Optional<ISymbol> sourceSymbol;

  /**
   * Usage of a WeakReference over an Optional is mostly symbolic;
   * NEVER rely on this information, but if it is available, it may be of use.
   */
  protected WeakReference<ASTNode> sourceNode;

  public SymTypeSourceInfo() {
    this.sourceSymbol = Optional.empty();
    this.sourceNode = new WeakReference<>(null);
  }

  /**
   * deep clone
   */
  public SymTypeSourceInfo(SymTypeSourceInfo other) {
    this.sourceSymbol = other.sourceSymbol;
    this.sourceNode = other.sourceNode;
  }

  /**
   * Source symbol of the SymTypeExpression, e.g.,
   * if a variable "int x" is resolved, the type symbol will be int,
   * but the source symbol will be the variable symbol x.
   * <p>
   * This information can be used for CoCos, code generators, etc.
   * <p>
   * WARNING: modifying the SymTypeExpression (e.g., normalization)
   * can remove this information. This is deliberate.
   * <p>
   * This replaces getDefiningSymbol() of TypeCheck1.
   */
  public Optional<ISymbol> getSourceSymbol() {
    return this.sourceSymbol;
  }

  public void setSourceSymbol(ISymbol source) {
    this.sourceSymbol = Optional.of(source);
  }

  /**
   * Source ASTNode of the SymTypeExpression, e.g.,
   * if the SymTypeExpression has been calculated as the type of a specific ASTNode,
   * this ASTNode may(!) be returned (no other ASTNode is returned).
   * <p>
   * This is ONLY meant to be used to create better log messages!
   * As (especially during type inference) SymTypeExpressions
   * are moved around, cloned, and modified A LOT,
   * therefore, it cannot be assumed that this holds reliable information.
   * The information should still be useful in most cases, though.
   */
  public Optional<ASTNode> getSourceNode() {
    return Optional.ofNullable(this.sourceNode.get());
  }

  /**
   * first and foremost called in Type4Ast
   */
  public void _internal_setSourceNode(ASTNode source) {
    this.sourceNode = new WeakReference<>(source);
  }

}
