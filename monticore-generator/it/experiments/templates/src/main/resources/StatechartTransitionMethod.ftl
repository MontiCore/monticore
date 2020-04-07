<#-- (c) https://github.com/MontiCore/monticore -->
    public void ${ast.getInput()?uncap_first}(){
        currentState.handle${ast.getInput()?cap_first}((${glex.getGlobalVar("modelName")})this);
    }
