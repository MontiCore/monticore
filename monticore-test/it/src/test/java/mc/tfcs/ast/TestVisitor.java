/* (c) https://github.com/MontiCore/monticore */

package de.monticore.tf.ast;

import org.junit.Assert;

import mc.feature.featuredsl._ast.ASTState;
import mc.feature.featuredsl._visitor.FeatureDSLVisitor2;

public class TestVisitor implements FeatureDSLVisitor2 {
  
  public void visit(ASTState a) {
    Assert.fail("Should be ignored by overriding the traverse method of automaton");
  }
  
}
