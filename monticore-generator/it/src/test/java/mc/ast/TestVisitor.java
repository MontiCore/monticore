/* (c) https://github.com/MontiCore/monticore */

package mc.ast;

import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._ast.ASTFeatureDSLNode;
import mc.feature.featuredsl._ast.ASTState;
import mc.feature.featuredsl._visitor.FeatureDSLVisitor;

import org.junit.Assert;

public class TestVisitor implements FeatureDSLVisitor {
  
  @Override
  public void traverse(ASTAutomaton a) {
    // Don't visit children
  }
  
  public void visit(ASTState a) {
    Assert.fail("Should be ignored by overriding the traverse method of automaton");
  }
  
  public void run(ASTFeatureDSLNode ast) {
    ast.accept(getRealThis());
  }
  
}
