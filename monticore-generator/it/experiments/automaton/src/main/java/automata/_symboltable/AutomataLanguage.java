/* (c) https://github.com/MontiCore/monticore */
package automata._symboltable;

import com.google.common.collect.ImmutableSet;
import de.se_rwth.commons.Names;

import java.util.Collections;
import java.util.Set;

public class AutomataLanguage extends AutomataLanguageTOP {
  public static final String FILE_ENDING = "aut";
  
  public AutomataLanguage() {
    super("Automata Language", FILE_ENDING);

  }
  
  @Override
  protected Set<String> calculateModelNamesForState(String name) {
    // e.g., if p.Automaton.State, return p.Automaton
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }
    
    return Collections.emptySet();
  }
  

  @Override
  protected AutomataModelLoader provideModelLoader() {
    return new AutomataModelLoader(this);
  }
}
