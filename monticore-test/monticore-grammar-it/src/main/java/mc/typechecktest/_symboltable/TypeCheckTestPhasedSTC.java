/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest._symboltable;

import com.google.common.collect.Lists;
import mc.typechecktest.TypeCheckTestMill;
import mc.typechecktest._ast.ASTTCCompilationUnit;
import mc.typechecktest._visitor.TypeCheckTestTraverser;

import java.util.List;

public class TypeCheckTestPhasedSTC {

  protected List<TypeCheckTestTraverser> priorityList;

  protected TypeCheckTestScopesGenitorDelegator scopesGenitorDelegator;

  protected ITypeCheckTestGlobalScope globalScope;


  public TypeCheckTestPhasedSTC(){
    this.globalScope = TypeCheckTestMill.globalScope();
    this.scopesGenitorDelegator = TypeCheckTestMill.scopesGenitorDelegator();
    this.priorityList = Lists.newArrayList();

    TypeCheckTestTraverser traverser = TypeCheckTestMill.traverser();
    traverser.add4TypeCheckTest(new TypeCheckTestSTCompleteTypes());
    priorityList.add(traverser);
  }

  public ITypeCheckTestArtifactScope createFromAST(ASTTCCompilationUnit ast){
    ITypeCheckTestArtifactScope as = scopesGenitorDelegator.createFromAST(ast);
    priorityList.forEach(ast::accept);
    return as;
  }


}
