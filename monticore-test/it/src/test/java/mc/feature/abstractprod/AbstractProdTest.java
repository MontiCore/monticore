/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abstractprod;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.abstractprod.abstractprod._ast.ASTA;
import mc.feature.abstractprod.abstractprod._ast.ASTB;
import mc.feature.abstractprod.abstractprod._ast.ASTC;
import mc.feature.abstractprod.abstractprod._parser.AbstractProdParser;

public class AbstractProdTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testb() throws IOException {
    
    AbstractProdParser p = new AbstractProdParser();
    java.util.Optional<ASTA> ast = p.parseA(new StringReader("b"));
    
    assertTrue(ast.isPresent());
    assertTrue(ast.get() instanceof ASTB);
    assertFalse(p.hasErrors());
    
  }
  
  @Test
  public void testc() throws IOException {
    
    AbstractProdParser p = new AbstractProdParser();
    java.util.Optional<ASTA> ast = p.parseA(new StringReader("c"));

    assertTrue(ast.isPresent());
    assertTrue(ast.get() instanceof ASTC);
    assertFalse(p.hasErrors());
    
  }
}
