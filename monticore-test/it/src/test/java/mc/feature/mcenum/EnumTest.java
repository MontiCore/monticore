/* (c) https://github.com/MontiCore/monticore */

package mc.feature.mcenum;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.mcenum.enums.EnumsMill;
import mc.feature.mcenum.enums._ast.*;
import mc.feature.mcenum.enums._parser.EnumsParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(EnumsMill.class)
public class EnumTest {
  
  @Test
  public void testa() throws IOException {
    
    EnumsParser p = EnumsMill.parser();
    
    Optional<ASTA> optAst = p.parse_String("++++WORD");
    assertTrue(optAst.isPresent());
    ASTA ast = optAst.get();
    assertTrue(ast.isA());
    assertSame(ASTE.PLUS, ast.getE());
    assertSame(ASTG.PLUS, ast.getG());
    assertSame(ASTF.PLUS, ast.getF());
    assertEquals(ASTConstantsEnums.PLUS, ast.getF().getIntValue());
    assertEquals(0, ast.getF().ordinal());
    assertSame("PLUS", ast.getF().name());
  }
  
  @Test
  public void testB() throws IOException {
    
    EnumsParser p = EnumsMill.parser();
    
    Optional<ASTB> optAst = p.parse_StringB("++,++");
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    assertSame(ASTE.PLUS, ast.getE(0));
    assertEquals(ASTConstantsEnums.PLUS, ast.getE(0).getIntValue());
    assertEquals(2, ast.sizeEs());
    assertSame(ASTF.PLUS, ast.getF(0));
    assertEquals(ASTConstantsEnums.PLUS, ast.getF(0).getIntValue());
    assertEquals(2, ast.sizeFs());
  }
  
  @Test
  public void testB2() throws IOException {
    
    EnumsParser p = EnumsMill.parser();
    
    Optional<ASTB> optAst = p.parse_StringB("++,#+");
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    
    assertSame(ASTE.PLUS, ast.getE(0));
    assertEquals(2, ast.sizeEs());
    assertEquals(0, ast.getF(0).ordinal());
    assertEquals(2, ast.sizeFs());
    assertEquals(ast.getF(0), ast.getF(1));
    assertSame(ASTF.PLUS, ast.getF(0));
  }
  
  @Test
  public void testB3() throws IOException {
    
    EnumsParser p = EnumsMill.parser();
    
    Optional<ASTB> optAst = p.parse_StringB("++,#-");
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    
    assertEquals(2, ast.sizeEs());
    assertSame(ASTE.PLUS, ast.getE(0));
    assertSame(ASTE.PLUS, ast.getE(1));
    
    assertEquals(2, ast.sizeFs());
    assertSame(ASTF.PLUS, ast.getF(0));
    assertSame(ASTF.MINUS, ast.getF(1));
  }
}
