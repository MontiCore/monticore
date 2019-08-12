<#-- (c) https://github.com/MontiCore/monticore -->
<#--
   Template, belongs to StateMachine @ grammar HierAutomaton
-->
// Hierarchical automaton: Describing a Statemachine
${tc.signature("dp")}
${glex.defineGlobalVar("statedepth", dp)}
automaton  ${ast.name} 
  ${tc.include("tpl2.StateBody", ast.stateBody)}
// arguments are:  ${dp};
