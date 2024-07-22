/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abstractprod;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.abstractprod.abstractprod._ast.ASTA;
import mc.feature.abstractprod.abstractprod._ast.ASTB;
import mc.feature.abstractprod.abstractprod._ast.ASTC;
import mc.feature.abstractprod.abstractprod._parser.AbstractProdParser;
import org.junit.jupiter.api.Test;

public class AbstractProdTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testb() throws IOException {
    
    AbstractProdParser p = new AbstractProdParser();
    java.util.Optional<ASTA> ast = p.parseA(new StringReader("b"));
    
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(ast.get() instanceof ASTB);
    Assertions.assertFalse(p.hasErrors());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testc() throws IOException {
    
    AbstractProdParser p = new AbstractProdParser();
    java.util.Optional<ASTA> ast = p.parseA(new StringReader("c"));

    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(ast.get() instanceof ASTC);
    Assertions.assertFalse(p.hasErrors());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
