<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("existingTransitions","nonExistingTransitionNames","className")}
public <#if className?ends_with("TOP")>abstract </#if>class ${className} extends Abstract${modelName}State{

    ${tc.include("HandleTransition.ftl",existingTransitions)}
    ${tc.include("HandleNotExistingTransition.ftl",nonExistingTransitionNames)}

}