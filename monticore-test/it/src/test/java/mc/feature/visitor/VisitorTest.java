/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.visitor.sub.SubMill;
import mc.feature.visitor.sub._ast.ASTE;
import mc.feature.visitor.sub._parser.SubParser;
import mc.feature.visitor.sub._visitor.SubTraverser;
import mc.feature.visitor.sup._ast.ASTA;
import mc.feature.visitor.sup._visitor.SupVisitor2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    t1.add4Sub(v);
    
    t1.handle(node.get());
    assertTrue(v.hasVisited());

    SubTraverser t2 = SubMill.traverser();
    SupVisitor2 vSup = new SupVisitor2() {};
    t2.add4Sup(vSup);
    long errorCount = Log.getErrorCount();
    // no expected error, as super visitor should run on sub language
    t2.handle(node.get());
    assertEquals(errorCount, Log.getErrorCount());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  
  @Test
  public void testInheritanceTraversal() throws IOException {
    SubParser p = new SubParser();
    Optional<ASTE> node = p.parse_String("test2 NodeOverride");
    assertFalse(p.hasErrors());
    assertTrue(node.isPresent());
    
    // init with plain traverser
    SubTraverser t1 = SubMill.traverser();
    NodeCounter c1 = new NodeCounter();
    t1.add4Sup(c1);
    
    // plain traverser should not reach the interface implementation
    node.get().accept(t1);
    assertEquals(0, c1.getNum());
    
    
    // init with inheritance traverser
    SubTraverser t2 = SubMill.inheritanceTraverser();
    NodeCounter c2 = new NodeCounter();
    t2.add4Sup(c2);
    
    // inheritance traverser should reach the interface implementation precisely once
    node.get().accept(t2);
    assertEquals(1, c2.getNum());
    
  }
  
}
