/* (c) https://github.com/MontiCore/monticore */

package mc.ast;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._ast.FeatureDSLNodeFactory;

import org.junit.Test;

public class ConcreteVisitorTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testA() {
    
    ASTAutomaton a = FeatureDSLNodeFactory.createASTAutomaton();
    a.getStatesList().add(FeatureDSLNodeFactory.createASTState());
    a.getStatesList().add(FeatureDSLNodeFactory.createASTState());
    a.getStatesList().add(FeatureDSLNodeFactory.createASTState());
    
    TestVisitor v = new TestVisitor();
    v.run(a);
  }
}
