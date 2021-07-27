/* (c) https://github.com/MontiCore/monticore */

package de.monticore.tf.ast;

import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._visitor.FeatureDSLHandler;
import mc.feature.featuredsl._visitor.FeatureDSLTraverser;

public class TestHandler implements FeatureDSLHandler {
  
  FeatureDSLTraverser traverser;
  
  @Override
  public FeatureDSLTraverser getTraverser() {
    return this.traverser;
  }
  
  @Override
  public void setTraverser(FeatureDSLTraverser traverser) {
    this.traverser = traverser;
  }
	
  @Override
  public void traverse(ASTAutomaton a) {
    // Don't visit children
  }
  
}
