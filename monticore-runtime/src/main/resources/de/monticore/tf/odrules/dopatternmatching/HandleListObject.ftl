<#-- (c) https://github.com/MontiCore/monticore -->
${signature("isOptional")}

<#assign listObject = ast>
if (nextNode.equals("${listObject.getObjectName()}_$List")) {
  // this is a list object
  if (isBacktrackingNegative) {
    isBacktracking = true;
    isBacktrackingNegative = false;
    clearNegativeObjects();
  }

  // Start ListMatching and test if match was found
  if (!doPatternMatching_${listObject.getObjectName()}(isBacktracking)) {
    // if no object is found, test if backtracking stack is empty
    if (backtracking.isEmpty()) {
      // no match of the pattern can be found
      <#if isOptional>
        if (isParentBacktrackingNegative) {
          //Can not find a new Match, signal the parent to backtrack
          return false;
        }
      <#else>
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
  } else {
    // Else stop backtracking
    isBacktracking = false;
    // put object on backtracking stack
    backtracking.push(nextNode);
    // update candidates for next object to match
    if (!searchPlan.isEmpty()) {
      findActualCandidates(searchPlan.peek());
    }
  }
}
