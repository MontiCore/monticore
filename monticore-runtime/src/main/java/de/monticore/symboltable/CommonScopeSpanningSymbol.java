/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

/**
 * @author Pedram Mir Seyed Nazari
 */
public abstract class CommonScopeSpanningSymbol extends CommonSymbol implements ScopeSpanningSymbol {

  private final MutableScope spannedScope;

  /**
   * @see CommonSymbol#CommonSymbol(String, SymbolKind)
   */
  public CommonScopeSpanningSymbol(String name, SymbolKind kind) {
    super(name, kind);

    spannedScope = createSpannedScope();
    getMutableSpannedScope().setSpanningSymbol(this);
  }

  /**
   * Factory method for creating the scope spanned by this symbol. By default, a
   * {@link CommonScope} is spanned.
   *
   * @return the (newly) created scope spanned by this symbol.
   */
  protected MutableScope createSpannedScope() {
    return new CommonScope(true);
  }

  @Override
  public Scope getSpannedScope() {
    return getMutableSpannedScope();
  }

  protected MutableScope getMutableSpannedScope() {
    return spannedScope;
  }


  @Override
  public void setEnclosingScope(MutableScope scope) {
    super.setEnclosingScope(scope);
   getMutableSpannedScope().setEnclosingScope(scope);
  }

}
