/* (c) https://github.com/MontiCore/monticore */

package mc.feature.mcenum;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;

import mc.feature.mcenum.enums._ast.*;
import mc.feature.mcenum.enums._parser.EnumsParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnumTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testa() throws IOException {
  
    EnumsParser p = new EnumsParser();
    
    Optional<ASTA> optAst = p.parse(new StringReader("++++WORD"));
    Assertions.assertTrue(optAst.isPresent());
    ASTA ast = optAst.get();
    Assertions.assertEquals(true, ast.isA());
    Assertions.assertEquals(true, ast.getE() == ASTE.PLUS);
    Assertions.assertEquals(true, ast.getG() == ASTG.PLUS);
    Assertions.assertEquals(true, ast.getF() == ASTF.PLUS);
    Assertions.assertEquals(true, ast.getF().getIntValue() == ASTConstantsEnums.PLUS);
    Assertions.assertEquals(true, ast.getF().ordinal() == 0);
    Assertions.assertEquals(true, ast.getF().name() == "PLUS");
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
   @Test
  public void testB() throws IOException {
  
     EnumsParser p = new EnumsParser();
    
    Optional<ASTB> optAst = p.parseB(new StringReader("++,++"));
    Assertions.assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    Assertions.assertEquals(true, ast.getE(0) == ASTE.PLUS);
    Assertions.assertEquals(true, ast.getE(0).getIntValue() == ASTConstantsEnums.PLUS);
    Assertions.assertEquals(2, ast.sizeEs());
    Assertions.assertEquals(true, ast.getF(0) == ASTF.PLUS);
    Assertions.assertEquals(true, ast.getF(0).getIntValue() == ASTConstantsEnums.PLUS);
    Assertions.assertEquals(2, ast.sizeFs());
  
     Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
   @Test
  public void testB2() throws IOException {
  
     EnumsParser p = new EnumsParser();
    
    Optional<ASTB> optAst = p.parseB(new StringReader("++,#+"));
    Assertions.assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
    
    Assertions.assertEquals(true, ast.getE(0) == ASTE.PLUS);
    Assertions.assertEquals(2, ast.sizeEs());
    Assertions.assertEquals(true, ast.getF(0).ordinal() == 0);
    Assertions.assertEquals(2, ast.sizeFs());
    Assertions.assertEquals(ast.getF(0), ast.getF(1));
    Assertions.assertEquals(true, ast.getF(0) == ASTF.PLUS);
  
     Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
   @Test
  public void testB3() throws IOException {
  
     EnumsParser p = new EnumsParser();
    
    Optional<ASTB> optAst = p.parseB(new StringReader("++,#-"));
    Assertions.assertTrue(optAst.isPresent());
    ASTB ast = optAst.get();
   
    Assertions.assertEquals(2, ast.sizeEs());
    Assertions.assertEquals(true, ast.getE(0) == ASTE.PLUS);
    Assertions.assertEquals(true, ast.getE(1) == ASTE.PLUS);
    
    Assertions.assertEquals(2, ast.sizeFs());
    Assertions.assertEquals(true, ast.getF(0) == ASTF.PLUS);
    Assertions.assertEquals(true, ast.getF(1) == ASTF.MINUS);
  
     Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
