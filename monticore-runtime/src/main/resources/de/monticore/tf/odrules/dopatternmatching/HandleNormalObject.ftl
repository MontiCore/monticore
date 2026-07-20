<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("isOptional", "isList" "parentObject")}
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="isOptional" type="boolean" -->
<#-- @ftlvariable name="isList" type="boolean" -->
<#-- @ftlvariable name="parentObject" type="de.monticore.tf.odrulegeneration._ast.ASTMatchingObject" -->

<#assign normalObject = ast>
case "${normalObject.getObjectName()}" -> {
  if(isBacktrackingNegative) {
    isBacktracking = true;
    isBacktrackingNegative = false;
    <#if isList && parentObject?has_content>
      clear${parentObject.getObjectName()}NegativeObjects();
    <#else>
      clearNegativeObjects();
    </#if>
  }
  if (!isBacktracking) {
    ((FastLookupList<?>)${normalObject.getObjectName()}_candidates_temp).reset();
  }
  <#if isOptional>
  // exit condition for optional structures
  else if(${normalObject.getObjectName()}_candidates_temp.isEmpty()  && isParentBacktrackingNegative) {
    return false;
  }
  </#if>
  // try to find a match
  ${normalObject.getObjectName()}_cand = match_${normalObject.getObjectName()}();
  // test if match was found
  if(${normalObject.getObjectName()}_cand == null) {
    // if no object ist found, test if backtracking stack is empty
    if(backtracking.isEmpty()) {
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
      // reset candidates list
      ((FastLookupList<?>)${normalObject.getObjectName()}_candidates_temp).reset();
    }
  } else {
    // stop backtracking
    isBacktracking = false;
    // put object on backtracking stack
    backtracking.push(nextNode);
    // update candidates for next object to match
    if(!searchPlan.isEmpty()){
      findActualCandidates(searchPlan.peek());
    }
  }
}
