<#-- (c) https://github.com/MontiCore/monticore -->
  /**
   * Signature of handle${ast.getInput()?cap_first}
   * The method is to be overwritten in each 
   * concrete subclass 
   * @param ${modelName} sc
   */
  abstract void handle${ast.getInput()?cap_first}(${modelName} sc);

