<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("modelName","initialStateName","transitions")}
public class ${modelName?cap_first}{

    protected AbstractState currentState = new ${initialStateName?cap_first}State();

    public void setState(AbstractState state){
        currentState = state;
    }

    <#list transitions as transition>
        ${tc.includeArgs("StatechartTransitionMethod.ftl",transition)}
    </#list>

}