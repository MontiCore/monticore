/* (c) https://github.com/MontiCore/monticore */

package cdautomata._symboltable;

import automata7._symboltable.StimulusSymbol;
import cdandaut.CDClass2StimulusAdapter;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ICDAutomataScope extends ICDAutomataScopeTOP {

  @Override
  default List<StimulusSymbol> resolveAdaptedStimulusLocallyMany(
      boolean foundSymbols, String name, AccessModifier m,
      Predicate<StimulusSymbol> p) {
    return resolveCDClassLocallyMany(foundSymbols, name,
        m, x -> true).stream()
        .map(s -> new CDClass2StimulusAdapter(s))
        .collect(Collectors.toList());
  }
}
