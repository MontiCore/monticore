/* (c) Monticore license: https://github.com/MontiCore/monticore */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import g1._ast.ASTT;
import g1._ast.G1Mill;
import g2._ast.G2Mill;
import g3._ast.G3Mill;

public class Builders4Test {
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }

  ASTT t7,t8,t9,t10;

  @Before
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
    t7 =  G2Mill.tBuilder().build();
    t8 =  G2Mill.tBuilder().build();
    t9 =  G2Mill.tBuilder().build();
    t10 =  G2Mill.tBuilder().build();
	// MB: Die beiden n�chsten Zeilen k�nnen erst nach dem n�chsten
	// Release aus. Das reset der SuperGrammar funktioniert erst dann.
    G1Mill.reset();
    G2Mill.reset();
    G3Mill.reset();
  }
  
  // tests whether generated build-setters work
  // (here: Overwriting T)
  @Test
  public void testG1MillS() throws IOException {
    G1Mill.init();
    g1._ast.ASTS s =  G1Mill.sBuilder()
    		.setA(t7)
    		.setA(t8)
		.build();
    assertTrue(t8 == s.getA());
    assertTrue(!(t7 == s.getA()));
    assertEquals(g1._ast.ASTS.class, s.getClass());
  }
  
  // tests whether generated build-setters work in G2
  @Test
  public void testG2MillS() throws IOException {
    G2Mill.init();
    g2._ast.ASTS s =  G2Mill.sBuilder()
                .setA(t7)
                .setB(t8)
                .addC(t9)
                .addC(t10)
                .build();
    assertTrue(t7 == s.getA());
    assertTrue(t8 == s.getB());
    assertEquals(5, s.sizeCs());  // 3 are predefined in the hc builder
    assertTrue(t10 == s.getC(4));
    assertEquals(g2._ast.ASTS.class, s.getClass());
  }

  // tests: Builder from G2 derived through G1
  @Test
  public void testG2MillSthroughG1() throws IOException {
    G2Mill.init();
    g1._ast.ASTS s =  G1Mill.sBuilder()
                .setA(t7)
                .build();
    assertEquals(g2._ast.ASTS.class, s.getClass());
    // 3 are predefined in the hc builder
    assertEquals(3, ((g2._ast.ASTS)s).sizeCs());
  }

  // tests: Builder from G3 derived through G1
  @Test
  public void testG3MillSthroughG1G2() throws IOException {
    G3Mill.init();
    g2._ast.ASTS s2 =  G2Mill.sBuilder()
                .setA(t7)
                .build();
    // the hwc Mill from G2 doesn't operate anymore, because S is
    // overwritten (so the result is 0) 
    assertEquals(0, s2.sizeCs());
    assertEquals(g3._ast.ASTS.class, s2.getClass());
  }

  // tests: Builder from G3 derived through G1
  @Test
  public void testG3MillSthroughG1G2G3() throws IOException {
    G3Mill.init();
    g3._ast.ASTS s3 =  G3Mill.sBuilder()
                .setA(t7)
                .build();
    // the hwc Mill from G2 doesn't operate anymore, because S is 
    // overwritten (so the result is 0)
    assertEquals(0, s3.sizeCs());  
    assertEquals(g3._ast.ASTS.class, s3.getClass());
  }

}
