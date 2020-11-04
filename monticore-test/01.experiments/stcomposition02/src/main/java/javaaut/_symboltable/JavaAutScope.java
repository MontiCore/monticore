/* (c) https://github.com/MontiCore/monticore */

package javaaut._symboltable;

import automata5._symboltable.AutomatonSymbol;
import basicjava._symboltable.MethodSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JavaAutScope extends JavaAutScopeTOP {

  public JavaAutScope() {
    super();
  }

  public JavaAutScope(boolean shadowing) {
    super(shadowing);
  }

  public JavaAutScope(IJavaAutScope enclosingScope) {
    super(enclosingScope);
  }

  public JavaAutScope(IJavaAutScope enclosingScope, boolean shadowing) {
    super(enclosingScope, shadowing);
  }

  @Override public List<MethodSymbol> resolveAdaptedMethodLocallyMany(boolean foundSymbols,
      String name, AccessModifier modifier, Predicate<MethodSymbol> predicate) {
    List<AutomatonSymbol> automatonSymbols = resolveAutomatonLocallyMany(foundSymbols, name,
        modifier, x -> true);
    return automatonSymbols.stream().map(s -> new Automaton2MethodAdapter(s)).collect(Collectors.toList());
  }
}
