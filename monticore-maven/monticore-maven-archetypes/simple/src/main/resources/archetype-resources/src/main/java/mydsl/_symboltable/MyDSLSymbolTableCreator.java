/* (c) https://github.com/MontiCore/monticore */

package ${package}.mydsl._symboltable;

import ${package}.mydsl._ast.ASTMyModel;
import de.se_rwth.commons.Names;

import java.util.Deque;

public class MyDSLSymbolTableCreator extends MyDSLSymbolTableCreatorTOP {

  /**
   * must overwrite both constructors with super
   */
  public MyDSLSymbolTableCreator(IMyDSLScope enclosingScope) {
    super(enclosingScope);
  }

  public MyDSLSymbolTableCreator(Deque<? extends IMyDSLScope> scopeStack) {
    super(scopeStack);
  }

  /**
   * add optional package defined in ASTMyModel to the artifact scope
   */
  @Override
  public MyDSLArtifactScope createFromAST(ASTMyModel rootNode) {
    // add package name to artifact scope
    MyDSLArtifactScope artifactScope = MyDSLSymTabMill.myDSLArtifactScopeBuilder()
        .setPackageName(Names.getQualifiedName(rootNode.getPackageList()))
        .build();
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  /**
   * add optional package defined in ASTMyModel to MyModelSymbol
   */
  @Override
  protected  MyModelSymbol create_MyModel (ASTMyModel ast)  {
    return MyDSLSymTabMill.myModelSymbolBuilder()
        .setName(ast.getName())
        .setPackageName(Names.getQualifiedName(ast.getPackageList()))
        .build();
  }


}
