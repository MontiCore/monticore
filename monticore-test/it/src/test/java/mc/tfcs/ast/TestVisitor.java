/* (c) https://github.com/MontiCore/monticore */

package mc.tfcs.ast;

import mc.feature.featuredsl._ast.ASTState;
import mc.feature.featuredsl._visitor.FeatureDSLVisitor2;

import static org.junit.jupiter.api.Assertions.fail;

public class TestVisitor implements FeatureDSLVisitor2 {
  
  public void visit(ASTState a) {
    fail("Should be ignored by overriding the traverse method of automaton");
  }
  
}
