<#-- (c) https://github.com/MontiCore/monticore -->
<#--
   Template, belongs to StateMachine @ grammar HierAutomata
-->
// Hierarchical automaton: Describing a Statemachine
automaton  ${ast.name} 
  ${include2("tpl3.StateBody", ast.stateBody)}
