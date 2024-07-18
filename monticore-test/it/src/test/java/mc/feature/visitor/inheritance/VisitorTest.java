/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.se_rwth.commons.logging.LogStub;
import mc.feature.visitor.inheritance.a.AMill;
import mc.feature.visitor.inheritance.b.BMill;
import mc.feature.visitor.inheritance.c.CMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.visitor.inheritance.c._visitor.CTraverser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

/**
 * Tests that for grammar C extends B extends A the CVisitor also visits rules
 * from B and A. Furthermore, we test that rules extending rules from a super
 * grammar are visited in both types, the sub and the super type.
 * 
 */
public class VisitorTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testSimple() {
    CTraverser traverser = CMill.traverser();
    SimpleVisitor v = new SimpleVisitor();
    traverser.add4A(v);
    traverser.add4B(v);
    traverser.add4C(v);
    
    traverser.handle(AMill.xABuilder().build());
    Assertions.assertEquals("A", v.getRun());
    v.clear();
    traverser.handle(BMill.xBBuilder().build());
    Assertions.assertEquals("B", v.getRun());
    v.clear();
    traverser.handle(CMill.xCBuilder().build());
    Assertions.assertEquals("C", v.getRun());
    v.clear();
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
