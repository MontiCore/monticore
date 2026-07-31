/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abstractprod;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;

import mc.GeneratorIntegrationsTest;
import mc.feature.abstractprod.abstractprod._ast.ASTA;
import mc.feature.abstractprod.abstractprod._ast.ASTB;
import mc.feature.abstractprod.abstractprod._ast.ASTC;
import mc.feature.abstractprod.abstractprod._parser.AbstractProdParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractProdTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testb() throws IOException {
    
    AbstractProdParser p = new AbstractProdParser();
    java.util.Optional<ASTA> ast = p.parseA(new StringReader("b"));
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTB.class, ast.get());
    assertFalse(p.hasErrors());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testc() throws IOException {
    
    AbstractProdParser p = new AbstractProdParser();
    java.util.Optional<ASTA> ast = p.parseA(new StringReader("c"));

    assertTrue(ast.isPresent());
    assertInstanceOf(ASTC.class, ast.get());
    assertFalse(p.hasErrors());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
