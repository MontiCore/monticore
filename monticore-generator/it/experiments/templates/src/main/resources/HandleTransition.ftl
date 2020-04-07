<#-- (c) https://github.com/MontiCore/monticore -->
    public void handle${ast.getInput()?cap_first}(${glex.getGlobalVar("modelName")?cap_first} model){
        model.setState(${glex.getGlobalVar("modelName")?cap_first}.${ast.getTo()?uncap_first});
    }
