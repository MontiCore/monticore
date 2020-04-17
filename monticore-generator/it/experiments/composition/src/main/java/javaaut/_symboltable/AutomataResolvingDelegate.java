/* (c) https://github.com/MontiCore/monticore */

package javaaut._symboltable;

import automata5._symboltable.Automata5GlobalScope;
import automata5._symboltable.Automata5Language;
import automata5._symboltable.Automata5SymTabMill;
import automata5._symboltable.AutomatonSymbol;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;

import java._symboltable.IMethodSymbolResolvingDelegate;
import java._symboltable.MethodSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class AutomataResolvingDelegate implements IMethodSymbolResolvingDelegate {

  Automata5GlobalScope automataGS;

  public AutomataResolvingDelegate(ModelPath mp){
    automataGS = Automata5SymTabMill.automata5GlobalScopeBuilder()
        .setModelPath(mp) //hand over modelpath
        .setAutomata5Language(new Automata5Language()) //will be removed soon
        .build();
  }


  @Override public List<MethodSymbol> resolveAdaptedMethodSymbol(boolean foundSymbols, String name,
      AccessModifier modifier, Predicate<MethodSymbol> predicate) {
    List<MethodSymbol> result = new ArrayList<>();
    Optional<AutomatonSymbol> automatonSymbol = automataGS.resolveAutomaton(name, modifier);
    if(automatonSymbol.isPresent()){
      result.add(new Automaton2MethodAdapter(automatonSymbol.get()));
    }
    return result;
  }
}
