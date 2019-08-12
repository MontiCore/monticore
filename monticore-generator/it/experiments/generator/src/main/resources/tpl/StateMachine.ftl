<#-- (c) https://github.com/MontiCore/monticore -->
<#--
   Template, belongs to StateMachine @ grammar HierAutomaton
-->
// Hierarchical automaton: Describing a Statemachine
automaton  ${ast.name} 
  ${tc.include("tpl.StateBody", ast.stateBody)}
