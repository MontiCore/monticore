<#-- (c) https://github.com/MontiCore/monticore -->
  /**
   * Method call delegated to the current state object
   */
  public void ${ast.getInput()?uncap_first}(){
    currentState.handle${ast.getInput()?cap_first}(getTypedThis());
  }
