/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance.delegator;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import mc.feature.visitor.inheritance.a._ast.AMill;
import mc.feature.visitor.inheritance.a._visitor.AVisitor;
import mc.feature.visitor.inheritance.b._ast.BMill;
import mc.feature.visitor.inheritance.b._visitor.BVisitor;
import mc.feature.visitor.inheritance.c._visitor.CDelegatorVisitor;
import mc.feature.visitor.inheritance.c._visitor.CVisitor;

/**
 * Tests composing of simple visitors with inheritance visitors.
 *
 * @author Robert Heim
 */
public class ComposeSimpleAndInheritanceTest extends CommonVisitorTest {
  
  // the composer
  private CDelegatorVisitor v = new CDelegatorVisitor();
  
  // the visitors about to compose
  private AVisitor aVis = new SimpleAVisitor(run);
  
  private BVisitor bVis = new InheritanceBVisitor(run);
  
  private CVisitor cVis = new InheritanceCVisitor(run);
  
  private boolean setUpDone = false;
  
  @Before
  public void setUp() {
    super.setup();
    run.setLength(0);
    expectedRun.setLength(0);
    if (!setUpDone) {
      setUpDone = true;
      v.setAVisitor(aVis);
      v.setBVisitor(bVis);
      v.setCVisitor(cVis);
    }
  }
  
  @Test
  public void testSimpleWithInhertiance() {
    v.handle(AMill.xABuilder().build());
    assertEquals("SimpleAVisitor.hXASimpleAVisitor.vXASimpleAVisitor.tXASimpleAVisitor.eXA",
        run.toString());
  }
  
  @Test
  public void testSimpleWithInhertiance2() {
    StringBuilder expectedRun = new StringBuilder();
    
    // handle from inheritance b visitor
    expectedRun.append("InheritanceBVisitor.hXB");
    
    // each visitor is called to visit astnode
    expectedRun.append("InheritanceCVisitor.vASTNode");
    expectedRun.append("InheritanceBVisitor.vASTNode");
    
    // visit node type in each of its super types using the corresponding visitor of the delegator
    expectedRun.append("SimpleAVisitor.vXA");
    expectedRun.append("InheritanceBVisitor.vXB");
    
    expectedRun.append("InheritanceBVisitor.tXB");
    
    expectedRun.append("InheritanceBVisitor.eXB");
    expectedRun.append("SimpleAVisitor.eXA");
    
    expectedRun.append("InheritanceBVisitor.eASTNode");
    expectedRun.append("InheritanceCVisitor.eASTNode");
    
    expectedRun.append("");
    
    // actual
    v.handle(BMill.xBBuilder().build());
    
    assertEquals(expectedRun.toString(), run.toString());
  }
}
