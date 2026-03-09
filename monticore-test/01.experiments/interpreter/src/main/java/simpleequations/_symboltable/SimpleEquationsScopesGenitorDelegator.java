package simpleequations._symboltable;

public class SimpleEquationsScopesGenitorDelegator extends SimpleEquationsScopesGenitorDelegatorTOP{

  @Override
  public simpleequations._symboltable.ISimpleEquationsArtifactScope createFromAST (simpleequations._ast.ASTSimpleEquationCompilationUnit rootNode) {
    simpleequations._symboltable.ISimpleEquationsArtifactScope as =  symbolTable.createFromAST(rootNode);
    //only add this here
    as.setName(rootNode.getName());
    //until here
    if (as.isPresentName()){
      if (!as.getPackageName().isEmpty()){
        globalScope.addLoadedFile(as.getPackageName() + "." + as.getName());
      } else {
        globalScope.addLoadedFile(as.getName());
      }
    }
    return as;
  }
}
