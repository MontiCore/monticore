/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest._symboltable;

import mc.typechecktest._ast.ASTTCCompilationUnit;

public class TypeCheckTestScopesGenitor extends TypeCheckTestScopesGenitorTOP {

  public TypeCheckTestScopesGenitor(){
    super();
  }

  @Override
  public ITypeCheckTestArtifactScope createFromAST(ASTTCCompilationUnit ast){
    ITypeCheckTestArtifactScope as = super.createFromAST(ast);
    as.setPackageName(ast.getMCPackageDeclaration().getMCQualifiedName().getQName());
    return as;
  }





}
