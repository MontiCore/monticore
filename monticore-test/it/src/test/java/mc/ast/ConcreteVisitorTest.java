/* (c) https://github.com/MontiCore/monticore */

package mc.ast;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._ast.FeatureDSLNodeFactory;
import mc.feature.featuredsl._visitor.FeatureDSLTraverser;

import org.junit.Test;

public class ConcreteVisitorTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testA() {
    
    ASTAutomaton a = FeatureDSLNodeFactory.createASTAutomaton();
    a.getStatesList().add(FeatureDSLNodeFactory.createASTState());
    a.getStatesList().add(FeatureDSLNodeFactory.createASTState());
    a.getStatesList().add(FeatureDSLNodeFactory.createASTState());
    
    FeatureDSLTraverser traverser = FeatureDSLMill.traverser();
    TestVisitor v = new TestVisitor();
    traverser.setFeatureDSLVisitor(v);
    TestHandler h = new TestHandler();
    traverser.setFeatureDSLHandler(h);
    
    a.accept(traverser);
  }
}
