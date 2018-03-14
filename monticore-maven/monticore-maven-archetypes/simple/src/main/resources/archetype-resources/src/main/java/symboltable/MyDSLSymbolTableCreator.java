/* (c) https://github.com/MontiCore/monticore */

package ${package}.symboltable;

import java.util.ArrayList;
import java.util.Optional;

import ${package}.mydsl._ast.ASTMyElement;
import ${package}.mydsl._ast.ASTMyField;
import ${package}.mydsl._ast.ASTMyModel;
import ${package}.mydsl._visitor.MyDSLVisitor;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class MyDSLSymbolTableCreator extends CommonSymbolTableCreator implements MyDSLVisitor {

  public MyDSLSymbolTableCreator(
      final ResolvingConfiguration resolvingConfig,
      final MutableScope enclosingScope) {
    super(resolvingConfig, enclosingScope);
  }

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Scope createFromAST(ASTMyModel rootNode) {
    Log.errorIfNull(rootNode);
    rootNode.accept(this);
    return getFirstCreatedScope();
  }
  
  @Override
  public void visit(final ASTMyModel myModelNode) {
    final ArtifactScope artifactScope = new ArtifactScope(Optional.empty(), "", new ArrayList<>());
    putOnStack(artifactScope);

    final MyModelSymbol myModel = new MyModelSymbol(myModelNode.getName());
    putInScopeAndLinkWithAst(myModel, myModelNode);
  }
  
  @Override
  public void endVisit(final ASTMyModel myModelNode) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(final ASTMyElement myElementNode) {
    final MyElementSymbol myElementSymbol = new MyElementSymbol(myElementNode.getName());

    putInScopeAndLinkWithAst(myElementSymbol, myElementNode);
  }
  
  @Override
  public void visit(final ASTMyField myFieldNode) {
    final MyElementSymbolReference type =
        new MyElementSymbolReference(myFieldNode.getType(), currentScope().get());
    
    final MyFieldSymbol myFieldSymbol =
        new MyFieldSymbol(myFieldNode.getName(), type);

    putInScopeAndLinkWithAst(myFieldSymbol, myFieldNode);
  }
}
