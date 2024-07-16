/* (c) https://github.com/MontiCore/monticore */
package mc.tfcs.ast;

import de.monticore.tf.ast.TestHandler;
import de.monticore.tf.ast.TestVisitor;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._visitor.FeatureDSLTraverser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertTrue;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

public class ConcreteVisitorTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
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
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
