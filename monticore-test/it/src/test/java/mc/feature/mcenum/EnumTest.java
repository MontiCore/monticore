/* (c) https://github.com/MontiCore/monticore */

package mc.feature.mcenum;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.mcenum.enums.EnumsMill;
import mc.feature.mcenum.enums._ast.*;
import mc.feature.mcenum.enums._parser.EnumsParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class EnumTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testa() throws IOException {
    
    EnumsParser p = EnumsMill.parser();
    
    Optional<ASTA> optAst = p.parse(new StringReader("++++WORD"));
    assertTrue(optAst.isPresent());
    ASTA ast = optAst.get();
    assertTrue(ast.isA());
    assertSame(ASTE.PLUS, ast.getE());
    assertSame(ASTG.PLUS, ast.getG());
    assertSame(ASTF.PLUS, ast.getF());
    assertEquals(ASTConstantsEnums.PLUS, ast.getF().getIntValue());
    assertEquals(0, ast.getF().ordinal());
    assertEquals("PLUS", ast.getF().name());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testB() throws IOException {
    
    EnumsParser p = EnumsMill.parser();
    
    Optional<ASTB> optAst = p.parseB(new StringReader("++,++"));
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    assertSame(ASTE.PLUS, ast.getE(0));
    assertEquals(ASTConstantsEnums.PLUS, ast.getE(0).getIntValue());
    assertEquals(2, ast.sizeEs());
    assertSame(ASTF.PLUS, ast.getF(0));
    assertEquals(ASTConstantsEnums.PLUS, ast.getF(0).getIntValue());
    assertEquals(2, ast.sizeFs());
    
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testB2() throws IOException {
    
    EnumsParser p = EnumsMill.parser();
    
    Optional<ASTB> optAst = p.parseB(new StringReader("++,#+"));
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    
    assertSame(ASTE.PLUS, ast.getE(0));
    assertEquals(2, ast.sizeEs());
    assertEquals(0, ast.getF(0).ordinal());
    assertEquals(2, ast.sizeFs());
    assertEquals(ast.getF(0), ast.getF(1));
    assertSame(ASTF.PLUS, ast.getF(0));
    
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testB3() throws IOException {
    
    EnumsParser p = EnumsMill.parser();
    
    Optional<ASTB> optAst = p.parseB(new StringReader("++,#-"));
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    
    assertEquals(2, ast.sizeEs());
    assertSame(ASTE.PLUS, ast.getE(0));
    assertSame(ASTE.PLUS, ast.getE(1));
    
    assertEquals(2, ast.sizeFs());
    assertSame(ASTF.PLUS, ast.getF(0));
    assertSame(ASTF.MINUS, ast.getF(1));
    
    assertTrue(Log.getFindings().isEmpty());
  }
}
