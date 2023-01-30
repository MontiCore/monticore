/* (c) https://github.com/MontiCore/monticore */

package cdautomata._symboltable;

import automata7._symboltable.StimulusSymbol;
import basiccd._symboltable.CDClassSymbol;
import cdandaut.CDClass2StimulusAdapter;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface ICDAutomataScope extends ICDAutomataScopeTOP {

  @Override
  default List<StimulusSymbol> resolveAdaptedStimulusLocallyMany(
      boolean foundSymbols, String name, AccessModifier m,
      Predicate<StimulusSymbol> p)
  {
    // resolve source kind
    List<CDClassSymbol> cdClasses = resolveCDClassLocallyMany(
        foundSymbols, name, m, x -> true);
    List<StimulusSymbol> stimulusSymbols  = getLocalStimulusSymbols();

    List<StimulusSymbol> adapters = new ArrayList<>();

    for (CDClassSymbol s : cdClasses) {
      // instantiate adapter
      if (!stimulusSymbols.stream().filter(v -> v instanceof CDClass2StimulusAdapter).filter(v -> ((CDClass2StimulusAdapter) v).getAdaptee().equals(s)).findAny().isPresent()) {

        CDClass2StimulusAdapter c2s = new CDClass2StimulusAdapter(s);
        if (p.test(c2s)) { // check predicate
          // add the adapter to the result
          adapters.add(c2s);
          // add the adapter to the scope
          this.add(c2s); // add adapter to scope
        }
      }
    }
    return adapters;
  }
}
