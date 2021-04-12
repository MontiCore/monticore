/* (c) https://github.com/MontiCore/monticore */
package automata._symboltable;

import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public interface IAutomataArtifactScope extends IAutomataArtifactScopeTOP {

  default public List<StateSymbol> resolveStateDownMany (boolean foundSymbols, String name, de.monticore.symboltable.modifiers.AccessModifier modifier, java.util.function.Predicate<automata._symboltable.StateSymbol> predicate)  {
    if (isStateSymbolsAlreadyResolved()) {
      return new ArrayList<>();
    }

    setStateSymbolsAlreadyResolved(true);
    final List<automata._symboltable.StateSymbol> resolvedSymbols = this.resolveStateLocallyMany(foundSymbols, name, modifier, predicate);
    foundSymbols = foundSymbols || !resolvedSymbols.isEmpty();
    setStateSymbolsAlreadyResolved(false);

    final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + "StateSymbol"
        + "\") in scope \"" + (isPresentName() ? getName() : "") + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolvedSymbols.size() + " (local)", "");
    if (resolvedSymbols.isEmpty()) {
      for (automata._symboltable.IAutomataScope subScope : getSubScopes()) {
        final List<automata._symboltable.StateSymbol> resolvedFromSub = subScope
            .continueAsStateSubScope(foundSymbols, name, modifier, predicate);
        foundSymbols = foundSymbols || !resolvedFromSub.isEmpty();
        resolvedSymbols.addAll(resolvedFromSub);
      }
    }
    Log.trace("END " + resolveCall + ". Found #" + resolvedSymbols.size(), "");
    setStateSymbolsAlreadyResolved(false);
    return resolvedSymbols;
  }


}
