/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

public abstract class CommonScopeSpanningSymbol extends CommonSymbol implements ScopeSpanningSymbol {

  private final Scope spannedScope;

  /**
   * @see CommonSymbol#CommonSymbol(String, SymbolKind)
   */
  public CommonScopeSpanningSymbol(String name, SymbolKind kind) {
    super(name, kind);

    spannedScope = createSpannedScope();
    spannedScope.setSpanningSymbol(this);
  }

  /**
   * Factory method for creating the scope spanned by this symbol. By default, a
   * {@link CommonScope} is spanned.
   *
   * @return the (newly) created scope spanned by this symbol.
   */
  protected Scope createSpannedScope() {
    return new CommonScope(true);
  }

  @Override
  public Scope getSpannedScope() {
    return spannedScope;
  }


  @Override
  public void setEnclosingScope(Scope scope) {
    super.setEnclosingScope(scope);
    getSpannedScope().setEnclosingScope(scope);
  }

}
