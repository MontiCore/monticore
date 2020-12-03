/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor.inheritance;

import static org.junit.Assert.assertEquals;

import mc.feature.visitor.inheritance.a.AMill;
import mc.feature.visitor.inheritance.b.BMill;
import mc.feature.visitor.inheritance.c.CMill;
import org.junit.Test;

import de.monticore.ast.ASTNode;
import mc.GeneratorIntegrationsTest;
import mc.feature.visitor.inheritance.a._ast.ASTXA;
import mc.feature.visitor.inheritance.b._ast.ASTXB;
import mc.feature.visitor.inheritance.c._ast.ASTXC;
import mc.feature.visitor.inheritance.c._visitor.CTraverser;

/**
 * Tests that for grammar C extends B extends A the CVisitor also visits rules
 * from B and A. Furthermore, we test that rules extending rules from a super
 * grammar are visited in both types, the sub and the super type.
 * 
 */
public class VisitorTest extends GeneratorIntegrationsTest {
  @Test
  public void testSimple() {
    CTraverser traverser = CMill.traverser();
    SimpleVisitor v = new SimpleVisitor();
    traverser.add4A(v);
    traverser.add4B(v);
    traverser.add4C(v);
    
    traverser.handle(AMill.xABuilder().build());
    assertEquals("A", v.getRun());
    v.clear();
    traverser.handle(BMill.xBBuilder().build());
    assertEquals("B", v.getRun());
    v.clear();
    traverser.handle(CMill.xCBuilder().build());
    assertEquals("C", v.getRun());
    v.clear();
  }
}
