package mc.feature.hwc.tagging2._ast;

import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.Joiners;
import mc.feature.hwc.tagging2._ast.ASTScopeTOP;

/**
 * Created by MichaelvonWenckstern on 14.06.2016.
 */
public interface ASTScope extends ASTScopeTOP {
  ASTQualifiedName getQualifiedName();
  default String getQualifiedNameAsString() {
    return Joiners.DOT.join(getQualifiedName().getParts());
  }
}
