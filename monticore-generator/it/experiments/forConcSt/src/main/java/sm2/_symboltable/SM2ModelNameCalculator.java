/* (c) Monticore license: https://github.com/MontiCore/monticore */

package sm2._symboltable;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import de.se_rwth.commons.Names;

public class SM2ModelNameCalculator extends SM2ModelNameCalculatorTOP {

  @Override
  protected Set<String> calculateModelNamesForState(String name) {
    // e.g., if p.SM2.State, return p.SM2
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }

    return Collections.emptySet();
  }
}
