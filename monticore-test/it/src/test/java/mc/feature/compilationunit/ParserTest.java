/* (c) https://github.com/MontiCore/monticore */

package mc.feature.compilationunit;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.compilationunit.compunit._ast.ASTCu;
import mc.feature.compilationunit.compunit._ast.ASTCuBar;
import mc.feature.compilationunit.compunit._ast.ASTCuFoo;
import mc.feature.compilationunit.compunit._parser.CompunitParser;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testFoo() throws IOException {
    CompunitParser p = new CompunitParser();
    
    Optional<ASTCu> cUnit = p.parseCu(new StringReader("foo a"));
    assertFalse(p.hasErrors());
    assertTrue(cUnit.isPresent());
    assertInstanceOf(ASTCuFoo.class, cUnit.get());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBar() throws IOException {
    CompunitParser p = new CompunitParser();
    
    Optional<ASTCu> cUnit = p.parseCu(new StringReader("bar a"));
    assertFalse(p.hasErrors());
    assertTrue(cUnit.isPresent());
    assertInstanceOf(ASTCuBar.class, cUnit.get());
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
