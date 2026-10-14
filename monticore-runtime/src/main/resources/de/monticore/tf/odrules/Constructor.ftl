<#-- (c) https://github.com/MontiCore/monticore -->
public ${ast.getJavaClassName()}(List<ASTNode> hostGraph) {
  this.modelAccessor = new ModelAccessor(${grammarName}Mill::inheritanceTraverser, hostGraph);
	this.glex = new GlobalExtensionManagement();
}

public ${ast.getJavaClassName()}(ASTNode... hostGraph){
  this(Lists.newArrayList(hostGraph));
}

public ${ast.getJavaClassName()}(GlobalExtensionManagement glex, ASTNode... hostGraph){
  this.modelAccessor = new ModelAccessor(${grammarName}Mill::inheritanceTraverser, hostGraph);
	this.glex = glex;
}

public ${ast.getJavaClassName()}(GlobalExtensionManagement glex, ASTNode astNode){
  this(astNode, glex);
}

public ${ast.getJavaClassName()}(ASTNode astNode) {
	this(astNode, new GlobalExtensionManagement());
}

public ${ast.getJavaClassName()}(ASTNode astNode, GlobalExtensionManagement glex) {
  this.modelAccessor = new ModelAccessor(${grammarName}Mill::inheritanceTraverser, astNode);
  this.glex = glex;
}

public ${ast.getJavaClassName()}(IModelAccessor modelAccessor) {
  this(modelAccessor, new GlobalExtensionManagement());
}

public ${ast.getJavaClassName()}(IModelAccessor modelAccessor, GlobalExtensionManagement glex) {
  this.modelAccessor = modelAccessor;
  this.glex = glex;
}
