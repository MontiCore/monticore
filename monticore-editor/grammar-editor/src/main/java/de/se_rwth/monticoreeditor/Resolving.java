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

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTNonTerminalSeparator;
import de.monticore.symboltable.Symbol;
import de.monticore.utils.ASTNodes;
import de.se_rwth.langeditor.modelstates.ModelState;

class Resolving {
  
  private final Map<IProject, ModelStatesInProject> modelStatesInProjects = new HashMap<>();
  
  private final Map<IProject, SymbolTableMaintainer> symbolTableMaintainers = new HashMap<>();
  
  void buildProject(IProject project, ImmutableSet<ModelState> modelStates,
      ImmutableList<Path> modelPath) {
    ModelStatesInProject astMapper = new ModelStatesInProject();
    modelStatesInProjects.put(project, astMapper);
    modelStates.forEach(astMapper::acceptModelState);
    SymbolTableMaintainer symbolTableMaintainer = new SymbolTableMaintainer(astMapper, modelPath);
    modelStates.forEach(symbolTableMaintainer::acceptModelState);
    symbolTableMaintainers.put(project, symbolTableMaintainer);
  }
  
  void buildModel(ModelState modelState) {
    Optional.ofNullable(modelStatesInProjects.get(modelState.getProject()))
        .ifPresent(modelStatesInProject -> modelStatesInProject.acceptModelState(modelState));
    
    SymbolTableMaintainer value = symbolTableMaintainers.get(modelState.getProject());
    Optional.ofNullable(value)
        .ifPresent(maintainer -> maintainer.acceptModelState(modelState));
  }
  
  Optional<Supplier<Optional<ASTNode>>> createResolver(ASTNode astNode) {
    Optional<Supplier<Optional<ASTNode>>> resolveByNonTerminal =
        getEnclosingASTNode(astNode, ASTNonTerminal.class)
            .map(nonTerminal -> createSupplier(nonTerminal, nonTerminal.getName()));
    
    Optional<Supplier<Optional<ASTNode>>> resolveByNonTerminalSeparator =
        getEnclosingASTNode(astNode, ASTNonTerminalSeparator.class)
            .map(separator -> createSupplier(separator, separator.getName()));
    
    return resolveByNonTerminal.map(Optional::of).orElse(resolveByNonTerminalSeparator);
  }
  
  private Supplier<Optional<ASTNode>> createSupplier(ASTNode astNode, String name) {
    return () -> MCGrammarSymbolTableHelper.resolveRule(astNode, name).flatMap(Symbol::getAstNode);
  }
  
  private <T extends ASTNode> Optional<T> getEnclosingASTNode(ASTNode astNode, Class<T> type) {
    Optional<ImmutableMap<ASTNode, ASTNode>> optionalChildToParentMap = modelStatesInProjects
        .values().stream()
        .flatMap(modelStates -> modelStates.getASTMCGrammars().stream())
        .map(ASTNodes::childToParentMap)
        .filter(childToParentMap -> childToParentMap.keySet().contains(astNode))
        .findFirst();
    return optionalChildToParentMap.map(childToParentMap -> {
      ASTNode enclosingASTNode = astNode;
      while (enclosingASTNode != null && !type.isInstance(enclosingASTNode)) {
        enclosingASTNode = childToParentMap.get(enclosingASTNode);
      }
      return enclosingASTNode;
    }).map(type::cast);
  }
}
