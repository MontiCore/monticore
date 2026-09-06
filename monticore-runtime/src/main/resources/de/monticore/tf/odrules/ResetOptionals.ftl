<#-- (c) https://github.com/MontiCore/monticore -->
<#-- We are only interested in optional structures here -->
<#list hierarchyHelper.getOptionalMatchObjects(ast.getPattern().getLHSObjectsList()) as structure>

// To allow optionals to always match at least once
protected boolean opt_found_${structure.getObjectName()} = false;

protected void reset_${structure.getObjectName()}() {
  // TODO: correct? -> not for lists?
<#list ast.getAllInnerNonOptionalNames(ast.getPattern().getLHSObjectsList(), structure) as elem>
  ${elem}_cand = null;
</#list>
}
</#list>
