/* (c) https://github.com/MontiCore/monticore */

package mc.feature.ast;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import de.se_rwth.commons.logging.Slf4jLog;
import mc.feature.delete.deletetest.DeleteTestMill;
import mc.feature.delete.deletetest._ast.ASTChild;
import mc.feature.delete.deletetest._ast.ASTParent;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.ASTA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ASTTest {
  
  @BeforeAll
  public static void setup() {
    Slf4jLog.init();
  }
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testGet_ChildNodes1() {
    List<ASTA> aList = new ArrayList<>();
    ASTA a = FeatureDSLMill.aBuilder().build();
    Assertions.assertEquals(0, aList.size());
    aList.add(a);
    Assertions.assertEquals(1, aList.size());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testGet_ChildNodes2() {
    ASTParent p = DeleteTestMill.parentBuilder().build();
    ASTChild s = DeleteTestMill.childBuilder().build();
    p.addChild(s);
    p.setSon(s);
    Assertions.assertEquals(1, p.getChildList().size());
    Assertions.assertTrue(p.containsChild(s));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
