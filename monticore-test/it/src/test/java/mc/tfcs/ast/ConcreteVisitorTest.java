/* (c) https://github.com/MontiCore/monticore */

package de.monticore.tf.ast;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._visitor.FeatureDSLTraverser;
import org.junit.Test;

public class ConcreteVisitorTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testA() {
    
    ASTAutomaton a = FeatureDSLMill.automatonBuilder().uncheckedBuild();
    a.getStatesList().add(FeatureDSLMill.stateBuilder().uncheckedBuild());
    a.getStatesList().add(FeatureDSLMill.stateBuilder().uncheckedBuild());
    a.getStatesList().add(FeatureDSLMill.stateBuilder().uncheckedBuild());
    
    FeatureDSLTraverser traverser = FeatureDSLMill.traverser();
    TestVisitor v = new TestVisitor();
    traverser.add4FeatureDSL(v);
    TestHandler h = new TestHandler();
    traverser.setFeatureDSLHandler(h);
    
    a.accept(traverser);
  }
}
