<#-- (c) https://github.com/MontiCore/monticore -->
<#-- We are only interested in optional structures here -->
<#list hierarchyHelper.getOptionalMatchObjects(ast.getPattern().getLHSObjectsList()) as structure>

protected boolean doPatternMatching_${structure.getObjectName()}(boolean isParentBacktracking, boolean isParentBacktrackingNegative) {
  // indicates whether this rule is currently backtracking
  // (this will skip all attempts to match lists or negative nodes)
  boolean isBacktracking = isParentBacktracking;
  boolean isBacktrackingNegative = isParentBacktrackingNegative;

  Stack<String> backtracking = new Stack<String>();
  Stack<String> backtrackingNegative = new Stack<String>();
  Stack<String> searchPlan = (Stack<String>) searchPlan_${structure.getObjectName()}.clone();

  String nextNode = null;
	boolean foundMatch = true;
  while(!searchPlan.isEmpty()){
    nextNode = searchPlan.pop();
    <#--creates an if statement for each object for matching the object-->

    <#-- <#list ast.getPattern().getLHSObjectsList() as object> -->
    <#list hierarchyHelper.getInnerLinkObjectsLHS(ast.getPattern().getLHSObjectsList(), structure) as object>
      <#if object.isListObject()>
        ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleListObject", object, [true, structure])}
      <#elseif object.isOptObject()>
        ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleOptObject", object, [true, structure])}
      <#elseif object.isNotObject()>
        ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleNotObject", object, [true, structure])}
      <#else>
        ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleNormalObject", object, [true, structure])}
      </#if>
      <#if object_has_next>else</#if>
    </#list>
  }
  // Now we wish to ensure, that we always find something with an optional at least once
  if (!foundMatch) {
    // no match for the optional found
    if (this.opt_found_${structure.getObjectName()}) {
      // and this appears to be the 2nd time we try to match this opt (and have failed)
      // -> do not match nothing a second time
      return false;
    }
		// return true, as the optional is... optional
  }
  opt_found_${structure.getObjectName()} = true; // do not match this empty optional again
  return true;
}
</#list>
