/* (c) https://github.com/MontiCore/monticore */
package de.monticore.regex.regularexpressions._symboltable;

import de.monticore.regex.regularexpressions._ast.ASTBackReference;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class RegularExpressionsScopesGenitor
    extends RegularExpressionsScopesGenitorTOP {

  @Override
  public void visit(ASTBackReference node) {
    if (node.isPresentName() && getCurrentScope().isPresent()) {
      Optional<NamedCapturingGroupSymbol> group =
          getCurrentScope().get().resolveNamedCapturingGroup(node.getName());
      if (group.isEmpty()) {
        Log.errorUser("0x2EB2E Name " + node.getName()
                + "used in BackReference is not the name of a capturing group",
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd()
        );
      }
    }
  }

}
