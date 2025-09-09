<#-- (c) https://github.com/MontiCore/monticore -->
${signature("isOptional")}

<#assign notObject = ast>
if (nextNode.equals("${notObject.getObjectName()}")) {
  // this is a negative object, reset candidates list
  if (!isBacktracking) {
    if (!isBacktrackingNegative) {
      ${notObject.getObjectName()}_candidates_temp = new ArrayList<>(${notObject.getObjectName()}_candidates);
    }
    // try to find a match
    ${notObject.getObjectName()}_cand = match_${notObject.getObjectName()}();
    // test if match does not exist
    if (${notObject.getObjectName()}_cand == null) {
      // if no object ist found, test if backtracking stack is empty
      if (backtrackingNegative.isEmpty()) {
        // no match of negative elements can be found go on with lists
        <#if !isOptional>
        foundMatch = true;
        </#if>
        isBacktrackingNegative = false;
        backtracking.push(nextNode);
        while (!searchPlan.isEmpty() && !searchPlan.peek().endsWith("_$List")) {
          backtracking.push(searchPlan.pop());
        }
      } else {
        // start backtracking
        isBacktrackingNegative = true;
        // put object back on stack
        searchPlan.push(nextNode);
        // put the first object of the backtracking stack
        searchPlan.push(backtrackingNegative.pop());
        // reset candidates list
        ${notObject.getObjectName()}_candidates_temp = new ArrayList<>(${notObject.getObjectName()}_candidates);
      }
    } else {
      // update candidates for next object to match
      if(!searchPlan.isEmpty()) {
        // put object on backtracking stack
        backtrackingNegative.push(nextNode);
        // set backtracking back to false
        isBacktrackingNegative = false;
        findActualCandidates(searchPlan.peek());
      } else {
        // start backtracking
        isBacktrackingNegative = true;
        // put object back on stack
        searchPlan.push(nextNode);
        while(!backtrackingNegative.empty()) {
          searchPlan.push(backtrackingNegative.pop());
        }
        if(!backtracking.isEmpty()) {
          searchPlan.push(backtracking.pop());
        }
        // reset candidates list
        ${notObject.getObjectName()}_candidates_temp = new ArrayList<>(${notObject.getObjectName()}_candidates);
      }
    }
  } else {
    searchPlan.push(nextNode);
    searchPlan.push(backtracking.pop());
  }
}
