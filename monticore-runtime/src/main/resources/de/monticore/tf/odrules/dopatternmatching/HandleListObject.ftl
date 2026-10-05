<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("isOptional", "isList" "parentObject")}
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="isOptional" type="boolean" -->
<#-- @ftlvariable name="isList" type="boolean" -->
<#-- @ftlvariable name="parentObject" type="de.monticore.tf.odrulegeneration._ast.ASTMatchingObject" -->

<#assign listObject = ast>
case "${listObject.getObjectName()}_$List" -> {
  // this is a list object
  if (isBacktrackingNegative) {
    isBacktracking = true;
    isBacktrackingNegative = false;
    <#if isList && parentObject?has_content>
      clear${parentObject.getObjectName()}NegativeObjects();
    <#else>
      clearNegativeObjects();
    </#if>
  }

  // Start ListMatching and test if match was found
  if (!doPatternMatching_${listObject.getObjectName()}(isBacktracking)) {
    // if no object is found, test if backtracking stack is empty
    if (backtracking.isEmpty()) {
      // no match of the pattern can be found
      <#if isOptional>
        <#if parentObject?has_content>
        reset_${parentObject.getObjectName()}();
        </#if>
        if (isParentBacktrackingNegative) {
          //Can not find a new Match, signal the parent to backtrack
          return false;
        }
      </#if>
      foundMatch = false;
      break mainLoop;
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
