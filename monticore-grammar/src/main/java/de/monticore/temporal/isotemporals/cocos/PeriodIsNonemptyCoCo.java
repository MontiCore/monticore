package de.monticore.temporal.isotemporals.cocos;

import de.monticore.temporal.isotemporals._ast.ASTFullPeriod;
import de.monticore.temporal.isotemporals._ast.ASTISOTemporalsNode;
import de.monticore.temporal.isotemporals._cocos.ISOTemporalsASTFullPeriodCoCo;
import de.monticore.temporal.isotemporals._cocos.ISOTemporalsCoCoChecker;
import de.se_rwth.commons.logging.Log;

public class PeriodIsNonemptyCoCo implements ISOTemporalsASTFullPeriodCoCo {
  
  public static final String EMPTY_PERIOD_ERROR_CODE = "0x668BF";
  
  public static final String EMPTY_PERIOD_ERROR_MESSAGE = "%s: Empty Period Error: At least one temporal unit must be non-empty";
  
  public static void doCheck(ASTFullPeriod node) {
    ISOTemporalsCoCoChecker checker = new ISOTemporalsCoCoChecker();
    checker.addCoCo(new PeriodIsNonemptyCoCo());
    checker.checkAll((ASTISOTemporalsNode) node);
  }
  
  @Override
  public void check(ASTFullPeriod node) {
    if (!node.getPre().getSource().matches(".*\\d.*")) {
      Log.error(String.format(EMPTY_PERIOD_ERROR_MESSAGE, EMPTY_PERIOD_ERROR_CODE));
    }
  }
}
