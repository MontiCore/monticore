<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("ruleName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>
<#assign astNode = names.getQualifiedName(genHelper.getAstPackage(), "AST" + ruleName)>
  public Optional<${astNode}> getAstNode() {
    if(this.getAstNode().isPresent()) {
      return Optional.of((${astNode}) this.getAstNode().get());
    }
    return Optional.empty();
  }
  
  public void setAstNode(${ruleName} node) {
    this.node = node;
  }