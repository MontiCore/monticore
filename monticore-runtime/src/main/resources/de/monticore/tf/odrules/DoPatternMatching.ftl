<#-- (c) https://github.com/MontiCore/monticore -->
<#assign mandatoryObjects = hierarchyHelper.getMandatoryObjectsWithoutOptAndListChilds(ast.getPattern().getLHSObjectsList())>
<#macro commaSeperatedNames>
  <#list mandatoryObjects as object>${object.getObjectName()}_cand
    <#if object_has_next>,</#if>
  </#list>
</#macro>

public boolean doPatternMatching() {
  Reporting.reportTransformationStart("${ast.getClassname()}");
  boolean foundMatch = true;
  // indicates whether this rule is currently backtracking
  // (this will skip all attempts to match negative nodes)
  boolean isBacktracking = true;
  boolean isBacktrackingNegative = false;
  for(ASTNode a: hostGraph){
    a.accept(t.getTraverser());
  }
  if (searchPlan == null) {
    searchPlan = findSearchPlan();
    splitSearchplan(); // for OptList structures
    isBacktracking = false;
  }
  Stack<String> backtracking = new Stack<String>();
  Stack<String> backtrackingNegative = new Stack<String>();
  String nextNode = null;
  while(!searchPlan.isEmpty()) {
    nextNode = searchPlan.pop();
    <#--creates an if statement for each object for matching the object-->
<#list hierarchyHelper.getMandatoryObjectsWithoutListChilds(ast.getPattern().getLHSObjectsList()) as object>
  <#if object.isListObject()>
    ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleListObject", object, [false])}
  <#elseif object.isOptObject()>
    ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleOptObject", object, [false])}
  <#elseif object.isNotObject()>
    ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleNotObject", object, [false])}
  <#else>
    ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleNormalObject", object, [false])}
  </#if>
  <#if object_has_next>else</#if>
</#list>
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
    <#list ast.getPattern().getLHSObjectsList() as object>
      <#if !object.isNotObject() && !object.isListObject() && !object.isOptObject() && !hierarchyHelper.isWithinOptionalStructure(object.getObjectName()) && !hierarchyHelper.isWithinListStructure(object.getObjectName())>
    if (${object.getObjectName()}_cand != null) {
      Reporting.reportTransformationObjectMatch("${ast.getClassname()}",${object.getObjectName()}_cand);
    }
    if (${object.getObjectName()}_cand != null) {
      Reporting.reportTransformationObjectMatch("${ast.getClassname()}",${object.getObjectName()}_cand);
    }
      </#if>
    </#list>
    if (nextNode != null) {
      searchPlan.push(nextNode);
    }
    allMatches.add(match);
  }
  return foundMatch;
}

private void clearNegativeObjects() {
  <#list ast.getPattern().getLHSObjectsList() as object>
    <#if object.isNotObject() && !hierarchyHelper.isWithinListStructure(object.getObjectName())>
      ${object.getObjectName()}_cand = null;
    </#if>
  </#list>
}
