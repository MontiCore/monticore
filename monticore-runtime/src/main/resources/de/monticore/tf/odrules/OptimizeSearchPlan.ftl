<#-- (c) https://github.com/MontiCore/monticore -->

  private void optimizeSearchplan() {

    // Optimization 1: With parent->child compositions: Ensure the parent is checked first
    Map<String, List<String>> compositionDependencies = new HashMap<>();
<#list ast.getPattern().getLHSObjectsList() as object>
    compositionDependencies.put(${"\"" + object.getObjectName() + "\""}, List.of(${ast.getCompositionDependencyNames(object)?map(name -> "\""+name+"\"")?join(", ")}));
</#list>

    boolean changed = true;
    while(changed) {
      changed = false;
      for(Map.Entry<String, List<String>> lhsObject : compositionDependencies.entrySet()) {
        String lhsObjectName = lhsObject.getKey();
        List<String> compositionSources = lhsObject.getValue();
        if(!compositionSources.isEmpty()) {
          int index = searchPlan.indexOf(lhsObjectName);
          for(String compSrcOb : compositionSources) {
            int indexSrc = searchPlan.indexOf(compSrcOb);
            if(indexSrc > index) {
              searchPlan.remove(compSrcOb);
              searchPlan.add(index, compSrcOb);
              changed = true;
            }
          }
        }
      }
    }

    // TODO: Optimization 2: In the case of parent (large count) -> child (few occurences)
    // In this case, the child should be searched for first, followed by a getParent(call)

    // We have to ensure that objects affected by a replacement are
    // - LHS [more to the bottom] RHS or
    // - ModifiedObject [more to the bottom] LHS <-- we try this approach here
    Map<String, String> replacementChangeMapping = new  HashMap<>();
<#list ast.getReplacementChangeMapping() as key, value>
    replacementChangeMapping.put(${"\"" + key + "\""}, ${"\"" + value + "\""});
</#list>

    for(Map.Entry<String, String> replacementChange : replacementChangeMapping.entrySet()) {
      String objectToMove = replacementChange.getKey();
      String lhsModifier = replacementChange.getValue();
      int index = searchPlan.indexOf(objectToMove);
      int indexSrc = searchPlan.indexOf(lhsModifier);
      if (index > indexSrc) {
        searchPlan.remove(objectToMove);
        searchPlan.add(indexSrc, objectToMove);
      }
    }

    // TODO: Improve this shuffling
    /*
    * state [[ $A :- $_ ]] {
    *    state $I;
    * }
    */

    // TODO: Do we have to consider creations too?
  }
