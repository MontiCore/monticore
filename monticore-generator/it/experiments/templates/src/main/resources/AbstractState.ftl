<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("transitions","modelName")}
public abstract class AbstractState {

    <#list transitions as transition>
        ${tc.includeArgs("HandleTransitionAbstract.ftl",transition,modelName)}
    </#list>
}