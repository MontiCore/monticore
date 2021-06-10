/* (c) https://github.com/MontiCore/monticore */

package automata._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;

import java.util.Collections;
import java.util.Set;

public class AutomataGlobalScope extends AutomataGlobalScopeTOP {

  public AutomataGlobalScope(MCPath symbolPath, String modelFileExtension) {
    super(symbolPath, modelFileExtension);
  }

  public AutomataGlobalScope(){
    super();
  }

  @Override
  public Set<String> calculateModelNamesForState(String name) {
    // e.g., if p.Automaton.State, return p.Automaton
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }

    return Collections.emptySet();
  }

  @Override public AutomataGlobalScope getRealThis() {
    return this;
  }
}
