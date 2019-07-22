<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("ruleName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>
<#assign astNode = names.getQualifiedName(genHelper.getAstPackage(), "AST" + ruleName)>
  public Optional<${astNode}> getAstNode() {
    return Optional.ofNullable(node);
  }

  public void setAstNode(${astNode} node) {
    this.node = node;
  }