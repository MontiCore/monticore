/* (c) https://github.com/MontiCore/monticore */
package mc.tfcs.ast;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._visitor.FeatureDSLTraverser;
import org.junit.jupiter.api.Test;

@TestWithMCLanguage(FeatureDSLMill.class)
public class ConcreteVisitorTest {
  
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
