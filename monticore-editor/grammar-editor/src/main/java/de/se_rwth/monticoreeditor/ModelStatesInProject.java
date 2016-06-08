/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
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
 *******************************************************************************/
package de.se_rwth.monticoreeditor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.modelloader.AstProvider;
import de.se_rwth.langeditor.modelstates.ModelState;

final class ModelStatesInProject implements AstProvider<ASTMCGrammar> {
  
  private final Set<ModelState> modelStates = new HashSet<>();
  
  @Override
  public ASTMCGrammar getRootNode(ModelCoordinate modelCoordinate) {
    String absoluteLocation = modelCoordinate.getLocation().toString();
    return modelStates.stream()
        .filter(modelState -> absoluteLocation
            .endsWith(modelState.getStorage().getFullPath().toString()))
        .findFirst()
        .map(ModelState::getRootNode)
        .filter(ASTMCGrammar.class::isInstance)
        .map(ASTMCGrammar.class::cast)
        .get();
  }
  
  public Set<ASTMCGrammar> getASTMCGrammars(){
    return modelStates.stream().map(ModelState::getRootNode)
        .filter(ASTMCGrammar.class::isInstance)
        .map(ASTMCGrammar.class::cast)
        .collect(Collectors.toSet());
  }
  
  void acceptModelState(ModelState modelState) {
    modelStates.add(modelState);
  }
  
  void remove(IProject project) {
    modelStates.removeIf(modelState -> modelState.getProject().equals(project));
  }
}
