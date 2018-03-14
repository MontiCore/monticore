/* (c) https://github.com/MontiCore/monticore */

package mc.feature.aststring;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.aststring.aststring._ast.ASTStart;
import mc.feature.aststring.aststring._parser.AststringParser;

public class TestASTStringParser extends GeneratorIntegrationsTest {
  
  @Test
  public void testParser() throws IOException {
    
    StringReader s = new StringReader(
        "start ah be ce , oh pe qu , x.y.z , de eh ef");
    
    AststringParser p = new AststringParser();
    java.util.Optional<ASTStart> opt = p.parseStart(s);
    assertTrue(opt.isPresent());
    ASTStart ast = opt.get();
    
    assertEquals(false, p.hasErrors());
    
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
  
}
