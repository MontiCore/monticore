/* (c) Monticore license: https://github.com/MontiCore/monticore */
package sm2.cocos;

import sm2._cocos.SM2CoCoChecker;

public class SM2CoCos {
  
  public SM2CoCoChecker getCheckerForAllCoCos() {
    final SM2CoCoChecker checker = new SM2CoCoChecker();
    checker.addCoCo(new AtLeastOneInitialState());
    checker.addCoCo(new StateNameStartsWithCapitalLetter());
    // TODO PN uncomment
    // checker.addCoCo(new TransitionSourceExists());
    
    return checker;
  }
}
