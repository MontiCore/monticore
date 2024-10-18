<#-- (c) https://github.com/MontiCore/monticore -->
<#list ast.getPattern().getLHSObjectsList() as list>
  <#if list.isListObject()>
  <#assign mandatoryObjects = hierarchyHelper.getListChilds(ast.getPattern().getLHSObjectsList(), list)>
  <#assign matchingObjects = hierarchyHelper.getListChilds(ast.getPattern().getMatchingObjectsList(), list)>
public class Match${list.getObjectName()}{
  private Match${list.getObjectName()}(
  <#list mandatoryObjects as object>
    <#if !object.isListObject()> ${object.getType()}
    <#else>${object.getListtype()}
    </#if> ${object.getObjectName()}
    <#if object_has_next>,</#if>
  </#list>){
  <#list mandatoryObjects as object>
    <#if hierarchyHelper.isWithinOptionalStructure(object.getObjectName())>
      this.${object.getObjectName()} = Optional.ofNullable(${object.getObjectName()});
    <#else>
      this.${object.getObjectName()} = ${object.getObjectName()};
    </#if>
  </#list>}
  <#list matchingObjects as object>
    <#assign isWithinOpt = hierarchyHelper.isWithinOptionalStructure(object.getObjectName())>
      private <#if isWithinOpt>Optional<</#if>
    <#if !object.isListObject()> ${object.getType()}
    <#else>${object.getListtype()}>
    </#if>
    <#if isWithinOpt>></#if> ${object.getObjectName()};
      private List<ASTNode> ${object.getObjectName()}_temp_candidates;
  </#list>
    private Stack<String> backtracking;
}

  <#list mandatoryObjects as listchild>
  // Method for checkConditions to get the Elements of the List to compare while Matching
  private List<${listchild.getType()}> get_${listchild.getObjectName()}_temp_cands() {
    List<${listchild.getType()}> ${listchild.getObjectName()} = new ArrayList<${listchild.getType()}>();
    ListIterator ${list.getObjectName()}It = ${list.getObjectName()}_candidates.listIterator();
    while(${list.getObjectName()}It.hasNext()) {
      Match${list.getObjectName()} ${list.getObjectName()} = (Match${list.getObjectName()})${list.getObjectName()}It.next();
    <#assign isInOpt = hierarchyHelper.isWithinOptionalStructure(listchild.getObjectName())>
    <#if isInOpt>if(${list.getObjectName()}.${listchild.getObjectName()}.isPresent()) {</#if>
    ${listchild.getObjectName()}.add(${list.getObjectName()}.${listchild.getObjectName()}<#if isInOpt>.get()</#if>);
    <#if isInOpt>}</#if>
    }
    return ${listchild.getObjectName()};
  }
  </#list>

  //Method for checking if the given object is already matched by the list
  private boolean isMatchedBy${list.getObjectName()} (ASTNode cand) {
    return
  <#list mandatoryObjects as listchild>get_${listchild.getObjectName()}_temp_cands().contains(cand)
    <#if listchild_has_next> || </#if>
  </#list>;
  }
</#if>
</#list>
