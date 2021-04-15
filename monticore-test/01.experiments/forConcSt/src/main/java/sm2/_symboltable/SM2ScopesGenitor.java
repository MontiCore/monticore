/* (c) https://github.com/MontiCore/monticore */
package sm2._symboltable;

import de.se_rwth.commons.logging.Log;

public class SM2ScopesGenitor extends SM2ScopesGenitorTOP {


  public SM2ScopesGenitor(){
    super();
  }

  @Override
  public void initStateHP1(final StateSymbol stateSymbol) {
    Log.info("StateSymbol defined for " + stateSymbol.getName(), "SM2ScopsGenitor");
  }

}
