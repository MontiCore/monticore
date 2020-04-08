<#-- (c) https://github.com/MontiCore/monticore -->
  public void handle${ast.getInput()?cap_first}(${modelName} model){
      model.setState(${modelName}.${ast.getTo()?uncap_first});
  }
