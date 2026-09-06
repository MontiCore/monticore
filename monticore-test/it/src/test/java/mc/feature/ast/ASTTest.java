/* (c) https://github.com/MontiCore/monticore */

package mc.feature.ast;

import de.monticore.runtime.junit.AbstractMCTest;
import mc.feature.delete.deletetest.DeleteTestMill;
import mc.feature.delete.deletetest._ast.ASTChild;
import mc.feature.delete.deletetest._ast.ASTParent;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.ASTA;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ASTTest extends AbstractMCTest {
  
  @Test
  public void testGet_ChildNodes1() {
    FeatureDSLMill.init();
    List<ASTA> aList = new ArrayList<>();
    ASTA a = FeatureDSLMill.aBuilder().build();
    assertEquals(0, aList.size());
    aList.add(a);
    assertEquals(1, aList.size());
  }
  
  @Test
  public void testGet_ChildNodes2() {
    DeleteTestMill.init();
    ASTParent p = DeleteTestMill.parentBuilder().build();
    ASTChild s = DeleteTestMill.childBuilder().build();
    p.addChild(s);
    p.setSon(s);
    assertEquals(1, p.getChildList().size());
    assertTrue(p.containsChild(s));
  }
  
}
