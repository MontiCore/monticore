/* (c) https://github.com/MontiCore/monticore */
package sm2._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Names;

import java.util.Collections;
import java.util.Set;

public class SM2GlobalScope extends SM2GlobalScopeTOP {

  public SM2GlobalScope(ModelPath mp) {
    super(mp, "aut");
  }

  @Override public SM2GlobalScope getRealThis() {
    return this;
  }

  @Override
  public Set<String> calculateModelNamesForState(String name) {
    // e.g., if p.SM2.State, return p.SM2
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }
    return Collections.emptySet();
  }
 
}
