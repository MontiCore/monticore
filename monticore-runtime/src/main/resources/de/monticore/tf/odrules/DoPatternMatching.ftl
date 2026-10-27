<#-- (c) https://github.com/MontiCore/monticore -->
<#assign mandatoryObjects = hierarchyHelper.getMandatoryObjectsWithoutOptAndListChilds(ast.getPattern().getLHSObjectsList())>
<#macro commaSeperatedNames>
  <#list mandatoryObjects as object>${object.getObjectName()}_cand
    <#if object_has_next>,</#if>
  </#list>
</#macro>

public boolean doPatternMatching() {
  boolean foundMatch = true;
  // indicates whether this rule is currently backtracking
  // (this will skip all attempts to match negative nodes)
  boolean isBacktracking = true;
  boolean isBacktrackingNegative = false;

<#list hierarchyHelper.getOptionalMatchObjects(ast.getPattern().getLHSObjectsList()) as optional>
  reset_${optional.getObjectName()}();
</#list>

  if (searchPlan == null) {
    searchPlan = findSearchPlan();

    if(optimizeSP) {
      optimizeSearchplan();
    }
    initializeFastLookupList();
    splitSearchplan(); // for OptList structures
    isBacktracking = false;
  }
  String nextNode = null;
  mainLoop: while(!searchPlan.isEmpty()) {
    nextNode = searchPlan.pop();
    switch(nextNode) {
    <#--creates a switch case for each object for matching the object-->
<#list hierarchyHelper.getMandatoryObjectsWithoutListChilds(ast.getPattern().getLHSObjectsList()) as object>
  <#if object.isListObject()>
    ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleListObject", object, [false, false, ""])}
  <#elseif object.isOptObject()>
    ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleOptObject", object, [false, false, ""])}
  <#elseif object.isNotObject()>
    ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleNotObject", object, [false, false, ""])}
  <#else>
    ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleNormalObject", object, [false, false, ""])}
  </#if>
</#list>
    }
    if (!isBacktrackingNegative) {
      if (searchPlan.isEmpty()) {
        if (!checkConstraints()) {
          if (backtracking.isEmpty()) {
            // no match of the pattern can be found
            foundMatch = false;
            break;
          } else {
            // start backtracking
            isBacktrackingNegative = true;
            // put all negative elements on the searchPlan
            <#list hierarchyHelper.getMandatoryObjectsWithoutListChilds(ast.getPattern().getLHSObjectsList()) as object>
              <#if object.isNotObject()>
            searchPlan.push(backtracking.pop());
              </#if>
            </#list>
            // also put the last not-negative element on the searchPlan
            searchPlan.push(backtracking.pop());
          }
        }
      }
    }
  }
  allMatches = new ArrayList
  <Match>();
  // create a replacement candidate if a match was found
  if (foundMatch) {
    Match match = new Match(<@commaSeperatedNames/>);
    if (nextNode != null) {
      searchPlan.push(nextNode);
    }
    allMatches.add(match);
    // And remove last backtracking
    if (!backtracking.isEmpty())
      backtracking.pop();
  }
  return foundMatch;
}

protected void clearNegativeObjects() {
  <#list ast.getPattern().getLHSObjectsList() as object>
    <#if object.isNotObject() && !hierarchyHelper.isWithinListStructure(object.getObjectName())>
      ${object.getObjectName()}_cand = null;
    </#if>
  </#list>
}
