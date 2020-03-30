<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("stateName","existingTransitions","nonExistingTransitionNames")}
public class ${stateName?cap_first}State extends AbstractState{

    ${tc.include("HandleTransition.ftl",existingTransitions)}
    ${tc.include("HandleNotExistingTransition.ftl",nonExistingTransitionNames)}

}