/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc.tagging2._ast;

import de.se_rwth.commons.Joiners;
import mc.grammar.types.ittesttypes._ast.ASTQualifiedName;

/**
 * Created by MichaelvonWenckstern on 14.06.2016.
 */
public interface ASTScope_ extends ASTScope_TOP {
  ASTQualifiedName getQualifiedName();
  default String getQualifiedNameAsString() {
    return Joiners.DOT.join(getQualifiedName().getPartList());
  }
}
