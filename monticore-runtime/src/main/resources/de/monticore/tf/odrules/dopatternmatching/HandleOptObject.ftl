<#-- (c) https://github.com/MontiCore/monticore -->
${signature("isOptional")}

<#assign optObject = ast>
if (nextNode.equals("${optObject.getObjectName()}")) {
  // this is an optional object
  if (doPatternMatching_${optObject.getObjectName()}(isBacktrackingNegative)) {
    // Experimental
    if (isBacktrackingNegative) {
      isBacktracking = true;
      isBacktrackingNegative = false;
      clearNegativeObjects();
      // put object back on stack
      searchPlan.push(nextNode);
      // put the first object of the backtracking stack
      searchPlan.push(backtracking.pop());
    } else {
      isBacktracking = false;
      backtracking.push(nextNode);
    }

    // update candidates for next object to match
    if (!searchPlan.isEmpty()) {
      findActualCandidates(searchPlan.peek());
    }
  } else {
    // the pattern matching of an optional structure will always return true
    // (even if no match was found), except in the case that we're
    // backtracking because of negative nodes and have no more candidates to match
    // if no object is found, test if backtracking stack is empty
    if (backtracking.isEmpty()) {
      // no match of the pattern can be found
      <#if !isOptional>
      foundMatch = false;
      </#if>
      break;
    } else {
      // start backtracking
      isBacktracking = true;
      // put object back on stack
      searchPlan.push(nextNode);
      // put the first object of the backtracking stack
      searchPlan.push(backtracking.pop());
    }
  }
}
