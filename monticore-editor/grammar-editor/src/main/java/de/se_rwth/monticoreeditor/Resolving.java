/* (c)  https://github.com/MontiCore/monticore */

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
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTNonTerminalSeparator;
import de.monticore.symboltable.Symbol;
import de.monticore.utils.ASTNodes;
import de.se_rwth.langeditor.modelstates.ModelState;
import de.se_rwth.langeditor.util.ResourceLocator;

class Resolving {
  
  private Map<IProject, ModelStatesInProject> modelStatesInProjects = new HashMap<>();
  
  private Map<IProject, SymbolTableMaintainer> symbolTableMaintainers = new HashMap<>();
  
  void buildProject(IProject project, ImmutableSet<ModelState> modelStates,
      ImmutableList<Path> modelPath) {
    ModelStatesInProject astMapper = getModelStatesInProject(project);
    modelStates.forEach(astMapper::acceptModelState);
    SymbolTableMaintainer symbolTableMaintainer = getSymbolTableMaintainer(project, modelPath);
    modelStates.forEach(symbolTableMaintainer::acceptModelState);
  }
  
  void buildModel(ModelState modelState) {
    ModelStatesInProject modelStatesInProject = getModelStatesInProject(modelState.getProject());
    modelStatesInProject.acceptModelState(modelState);
    
    SymbolTableMaintainer maintainer = getSymbolTableMaintainer(modelState.getProject());
    maintainer.acceptModelState(modelState);
  }
  
  Optional<Supplier<Optional<ASTNode>>> createResolver(ASTNode astNode) {
    Optional<Supplier<Optional<ASTNode>>> resolveByNonTerminal = getEnclosingASTNode(astNode,
        ASTNonTerminal.class)
            .map(nonTerminal -> createSupplier(nonTerminal, nonTerminal.getName()));
    if (resolveByNonTerminal.isPresent()) {
      return resolveByNonTerminal;
    }
    
    Optional<ASTNonTerminalSeparator> nonTerminalSep = getEnclosingASTNode(astNode,
        ASTNonTerminalSeparator.class);
    if (nonTerminalSep.isPresent()) {
      Optional<ASTMCGrammar> grammarNode = getEnclosingASTNode(nonTerminalSep.get(), ASTMCGrammar.class);
      if (grammarNode.isPresent()) {
        return Optional.of(createSupplier(grammarNode.get(),
            nonTerminalSep.get().getName()));
      }   
    }
    
    return Optional.empty();
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
  
  private ModelStatesInProject getModelStatesInProject(IProject project) {
    if (!modelStatesInProjects.containsKey(project)) {
      ModelStatesInProject astMapper = new ModelStatesInProject();
      modelStatesInProjects.put(project, astMapper);
    }
    return modelStatesInProjects.get(project);
  }
  
  private SymbolTableMaintainer getSymbolTableMaintainer(IProject project) {
    if (!symbolTableMaintainers.containsKey(project)) {
      SymbolTableMaintainer symbolTableMaintainer = new SymbolTableMaintainer(
          getModelStatesInProject(project), ResourceLocator.assembleModelPath(project));
      symbolTableMaintainers.put(project, symbolTableMaintainer);
    }
    return symbolTableMaintainers.get(project);
  }
  
  private SymbolTableMaintainer getSymbolTableMaintainer(IProject project,
      ImmutableList<Path> modelPath) {
    if (!symbolTableMaintainers.containsKey(project)) {
      SymbolTableMaintainer symbolTableMaintainer = new SymbolTableMaintainer(
          getModelStatesInProject(project), modelPath);
      symbolTableMaintainers.put(project, symbolTableMaintainer);
    }
    return symbolTableMaintainers.get(project);
  }
  
}
