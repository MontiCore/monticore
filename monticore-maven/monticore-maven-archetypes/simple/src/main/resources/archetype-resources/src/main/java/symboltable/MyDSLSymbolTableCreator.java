/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
