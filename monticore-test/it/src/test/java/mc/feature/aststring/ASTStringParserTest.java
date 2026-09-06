/* (c) https://github.com/MontiCore/monticore */

package mc.feature.aststring;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.aststring.aststring.AststringMill;
import mc.feature.aststring.aststring._ast.ASTStart;
import mc.feature.aststring.aststring._ast.ASTTestSingleQuote;
import mc.feature.aststring.aststring._parser.AststringParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AststringMill.class)
public class ASTStringParserTest {

  @Test
  public void testParser() throws IOException {
    AststringParser p = AststringMill.parser();
    Optional<ASTStart> opt = p.parse_StringStart("start ah be ce , oh pe qu , x.y.z , de eh ef");
    assertTrue(opt.isPresent());
    ASTStart ast = opt.get();
    
    assertFalse(p.hasErrors());
    
    // Test parsing
    assertEquals("ah", ast.getAList().get(0));
    assertEquals("be", ast.getAList().get(1));
    assertEquals("ce", ast.getAList().get(2));
    assertEquals("oh", ast.getBList().get(0));
    assertEquals("pe", ast.getBList().get(1));
    assertEquals("qu", ast.getBList().get(2));
    assertEquals("x", ast.getCList().get(0));
    assertEquals("y", ast.getCList().get(1));
    assertEquals("z", ast.getCList().get(2));
    assertEquals("de", ast.getDList().get(0));
    assertEquals("eh", ast.getDList().get(1));
    
    // Test toString method
    assertEquals("ef", ast.getDList().get(2).toString());
  }

  @Test
  public void testSingleQuote() throws IOException {
    Optional<ASTTestSingleQuote> ast = AststringMill.parser().parse_StringTestSingleQuote("Alex's Parser probleme");
    assertTrue(ast.isPresent());
  }
  
}
