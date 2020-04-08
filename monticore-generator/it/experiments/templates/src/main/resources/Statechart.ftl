<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initialState","transitions","states","className")}
public <#if className?ends_with("TOP")>abstract </#if>class ${className}{

    ${tc.include("StatechartStateAttributes.ftl",states)}

    protected Abstract${modelName}State currentState = ${modelName}Factory.get${initialState.getName()}State();

    public void setState(Abstract${modelName}State state){
        currentState = state;
    }

    ${tc.include("StatechartTransitionMethod.ftl",transitions)}

}