/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import de.monticore.ast.ASTNode;

/**
 * Mock for {@link GeneratorEngine}. Can always be used instead
 * of {@link GeneratorEngine}.
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class GeneratorEngineMock extends GeneratorEngine {
  
  private Map<ASTNode, List<String>> handledNodesAndTemplates = Maps.newHashMap();


  public GeneratorEngineMock(GeneratorSetup generatorSetup) {
    super(generatorSetup);
  }
   

  public Map<ASTNode, List<String>> getHandledNodesAndTemplates() {
    return this.handledNodesAndTemplates;
  }
  
}
