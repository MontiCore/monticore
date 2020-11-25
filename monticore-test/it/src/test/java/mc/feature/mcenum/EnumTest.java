/* (c) https://github.com/MontiCore/monticore */

package mc.feature.mcenum;

import mc.GeneratorIntegrationsTest;

import mc.feature.mcenum.enums._ast.*;
import mc.feature.mcenum.enums._parser.EnumsParser;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnumTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testa() throws IOException {
  
    EnumsParser p = new EnumsParser();
    
    Optional<ASTA> optAst = p.parse(new StringReader("++++WORD"));
    assertTrue(optAst.isPresent());
    ASTA ast = optAst.get();
    assertEquals(true, ast.isA());
    assertEquals(true, ast.getE() == ASTE.PLUS);
    assertEquals(true, ast.getG() == ASTG.PLUS);
    assertEquals(true, ast.getF() == ASTF.PLUS);
    assertEquals(true, ast.getF().getIntValue() == ASTConstantsEnums.PLUS);
    assertEquals(true, ast.getF().ordinal() == 0);
    assertEquals(true, ast.getF().name() == "PLUS");
  }
  
   @Test
  public void testB() throws IOException {
  
     EnumsParser p = new EnumsParser();
    
    Optional<ASTB> optAst = p.parseB(new StringReader("++,++"));
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    assertEquals(true, ast.getE(0) == ASTE.PLUS);
    assertEquals(true, ast.getE(0).getIntValue() == ASTConstantsEnums.PLUS);
    assertEquals(2, ast.sizeEs());
    assertEquals(true, ast.getF(0) == ASTF.PLUS);
    assertEquals(true, ast.getF(0).getIntValue() == ASTConstantsEnums.PLUS);
    assertEquals(2, ast.sizeFs());
    
  }
  
   @Test
  public void testB2() throws IOException {
  
     EnumsParser p = new EnumsParser();
    
    Optional<ASTB> optAst = p.parseB(new StringReader("++,#+"));
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    
    assertEquals(true, ast.getE(0) == ASTE.PLUS);
    assertEquals(2, ast.sizeEs());
    assertEquals(true, ast.getF(0).ordinal() == 0);
    assertEquals(2, ast.sizeFs());
    assertEquals(ast.getF(0), ast.getF(1));
    assertEquals(true, ast.getF(0) == ASTF.PLUS);
    
  }
  
   @Test
  public void testB3() throws IOException {
  
     EnumsParser p = new EnumsParser();
    
    Optional<ASTB> optAst = p.parseB(new StringReader("++,#-"));
    assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
   
    assertEquals(2, ast.sizeEs());
    assertEquals(true, ast.getE(0) == ASTE.PLUS);
    assertEquals(true, ast.getE(1) == ASTE.PLUS);
    
    assertEquals(2, ast.sizeFs());
    assertEquals(true, ast.getF(0) == ASTF.PLUS);
    assertEquals(true, ast.getF(1) == ASTF.MINUS);
    
  }
}
