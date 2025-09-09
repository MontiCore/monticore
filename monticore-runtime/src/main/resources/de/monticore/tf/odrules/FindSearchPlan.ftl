<#-- (c) https://github.com/MontiCore/monticore -->

  private Stack<String> findSearchPlan() {
    Stack<String> searchPlan = new Stack<>();
    <#--creates a counter for each object-->
    <#list ast.getPattern().getLHSObjectsList() as object>
      <#if object.isNotObject() >
    int count_${object.getObjectName()} = (int) (0.9 * Integer.MAX_VALUE);
      <#elseif object.isOptObject() >
    int count_${object.getObjectName()} = (int) (0.7 * Integer.MAX_VALUE);
      <#elseif object.isListObject() >
    int count_${object.getObjectName()} = (int) (0.8 * Integer.MAX_VALUE);
      <#elseif dependVars?keys?seq_contains(object.getObjectName())>
    int count_${object.getObjectName()} = 0;
    count_${object.getObjectName()} = count_${object.getObjectName()} - ${dependVars[object.getObjectName()]};
      <#else>
    int count_${object.getObjectName()} = (int) (0.1 * Integer.MAX_VALUE);
      </#if>
    </#list>

    <#--set candidate lists for predefined parameter values-->
    <#list ast.getPattern().getLHSObjectsList() as object>
      <#if !object.isOptObject() && !object.isListObject()>
        <#if object.isNotObject() || object.isOptObject() || object.isListObject()>
    ${object.getObjectName()}_candidates = new ArrayList<>();
        <#else>
    if (!is_${object.getObjectName()}_fix) {
      ${object.getObjectName()}_candidates = new ArrayList<>();
    } else {
      count_${object.getObjectName()} = 1;
    }
        </#if>
      </#if>
    </#list>

    <#--creates a candidates list for each object-->
    <#list ast.getPattern().getTypesList() as type>
      <#if type != "de.monticore.tf.ast.IOptional" && type != "de.monticore.tf.ast.IList">
    // count occurrences of object types for the costs
    if (t.containsKey("${type}")){
      for (ASTNode cand : t.getInstances("${type}")){
        <#list ast.getPattern().getLHSObjectsList() as object>
          <#if object.type = type>
            <#if object.isNotObject()>
        if (checkConditions_${object.getObjectName()}((${type})cand)){
          ${object.getObjectName()}_candidates.add(cand);
        }
            <#else>
        if (!is_${object.getObjectName()}_fix && checkConditions_${object.getObjectName()}((${type})cand)) {
          count_${object.getObjectName()}++;
          ${object.getObjectName()}_candidates.add(cand);
        }
            </#if>
          </#if>
        </#list>
      }
    } else {
      for (ASTNode cand : t.getAll()) {
        if (cand instanceof ${type}) {
        <#list ast.getPattern().getLHSObjectsList() as object>
          <#if object.type = type>
            <#if object.isNotObject()>
          if (checkConditions_${object.getObjectName()}((${type})cand)) {
            ${object.getObjectName()}_candidates.add(cand);
          }
            <#else>
          if (!is_${object.getObjectName()}_fix && checkConditions_${object.getObjectName()}((${type})cand)) {
            count_${object.getObjectName()}++;
            ${object.getObjectName()}_candidates.add(cand);
          }
            </#if>
          </#if>
        </#list>
        }
      }
    }
      </#if>
    </#list>

    Map<String, Integer> unsortedData = new HashMap<>();
    <#list ast.getPattern().getLHSObjectsList() as object>
    unsortedData.put("${object.getObjectName()}<#if object.isListObject()>_$List</#if>",
    count_${object.getObjectName()});
    </#list>

    TreeMap<String, Integer> sortedData = new TreeMap<>(new ValueComparator(unsortedData));
    sortedData.putAll(unsortedData);

    // create searchPlan stack
    for (String s : sortedData.keySet()) {
      searchPlan.add(s);
    }

    return searchPlan;
  }
