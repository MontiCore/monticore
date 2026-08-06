/* (c) https://github.com/MontiCore/monticore */

package mc.feature.abstractprod;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.abstractprod.abstractprod.AbstractProdMill;
import mc.feature.abstractprod.abstractprod._ast.ASTA;
import mc.feature.abstractprod.abstractprod._ast.ASTB;
import mc.feature.abstractprod.abstractprod._ast.ASTC;
import mc.feature.abstractprod.abstractprod._parser.AbstractProdParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AbstractProdMill.class)
public class AbstractProdTest {

  @Test
  public void testB() throws IOException {
    AbstractProdParser p = AbstractProdMill.parser();
    Optional<ASTA> ast = p.parse_StringA("b");
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTB.class, ast.get());
    assertFalse(p.hasErrors());
  }
  
  @Test
  public void testC() throws IOException {
    AbstractProdParser p = AbstractProdMill.parser();
    Optional<ASTA> ast = p.parse_StringA("c");

    assertTrue(ast.isPresent());
    assertInstanceOf(ASTC.class, ast.get());
    assertFalse(p.hasErrors());
  }
}
