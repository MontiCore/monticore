/* (c) https://github.com/MontiCore/monticore */

package mc.feature.compilationunit;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.compilationunit.compunit.CompunitMill;
import mc.feature.compilationunit.compunit._ast.ASTCu;
import mc.feature.compilationunit.compunit._ast.ASTCuBar;
import mc.feature.compilationunit.compunit._ast.ASTCuFoo;
import mc.feature.compilationunit.compunit._parser.CompunitParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(CompunitMill.class)
public class ParserTest {

  @Test
  public void testFoo() throws IOException {
    CompunitParser p = CompunitMill.parser();
    
    Optional<ASTCu> cUnit = p.parseCu(new StringReader("foo a"));
    assertFalse(p.hasErrors());
    assertTrue(cUnit.isPresent());
    assertInstanceOf(ASTCuFoo.class, cUnit.get());
  }
  
  @Test
  public void testBar() throws IOException {
    CompunitParser p = CompunitMill.parser();
    
    Optional<ASTCu> cUnit = p.parseCu(new StringReader("bar a"));
    assertFalse(p.hasErrors());
    assertTrue(cUnit.isPresent());
    assertInstanceOf(ASTCuBar.class, cUnit.get());
  }
  
}
