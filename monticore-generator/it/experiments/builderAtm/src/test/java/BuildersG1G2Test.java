/* (c) https://github.com/MontiCore/monticore */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import g1.G1Mill;
import g3.G3Mill;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.ast.Comment;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import g1._ast.ASTT;
import g1._ast.ASTTBuilder;
import g2.G2Mill;

public class BuildersG1G2Test {
  
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
    G3Mill.reset();
  }
  
  // tests whether inherited build-setters work
  @Test
  public void testG1MillTBuilder() throws IOException {
    ASTTBuilder tb =  G1Mill.tBuilder();
              tb.add_PreComment(new Comment("blubb1"))
                .add_PreComment(new Comment("blubb2"));
    assertEquals(2, tb.size_PreComments());
    assertEquals("blubb2", tb.get_PreComment(1).getText());
  }

  // tests whether inherited attributes are set through builder
  @Test
  public void testG1MillT() throws IOException {
    ASTTBuilder tb =  G1Mill.tBuilder();
    ASTT t =  tb.add_PreComment(new Comment("blubb1"))
    		.add_PreComment(new Comment("blubb2"))
		.build();
    assertEquals(2, t.size_PreComments());
    assertEquals("blubb2", t.get_PreComment(1).getText());
  }

  // tests whether generated build-setters work
  // (here: Overwriting T)
  @Test
  public void testG1MillS() throws IOException {
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
    g2._ast.ASTS s =  G2Mill.sBuilder()
                .setA(t7)
                .setB(t8)
                .addC(t9)
                .addC(t10)
                .build();
    assertTrue(t7 == s.getA());
    assertTrue(t8 == s.getB());
    assertEquals(5, s.sizeC());
    assertTrue(t10 == s.getC(4));
    assertEquals(g2._ast.ASTS.class, s.getClass());
  }

  // tests whether generated build-setters work in G2
  @Test
  public void testG2MillSAbsent() throws IOException {
    g2._ast.ASTS s =  G2Mill.sBuilder()
                .setA(t7)
                .addC(t10)
                .build();
    assert(!s.isPresentB());
    try {
    	s.getB();
      fail("Expected an Exception to be thrown");
    } catch (java.lang.IllegalStateException ex) { }
  }

  // tests: Builder from G2 derived through G1
  @Test
  public void testG2MillSthroughG1() throws IOException {
    G2Mill.init();
    g1._ast.ASTS s =  G1Mill.sBuilder()
                .setA(t7)
                .build();
    assertTrue(t7 == s.getA());
    assertEquals(g2._ast.ASTS.class, s.getClass());
  }

  // tests: Builder from G3 derived through G1
  @Test
  public void testG3MillSthroughG1G2() throws IOException {
    G3Mill.init();
    g1._ast.ASTS s =  G1Mill.sBuilder()
                .setA(t7)
                .build();
    assertTrue(t7 == s.getA());
    assertEquals(g3._ast.ASTS.class, s.getClass());

    g2._ast.ASTS s2 =  G2Mill.sBuilder()
                .setA(t7)
                .build();
    assertTrue(t7 == s2.getA());
    assertEquals(g3._ast.ASTS.class, s2.getClass());

    g3._ast.ASTS s3 =  G3Mill.sBuilder()
                .setA(t7)
                .build();
    assertTrue(t7 == s3.getA());
    assertEquals(g3._ast.ASTS.class, s3.getClass());
  }


}
