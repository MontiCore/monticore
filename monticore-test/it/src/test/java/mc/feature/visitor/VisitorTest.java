/* (c) https://github.com/MontiCore/monticore */

package mc.feature.visitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.visitor.sub.SubMill;
import mc.feature.visitor.sub._parser.SubParser;
import mc.feature.visitor.sub._visitor.SubTraverser;
import mc.feature.visitor.sup._ast.ASTA;
import mc.feature.visitor.sup._visitor.SupVisitor2;
import org.junit.jupiter.api.Test;

public class VisitorTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testConcreteVisitor() throws IOException {
    // Create AST
    SubParser p = new SubParser();
    Optional<ASTA> node = p.parseA(new StringReader("test1 test2"));
    Assertions.assertFalse(p.hasErrors());
    Assertions.assertTrue(node.isPresent());
    
    // Running Visitor
    SubTraverser t1 = SubMill.traverser();
    SubConcreteVisitor v = new SubConcreteVisitor();
    t1.add4Sub(v);
    
    t1.handle(node.get());
    Assertions.assertTrue(v.hasVisited());

    SubTraverser t2 = SubMill.traverser();
    SupVisitor2 vSup = new SupVisitor2() {};
    t2.add4Sup(vSup);
    long errorCount = Log.getErrorCount();
    // no expected error, as super visitor should run on sub language
    t2.handle(node.get());
    Assertions.assertEquals(errorCount, Log.getErrorCount());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
