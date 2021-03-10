/* (c) https://github.com/MontiCore/monticore */

package mc.feature.ast;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import mc.feature.delete.deletetest.DeleteTestMill;
import mc.feature.delete.deletetest._ast.ASTChild;
import mc.feature.delete.deletetest._ast.ASTParent;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.ASTA;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ASTTest {
  
  @BeforeClass
  public static void setup() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testGet_ChildNodes1() {
    List<ASTA> aList = new ArrayList<>();
    ASTA a = FeatureDSLMill.aBuilder().build();
    assertEquals(0, aList.size());
    aList.add(a);
    assertEquals(1, aList.size());
  }
  
  @Test
  public void testGet_ChildNodes2() {
    ASTParent p = DeleteTestMill.parentBuilder().build();
    ASTChild s = DeleteTestMill.childBuilder().build();
    p.addChild(s);
    p.setSon(s);
    assertEquals(1, p.getChildList().size());
    assertTrue(p.containsChild(s));
  }
  
}
