/* (c) https://github.com/MontiCore/monticore */
package de.monticore.regex.regularexpressions.cocos;

import de.monticore.regex.regularexpressions._ast.ASTRangeQualification;
import de.monticore.regex.regularexpressions._cocos.RegularExpressionsASTRangeQualificationCoCo;
import de.se_rwth.commons.logging.Log;

public class RangeHasLowerOrUpperBound
    implements RegularExpressionsASTRangeQualificationCoCo {

  @Override
  public void check(ASTRangeQualification node) {
    if (node.isEmptyLowerBound() && node.isEmptyUpperBound()) {
      Log.errorUser("0x2E20E The range qualification at "
              + node.get_SourcePositionStart()
              + " neither has a lower or upper bound.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

}
