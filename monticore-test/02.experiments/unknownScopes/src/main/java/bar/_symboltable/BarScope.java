/* (c) https://github.com/MontiCore/monticore */
package bar._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.symboltable.SymbolWithScopeOfUnknownKind;

import java.util.Optional;

public class BarScope extends BarScopeTOP {

  protected LinkedListMultimap<String, SymbolWithScopeOfUnknownKind> unknownSymbols = LinkedListMultimap.create();

  public BarScope() {
    super();
    this.name = Optional.empty();
  }

  public BarScope(boolean shadowing) {
    this.shadowing = shadowing;
    this.name = Optional.empty();
  }

  public void add(SymbolWithScopeOfUnknownKind symbol) {
    this.unknownSymbols.put(symbol.getName(), symbol);
    symbol.setEnclosingScope(this);
  }

  public void remove(SymbolWithScopeOfUnknownKind symbol) {
    this.unknownSymbols.remove(symbol.getName(), symbol);
  }

  public LinkedListMultimap<String, SymbolWithScopeOfUnknownKind> getUnknownSymbols() {
    return this.unknownSymbols;
  }

}
