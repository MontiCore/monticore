<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("stateName","modelName","existingTransitions","nonExistingTransitionNames")}
public class ${stateName?cap_first}State extends AbstractState{

    <#list existingTransitions as transition>
        ${tc.includeArgs("HandleTransition.ftl",modelName,transition.getInput(),transition.getTo())}
    </#list>

    <#list nonExistingTransitionNames as transition>
        ${tc.includeArgs("HandleNotExistingTransition.ftl",modelName,transition)}
    </#list>
}