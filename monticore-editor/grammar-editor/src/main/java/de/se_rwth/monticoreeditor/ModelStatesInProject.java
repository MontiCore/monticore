/* (c)  https://github.com/MontiCore/monticore */

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
