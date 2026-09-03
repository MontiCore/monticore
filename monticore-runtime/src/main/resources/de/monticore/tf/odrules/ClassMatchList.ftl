<#-- (c) https://github.com/MontiCore/monticore -->
<#list ast.getPattern().getLHSObjectsList() as list>
  <#if list.isListObject()>
  <#assign mandatoryObjects = hierarchyHelper.getListChilds(ast.getPattern().getLHSObjectsList(), list)>
  <#assign matchingObjects = hierarchyHelper.getListChilds(ast.getPattern().getMatchingObjectsList(), list)>
public static class Match${list.getObjectName()}{
  protected static class ListMatch {
    <#list matchingObjects as object>
      <#assign isWithinOpt = hierarchyHelper.isWithinOptionalStructure(object.getObjectName())>
      protected <#if isWithinOpt>Optional<</#if>
      <#if !object.isListObject()> ${object.getType()}
      <#else>${object.getListtype()}>
      </#if>
      <#if isWithinOpt>></#if> ${object.getObjectName()};
      protected List<ASTNode> ${object.getObjectName()}_temp_candidates;
    </#list>
    protected Stack<String> backtracking;

    protected ListMatch (
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
      </#list>
    }
  }

  protected LinkedList<ListMatch> items;
  protected ListMatchReferenceCounter refs;

  public Match${list.getObjectName()}() {
    this.items = new LinkedList<>();
    this.refs = new ListMatchReferenceCounter();
  }

  public Match${list.getObjectName()}(Match${list.getObjectName()} match) {
    this.items = new LinkedList<>(match.items);
    this.refs = new ListMatchReferenceCounter(match.refs);
  }

  protected void add(ListMatch listMatch) {
    this.items.add(listMatch);
    <#list mandatoryObjects as object>
      <#if hierarchyHelper.isWithinOptionalStructure(object.getObjectName())>
    listMatch.${object.getObjectName()}.ifPresent(x -> this.refs.inc(x));
      <#else>
    this.refs.inc(listMatch.${object.getObjectName()});
      </#if>
    </#list>
  }

  protected ListMatch popLast() {
    ListMatch last = this.items.pollLast();
    if(last != null) {
    <#list mandatoryObjects as object>
        <#if hierarchyHelper.isWithinOptionalStructure(object.getObjectName())>
          last.${object.getObjectName()}.ifPresent(x -> this.refs.dec(x));
        <#else>
          this.refs.dec(last.${object.getObjectName()});
        </#if>
    </#list>
    }
    return last;
  }
}

  //Method for checking if the given object is already matched by the list
  protected boolean isMatchedBy${list.getObjectName()} (ASTNode cand) {
    return ${list.getObjectName()}_candidates.refs.isMatchedBy(cand);
  }
</#if>
</#list>
