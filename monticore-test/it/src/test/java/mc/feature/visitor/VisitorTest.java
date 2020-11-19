/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.visitor.sub.SubMill;
import mc.feature.visitor.sub._ast.ASTB;
import mc.feature.visitor.sub._ast.ASTE;
import mc.feature.visitor.sub._parser.SubParser;
import mc.feature.visitor.sub._visitor.SubTraverser;
import mc.feature.visitor.sup._ast.ASTA;
import mc.feature.visitor.sup._visitor.SupVisitor2;

public class VisitorTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testConcreteVisitor() throws IOException {
    // Create AST
    SubParser p = new SubParser();
    Optional<ASTA> node = p.parseA(new StringReader("test1 test2"));
    assertFalse(p.hasErrors());
    assertTrue(node.isPresent());
    
    // Running Visitor
    SubTraverser t1 = SubMill.traverser();
    SubConcreteVisitor v = new SubConcreteVisitor();
    t1.setSubVisitor(v);
    
    t1.handle(node.get());
    assertTrue(v.hasVisited());

    SubTraverser t2 = SubMill.traverser();
    SupVisitor2 vSup = new SupVisitor2() {};
    t2.setSupVisitor(vSup);
    long errorCount = Log.getErrorCount();
    // no expected error, as super visitor should run on sub language
    t2.handle(node.get());
    assertEquals(errorCount, Log.getErrorCount());
  }
  
}
