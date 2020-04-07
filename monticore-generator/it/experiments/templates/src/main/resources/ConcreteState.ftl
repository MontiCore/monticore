<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("existingTransitions","nonExistingTransitionNames","className")}
public <#if className?ends_with("TOP")>abstract </#if>class ${className?cap_first} extends AbstractState{

    ${tc.include("HandleTransition.ftl",existingTransitions)}
    ${tc.include("HandleNotExistingTransition.ftl",nonExistingTransitionNames)}

}