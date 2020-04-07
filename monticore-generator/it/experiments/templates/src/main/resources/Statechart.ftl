<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initialState","transitions","states","className")}
public <#if className?ends_with("TOP")>abstract </#if>class ${className?cap_first}{

    ${tc.include("StatechartStateAttributes.ftl",states)}

    protected AbstractState currentState = ${glex.getGlobalVar("modelName")?cap_first}Factory.get${initialState.getName()?cap_first}State();

    public void setState(AbstractState state){
        currentState = state;
    }

    ${tc.include("StatechartTransitionMethod.ftl",transitions)}

}