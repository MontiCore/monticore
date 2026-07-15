<#-- (c) https://github.com/MontiCore/monticore -->
${signature("isOptional", "parentObject")}

<#assign optObject = ast>
case "${optObject.getObjectName()}" -> {
  // this is an optional object
  if (doPatternMatching_${optObject.getObjectName()}(isBacktracking, isBacktrackingNegative)) {
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
      foundMatch = false;
      <#if isOptional && parentObject?has_content>
      reset_${parentObject.getObjectName()}();
      </#if>
      break mainLoop;
    } else {
      // start backtracking
      isBacktracking = true;
      // put object back on stack
      searchPlan.push(nextNode);
      // put the first object of the backtracking stack
      searchPlan.push(backtracking.pop());
      // reset the optional candidate
      reset_${optObject.getObjectName()}();
      this.opt_found_${optObject.getObjectName()} = false;
    }
  }
}
