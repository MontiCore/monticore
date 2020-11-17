/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import mc.feature.visitor.inheritance.a.AMill;
import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.a._visitor.AHandler;
import mc.feature.visitor.inheritance.a._visitor.AVisitor2;
import mc.feature.visitor.inheritance.b.BMill;
import mc.feature.visitor.inheritance.b._ast.ASTYB;
import mc.feature.visitor.inheritance.b._ast.ASTZB;
import mc.feature.visitor.inheritance.b._visitor.BHandler;
import mc.feature.visitor.inheritance.b._visitor.BVisitor2;
import mc.feature.visitor.inheritance.c.CMill;
import mc.feature.visitor.inheritance.c._ast.ASTYC;
import mc.feature.visitor.inheritance.c._visitor.CHandler;
import mc.feature.visitor.inheritance.c._visitor.CTraverser;
import mc.feature.visitor.inheritance.c._visitor.CVisitor2;

/**
 * Tests composing simple visiors using the traverser visitor. The
 * SimpleXVisitors append "[NameOfVisitor].[h|t|v|e][ASTNode]" when a method of
 * them is called.
 * TODO NJ,DA replace Visitors by traverser/handler 
 */
public class ComposeSimpleTest extends CommonVisitorTest {
  
  // the composer
  private CTraverser traverser = CMill.traverser();
  
  // the simple visitors and handlers about to compose
  private AVisitor2 aVis = new SimpleAVisitor(run);
  private BVisitor2 bVis = new SimpleBVisitor(run);
  private CVisitor2 cVis = new SimpleCVisitor(run);
  private AHandler aHan = new SimpleAHandler(run);
  private BHandler bHan = new SimpleBHandler(run);
  private CHandler cHan = new SimpleCHandler(run);
  
  private boolean setUpDone = false;
  
  @Before
  public void setUp() {
    run.setLength(0);
    expectedRun.setLength(0);
    if (!setUpDone) {
      setUpDone = true;
      traverser.setAVisitor(aVis);
      traverser.setBVisitor(bVis);
      traverser.setCVisitor(cVis);
      traverser.setAHandler(aHan);
      traverser.setBHandler(bHan);
      traverser.setCHandler(cHan);
    }
  }
  
  @Test
  public void testSimpleComposed() {
    traverser.handle(AMill.xABuilder().build());
    assertEquals("SimpleAVisitor.hXASimpleAVisitor.vXASimpleAVisitor.tXASimpleAVisitor.eXA",
        run.toString());
  }
  
  @Test
  public void testSimpleComposed2() {
    traverser.handle(BMill.xBBuilder().build());
    assertEquals("SimpleBVisitor.hXBSimpleBVisitor.vXBSimpleBVisitor.tXBSimpleBVisitor.eXB",
        run.toString());
  }
  
  @Test
  public void testSimpleComposed3() {
    traverser.handle(mc.feature.visitor.inheritance.c.CMill.xCBuilder().build());
    assertEquals("SimpleCVisitor.hXCSimpleCVisitor.vXCSimpleCVisitor.tXCSimpleCVisitor.eXC",
        run.toString());
  }
  
  @Test
  public void testSimpleComposed4() {
    ASTYB yb = BMill.yBBuilder().build();
    ASTYC yc = mc.feature.visitor.inheritance.c.CMill.yCBuilder()
        .setYB(yb)
        .build();
    traverser.handle(yc);
    // first part of yc handling
    expectedRun.append("SimpleCVisitor.hYCSimpleCVisitor.vYCSimpleCVisitor.tYC");
    // handle yb
    expectedRun.append("SimpleBVisitor.hYBSimpleBVisitor.vYBSimpleBVisitor.tYBSimpleBVisitor.eYB");
    // rest of yc
    expectedRun.append("SimpleCVisitor.eYC");
    assertEquals(expectedRun.toString(), run.toString());
  }
  
  @Test
  public void testSimpleComposed5() {
    ASTYB yb = BMill.yBBuilder().build();
    ASTXA xa = AMill.xABuilder().build();
    ASTZB zb = BMill.zBBuilder()
        .setXA(xa)
        .setYB(yb)
        .build();
    traverser.handle(zb);
    
    // first part of zb handling
    expectedRun.append("SimpleBVisitor.hZBSimpleBVisitor.vZBSimpleBVisitor.tZB");
    // handle child xa
    expectedRun.append("SimpleAVisitor.hXASimpleAVisitor.vXASimpleAVisitor.tXASimpleAVisitor.eXA");
    // handle child yb
    expectedRun.append("SimpleBVisitor.hYBSimpleBVisitor.vYBSimpleBVisitor.tYBSimpleBVisitor.eYB");
    // rest of zb
    expectedRun.append("SimpleBVisitor.eZB");
    assertEquals(expectedRun.toString(), run.toString());
  }

}
