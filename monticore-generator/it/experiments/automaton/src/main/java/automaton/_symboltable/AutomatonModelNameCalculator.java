/* (c) Monticore license: https://github.com/MontiCore/monticore */
package automaton._symboltable;

import com.google.common.collect.ImmutableSet;
import de.se_rwth.commons.Names;

import java.util.Collections;
import java.util.Set;

public class AutomatonModelNameCalculator extends AutomatonModelNameCalculatorTOP {

  @Override
  protected Set<String> calculateModelNamesForState(String name) {
    // e.g., if p.Automaton.State, return p.Automaton
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }

    return Collections.emptySet();
  }
}
