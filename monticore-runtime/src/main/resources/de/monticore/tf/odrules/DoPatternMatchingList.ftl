<#-- (c) https://github.com/MontiCore/monticore -->

<#list ast.getPattern().getLHSObjectsList() as structure>
<#-- We are only interested in List structures here -->
    <#if structure.isListObject()>
     <#assign mandatoryObjects = hierarchyHelper.getListChilds(ast.getPattern().getLHSObjectsList(), structure)>
      <#assign allObjects = hierarchyHelper.getListChildsWithOptionals(ast.getPattern().getLHSObjectsList(), structure)>
      <#-- <#assign allObjects = hierarchyHelper.getListChildsWithOptionals(ast.getPattern().getLHSObjectsList(), structure)> -->
    <#-- This call omits optionals! <#assign mandatoryObjects = hierarchyHelper.getListChilds(ast.getPattern().getLHSObjectsList(), structure)> -->

    <#-- <<#assign mandatoryObjects = hierarchyHelper.getListChildsWithOptionals(ast.getPattern().getLHSObjectsList(), structure)> -->

    <#macro commaSeperatedNames>
    <#list mandatoryObjects as object>${object.getObjectName()}_cand<#if object_has_next>,</#if></#list></#macro>

public boolean doPatternMatching_${structure.getObjectName()}(boolean isParentBacktracking) {
    // indicates whether this rule is currently backtracking
    // (this will skip all attempts to match negative nodes)
    boolean isBacktracking = isParentBacktracking;
    boolean isBacktrackingNegative = false;

    Stack<String> backtracking = new Stack<String>();
    Stack<String> backtrackingNegative = new Stack<String>();
    Stack<String> searchPlan = new Stack<String>();
    boolean foundMatch = true;
    String nextNode = null;
    if(is_${structure.getObjectName()}_fix) {
      // The List is given, just write it in the cand
      foundMatch = false;
    } else if (!isParentBacktracking) {
      // if the Parent is not Backtracking find a complete new List
      ${structure.getObjectName()}_candidates = new ArrayList<Match${structure.getObjectName()}>();
    }

    // SetUp Last Matching Process if ParentIsBacktracking
    if(isParentBacktracking) {
      if (${structure.getObjectName()}_candidates == null) {
         // the candidates were reset previously (list in opt?) -> we can't backtrace
         return false;
      }
      // Get Last List Object
      Match${structure.getObjectName()} match = ${structure.getObjectName()}_candidates.get(${structure.getObjectName()}_candidates.size()-1);
      ${structure.getObjectName()}_candidates.remove(${structure.getObjectName()}_candidates.size()-1);
      // Load the Objects and Their temp_candidates
      <#list mandatoryObjects as object>
        ${object.getObjectName()}_cand = match.${object.getObjectName()}<#if hierarchyHelper.isWithinOptionalStructure(object.getObjectName())>.orElse(null)</#if>;
        ${object.getObjectName()}_candidates_temp = match.${object.getObjectName()}_temp_candidates;
      </#list>
      // Get the BacktrackingStack
      backtracking = match.backtracking;
      // Clear the Last Object and put it on the searchPlan
      if (!backtracking.isEmpty()) {
      <#list mandatoryObjects as object>
        if (backtracking.peek().equals("${object.getObjectName()}")) {
          ${object.getObjectName()}_cand = null;
        }
      </#list>
        searchPlan.push(backtracking.pop());
      }
    }

    boolean hasFoundAtLeastOneMatch = false;
    while(foundMatch) {
      // If the parent was Backtracking don't load a new searchPlan
      if (!isBacktracking) {
        searchPlan = (Stack<String>) searchPlan_${structure.getObjectName()}.clone();
        // also reset all optional "counter" of opts within this list
				<#list allObjects as object>
					<#if object.isOptObject()>
					opt_found_${object.getObjectName()} = false;
					</#if>
				</#list>

      }
      mainLoop: while(!searchPlan.isEmpty()){
        nextNode = searchPlan.pop();
        switch(nextNode) {
    <#--creates an switch case for each object for matching the object-->
        <#list allObjects as object>
          <#if object.isListObject()>
              ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleListObject", object, [false, true, structure])}
          <#elseif object.isOptObject()>
              ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleOptObject", object, [false, true, structure])}
          <#elseif object.isNotObject()>
              ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleNotObject", object, [false, true, structure])}
          <#else><#-- normal object -->
              ${tc.includeArgs("de.monticore.tf.odrules.dopatternmatching.HandleNormalObject", object, [false, true, structure])}
          </#if>
        </#list>
        }

        if(!isBacktrackingNegative){
          if(searchPlan.isEmpty()){
            if(!checkConstraints()){
              if(backtracking.isEmpty()){
                //no match of the pattern can be found
                foundMatch = false;
                break;
              }else{
                // start backtracking
                isBacktrackingNegative = true;
                //put all negative elements on the searchPlan
               <#list allObjects as object>
                <#if object.isNotObject()>
                    searchPlan.push(backtracking.pop());
                </#if>
              </#list>
                //also put the last not-negative element on the searchPlan
                searchPlan.push(backtracking.pop());
              }
            }
          }
        }
      }
      //create a replacement candidate if a match was found
      if(foundMatch) {
        Match${structure.getObjectName()} match = new Match${structure.getObjectName()}(<@commaSeperatedNames/>);
        match.backtracking = (Stack<String>) backtracking.clone();
        <#list mandatoryObjects as o>// save context of every object and then clear it
        match.${o.getObjectName()}_temp_candidates = ((FastLookupList<ASTNode>)${o.getObjectName()}_candidates_temp).matchCopy();
        ${o.getObjectName()}_cand = null;
        </#list>
        ${structure.getObjectName()}_candidates.add(match);
        backtracking.clear();
        hasFoundAtLeastOneMatch = true;
      }
    }

    // Reset list candidates are match
    <#list structure.getInnerLinkObjectNamesList() as innerLinkObjectName>
    <#if hierarchyHelper.isNoOptionalName(ast.getPattern().getLHSObjectsList(), innerLinkObjectName)>
    ${innerLinkObjectName}_cand = null;
    </#if>
    </#list>

    // TODO: Do something similar for optionals (but somehow do not loose them?)

    if (!hasFoundAtLeastOneMatch) {
      // TODO: Does this reset create any sideeffects?
      ${structure.getObjectName()}_candidates = null;
      return false;
    }
    ${structure.getObjectName()}_cand = ${structure.getObjectName()}_candidates;
    return true;
}

protected void clear${structure.getObjectName()}NegativeObjects(){
  <#list mandatoryObjects as object>
    <#if object.isNotObject() && hierarchyHelper.isWithinListStructure(object.getObjectName())>
      ${object.getObjectName()}_cand = null;
    </#if>
  </#list>
}
</#if>
</#list>

