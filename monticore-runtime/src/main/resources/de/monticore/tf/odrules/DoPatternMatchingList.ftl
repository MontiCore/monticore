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
    boolean foundmatch = true;
    String nextNode = null;
    if(is_${structure.getObjectName()}_fix) {
      // The List is given, just write it in the cand
      foundmatch = false;
    } else if (!isParentBacktracking) {
      // if the Parent is not Backtracking find a complete new List
      ${structure.getObjectName()}_candidates = new ArrayList<Match${structure.getObjectName()}>();
    }

    // SetUp Last Matching Process if ParentIsBacktracking
    if(isParentBacktracking) {
      // Get Last List Object
      Match${structure.getObjectName()} match = ${structure.getObjectName()}_candidates.get(${structure.getObjectName()}_candidates.size()-1);
      ${structure.getObjectName()}_candidates.remove(${structure.getObjectName()}_candidates.size()-1);
      // Load the Objects and Their temp_candidates
      <#list mandatoryObjects as object>
        ${object.getObjectName()}_cand = match.${object.getObjectName()}<#if hierarchyHelper.isWithinOptionalStructure(object.getObjectName())>.get()</#if>;
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

    while(foundmatch) {
      // If the parent was Backtracking don't load a new searchPlan
      if (!isBacktracking) {
        searchPlan = (Stack<String>) searchPlan_${structure.getObjectName()}.clone();
      }
      while(!searchPlan.isEmpty()){
        nextNode = searchPlan.pop();
    <#--creates an if statement for each object for matching the object-->
        <#list allObjects as object>
          <#if object.isListObject()>
            if(nextNode.equals("${object.getObjectName()}_$List")){
              // this is a list object
              if(isBacktrackingNegative){
                isBacktracking = true;
                isBacktrackingNegative = false;
                clear${structure.getObjectName()}NegativeObjects();
              }
              // Start ListMatching and test if match was found
              if(!doPatternMatching_${object.getObjectName()}(isBacktracking)){
                //if no object is found, test if backtracking stack is empty
                if(backtracking.isEmpty()){
                  //no match of the pattern can be found
                  foundmatch = false;
                  break;
                }else{
                  // start backtracking
                  isBacktracking = true;
                  //put object back on stack
                  searchPlan.push(nextNode);
                  //put the first object of the backtracking stack
                  searchPlan.push(backtracking.pop());
                }
              } else {
                // Else stop backtracking
                isBacktracking = false;
                //put object on backtracking stack
                backtracking.push(nextNode);
                // update candidates for next object to match
                if (!searchPlan.isEmpty()) {
                  findActualCandidates(searchPlan.peek());
                }
              }

          <#elseif object.isOptObject()>
            if(nextNode.equals("${object.getObjectName()}")) {
              // this is an optional object
              if(doPatternMatching_${object.getObjectName()}(isBacktrackingNegative)) {

              if(isBacktrackingNegative){
                isBacktracking = true;
                isBacktrackingNegative = false;
                clear${structure.getObjectName()}NegativeObjects();
                // put object back on stack
                searchPlan.push(nextNode);
                // put the first object of the backtracking stack
                searchPlan.push(backtracking.pop());
              }else{
                isBacktracking = false;
                backtracking.push(nextNode);
              }

                // update candidates for next object to match
                if (!searchPlan.isEmpty()) {
                  findActualCandidates(searchPlan.peek());
                }
              }
              else {
                // the pattern matching of an optional structure will always return true
                // (even if no match was found), except in the case that we're
                // backtracking because of negative nodes and have no more
                // candidates to match

                // if no object is found, test if backtracking stack is empty
                if (backtracking.isEmpty()) {
                  // no match of the pattern can be found
                  foundmatch = false;
                  break;
                }
                else {
                  // start backtracking
                  isBacktracking = true;
                  // put object back on stack
                  searchPlan.push(nextNode);
                  // put the first object of the backtracking stack
                  searchPlan.push(backtracking.pop());
                }
              }

          <#elseif object.isNotObject()>
            if(nextNode.equals("${object.getObjectName()}")){
              // this is a negative object
              // reset candidates list
              if(!isBacktracking){
                if (!isBacktrackingNegative) {
                  ${object.getObjectName()}_candidates_temp = new ArrayList<>(${object.getObjectName()}_candidates);
                }
                //try to find a match
                ${object.getObjectName()}_cand = match_${object.getObjectName()}();
                //test if match does not exist
                if(${object.getObjectName()}_cand == null){
                  //if no object ist found, test if backtracking stack is empty
                  if(backtrackingNegative.isEmpty()){
                    //no match of negative elements can be found go on with lists
                    foundmatch = true;
                    isBacktrackingNegative = false;
                    backtracking.push(nextNode);
                    while (!searchPlan.isEmpty() && !searchPlan.peek().endsWith("_$List")) {
                      backtracking.push(searchPlan.pop());
                    }
                  }else{
                    // start backtracking
                    isBacktrackingNegative = true;
                    //put object back on stack
                    searchPlan.push(nextNode);
                    //put the first object of the backtracking stack
                    searchPlan.push(backtrackingNegative.pop());
                    //reset candidates list
                    ${object.getObjectName()}_candidates_temp = new ArrayList<>(${object.getObjectName()}_candidates);
                  }
                }else{

                  //update candidates for next object to match
                  if(!searchPlan.isEmpty()){
                    //put object on backtracking stack
                    backtrackingNegative.push(nextNode);
                    //set backtracking back to false
                    isBacktrackingNegative = false;
                    findActualCandidates(searchPlan.peek());
                  } else {
                    // start backtracking
                    isBacktrackingNegative = true;
                    //put object back on stack
                    searchPlan.push(nextNode);
                    while(!backtrackingNegative.empty()){
                      searchPlan.push(backtrackingNegative.pop());
                    }
                    if(!backtracking.isEmpty()){
                      searchPlan.push(backtracking.pop());
                    }
                    //reset candidates list
                    ${object.getObjectName()}_candidates_temp = new ArrayList<>(${object.getObjectName()}_candidates);

                  }
                }
              }else{
                searchPlan.push(nextNode);
                searchPlan.push(backtracking.pop());
              }
          <#else><#-- normal object -->
            if(nextNode.equals("${object.getObjectName()}")){
              if(isBacktrackingNegative){
                isBacktracking = true;
                isBacktrackingNegative = false;
                clear${structure.getObjectName()}NegativeObjects();
              }
              if (!isBacktracking) {
                ${object.getObjectName()}_candidates_temp = new ArrayList<>(${object.getObjectName()}_candidates);
              }
              //try to find a match
              ${object.getObjectName()}_cand = match_${object.getObjectName()}();
              //test if match was found
              if(${object.getObjectName()}_cand == null){
                //if no object ist found, test if backtracking stack is empty
                if(backtracking.isEmpty()){
                  //no match of the pattern can be found
                  foundmatch = false;
                  break;
                }else{
                  // start backtracking
                  isBacktracking = true;
                  //put object back on stack
                  searchPlan.push(nextNode);
                  //put the first object of the backtracking stack
                  searchPlan.push(backtracking.pop());
                  //reset candidates list
                  ${object.getObjectName()}_candidates_temp = new ArrayList<>(${object.getObjectName()}_candidates);
                }
              }else{
                // stop backtracking
                isBacktracking = false;
                //put object on backtracking stack
                backtracking.push(nextNode);
                //update candidates for next object to match
                if(!searchPlan.isEmpty()){
                  findActualCandidates(searchPlan.peek());
                }
              }
          </#if>
            }<#if object_has_next>else</#if>
        </#list>

        if(!isBacktrackingNegative){
          if(searchPlan.isEmpty()){
            if(!checkConstraints()){
              if(backtracking.isEmpty()){
                //no match of the pattern can be found
                foundmatch = false;
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
      if(foundmatch) {
        Match${structure.getObjectName()} match = new Match${structure.getObjectName()}(<@commaSeperatedNames/>);
        match.backtracking = (Stack<String>) backtracking.clone();
        <#list mandatoryObjects as o>// save context of every object and then clear it
          match.${o.getObjectName()}_temp_candidates = ${o.getObjectName()}_candidates_temp;
          ${o.getObjectName()}_cand = null;
        </#list>
        ${structure.getObjectName()}_candidates.add(match);
        backtracking.clear();
      }
    }
    if(${structure.getObjectName()}_candidates.isEmpty()) {
      return false;
    }
    ${structure.getObjectName()}_cand = ${structure.getObjectName()}_candidates;
    return true;
}

private void clear${structure.getObjectName()}NegativeObjects(){
  <#list mandatoryObjects as object>
    <#if object.isNotObject() && hierarchyHelper.isWithinListStructure(object.getObjectName())>
      ${object.getObjectName()}_cand = null;
    </#if>
  </#list>
}
</#if>
</#list>

