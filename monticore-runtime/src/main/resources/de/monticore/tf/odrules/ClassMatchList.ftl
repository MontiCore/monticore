<#-- (c) https://github.com/MontiCore/monticore -->
<#list ast.getPattern().getLHSObjectsList() as list>
  <#if list.isListObject()>
  <#assign mandatoryObjects = hierarchyHelper.getListChilds(ast.getPattern().getLHSObjectsList(), list)>
  <#assign matchingObjects = hierarchyHelper.getListChilds(ast.getPattern().getMatchingObjectsList(), list)>
public static class Match${list.getObjectName()}{
  protected Match${list.getObjectName()}(
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
      protected <#if isWithinOpt>Optional<</#if>
    <#if !object.isListObject()> ${object.getType()}
    <#else>${object.getListtype()}>
    </#if>
    <#if isWithinOpt>></#if> ${object.getObjectName()};
      protected List<ASTNode> ${object.getObjectName()}_temp_candidates;
  </#list>
    protected Stack<String> backtracking;
}

  <#list mandatoryObjects as listchild>
	<#assign isInOpt = hierarchyHelper.isWithinOptionalStructure(listchild.getObjectName())>
  // Method for checkConditions to get the Elements of the List to compare while Matching
  protected List<${listchild.getType()}> get_${listchild.getObjectName()}_temp_cands() {
		<#if isInOpt>
		// due to optionals, we have to filter absent elements
    List<${listchild.getType()}> ${listchild.getObjectName()} = new ArrayList<${listchild.getType()}>();
    ListIterator ${list.getObjectName()}It = ${list.getObjectName()}_candidates.listIterator();
    while(${list.getObjectName()}It.hasNext()) {
      Match${list.getObjectName()} ${list.getObjectName()} = (Match${list.getObjectName()})${list.getObjectName()}It.next();
    	if(${list.getObjectName()}.${listchild.getObjectName()}.isPresent()) {
    ${listchild.getObjectName()}.add(${list.getObjectName()}.${listchild.getObjectName()}.get());
			}
    }
    return ${listchild.getObjectName()};
	}
		<#else>
		// optimized case: Return a View to the individual candidates
		return new MatchCandList${listchild.getObjectName()}(${list.getObjectName()}_candidates);
	}

	static class MatchCandList${listchild.getObjectName()} extends java.util.AbstractList<${listchild.getType()}> {

			protected final List<Match${list.getObjectName()}> matches;

			public MatchCandList${listchild.getObjectName()}(List<Match${list.getObjectName()}> matches) {
				this.matches = matches;
			}

			@Override
			public ${listchild.getType()} get(int index) {
				return matches.get(index).${listchild.getObjectName()};
			}

			@Override
			public int size() {
				return matches.size();
			}
	}
		</#if>
  </#list>

  //Method for checking if the given object is already matched by the list
  protected boolean isMatchedBy${list.getObjectName()} (ASTNode cand) {
    return
  <#list mandatoryObjects as listchild>get_${listchild.getObjectName()}_temp_cands().contains(cand)
    <#if listchild_has_next> || </#if>
  </#list>;
  }
</#if>
</#list>
