/* (c) https://github.com/MontiCore/monticore */
package automata._symboltable;

import automata.AutomataMill;
import com.google.common.collect.Lists;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;

public class MyStateResolver implements IStateSymbolResolver{
  @Override public List<StateSymbol> resolveAdaptedStateSymbol(boolean foundSymbols, String name,
      AccessModifier modifier, Predicate<StateSymbol> predicate) {
    return Lists.newArrayList(AutomataMill.stateSymbolBuilder().setName("Dummy").build());
  }
}
