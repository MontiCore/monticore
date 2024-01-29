/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionType;
import de.monticore.types.mcfunctiontypes._ast.ASTMCUnaryFunctionType;
import de.monticore.types.mcfunctiontypestest.MCFunctionTypesTestMill;
import de.monticore.types.mcfunctiontypestest._parser.MCFunctionTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MCFunctionTypesTest {

  @Before
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCFunctionTypesTestMill.reset();
    MCFunctionTypesTestMill.init();
  }

  @Test
  public void testRunnableFunctionType() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("() -> void");
    assertEquals("void",
        type.getMCReturnType()
            .printType());
    assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(0, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  @Test
  public void testSupplierFunctionType() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("() -> int");
    assertEquals("int",
        type.getMCReturnType()
            .printType());
    assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(0, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  @Test
  public void testWithInputFunctionType1() throws IOException {
    ASTMCUnaryFunctionType type =
        parseMCFunctionTypeNoParentheses("int -> void");
    assertEquals("void", type.getMCReturnType().printType());
    assertEquals("int", type.getParameter().printType());
  }

  @Test
  public void testWithInputFunctionType2() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("(int, long) -> void");
    assertEquals("void", type.getMCReturnType().printType());
    assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(2, type.getMCFunctionParTypes().getMCTypeList().size());
    assertEquals("int",
        type.getMCFunctionParTypes().getMCType(0).printType()
    );
    assertEquals("long",
        type.getMCFunctionParTypes().getMCType(1).printType()
    );
  }

  @Test
  public void testEllipticFunctionType1() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("(long...) -> void");
    assertEquals("void",
        type.getMCReturnType()
            .printType());
    assertTrue(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(1, type.getMCFunctionParTypes().getMCTypeList().size());
    assertEquals("long",
        type.getMCFunctionParTypes().getMCType(0)
            .printType());
  }

  @Test
  public void testEllipticFunctionType2() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("(int, long...) -> long");
    assertEquals("long",
        type.getMCReturnType()
            .printType());
    assertTrue(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(2, type.getMCFunctionParTypes().getMCTypeList().size());
    assertEquals("int",
        type.getMCFunctionParTypes().getMCType(0)
            .printType());
    assertEquals("long",
        type.getMCFunctionParTypes().getMCType(1)
            .printType());
  }

  @Test
  public void testHigherOrderFunctionType1() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("() -> () -> void");
    assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(0, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  @Test
  public void testHigherOrderFunctionType2() throws IOException {
    ASTMCFunctionType type =
        parseMCFunctionType("((long) -> void) -> (int) -> long");
    assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(1, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  @Test
  public void testHigherOrderFunctionType3() throws IOException {
    ASTMCUnaryFunctionType type =
        parseMCFunctionTypeNoParentheses("int -> long -> void");
    assertEquals("int", type.getParameter().printType());
    assertTrue(type.getMCReturnType().isPresentMCType());
    ASTMCType returnType = type.getMCReturnType().getMCType();
    assertTrue(returnType instanceof ASTMCUnaryFunctionType);
    ASTMCUnaryFunctionType returnFuncType =
        (ASTMCUnaryFunctionType) returnType;
    assertEquals("long", returnFuncType.getParameter().printType());
    assertEquals("void", returnFuncType.getMCReturnType().printType());
  }

  @Test
  public void testHigherOrderFunctionType5() throws IOException {
    ASTMCUnaryFunctionType type =
        parseMCFunctionTypeNoParentheses("int -> (long -> void) -> long");
    assertEquals("int", type.getParameter().printType());
    assertEquals("(long->void)->long", type.getMCReturnType().printType());
  }

  protected ASTMCFunctionType parseMCFunctionType(String mcTypeStr) throws IOException {
    MCFunctionTypesTestParser parser = new MCFunctionTypesTestParser();
    Optional<ASTMCType> typeOpt = parser.parse_StringMCType(mcTypeStr);
    assertNotNull(typeOpt);
    assertTrue(typeOpt.isPresent());
    assertTrue(typeOpt.get() instanceof ASTMCFunctionType);
    ASTMCFunctionType type = (ASTMCFunctionType) typeOpt.get();
    assertEquals(0, Log.getFindingsCount());
    return type;
  }

  protected ASTMCUnaryFunctionType parseMCFunctionTypeNoParentheses(
      String mcTypeStr
  ) throws IOException {
    MCFunctionTypesTestParser parser = new MCFunctionTypesTestParser();
    Optional<ASTMCType> typeOpt = parser.parse_StringMCType(mcTypeStr);
    assertNotNull(typeOpt);
    assertTrue(typeOpt.isPresent());
    assertTrue(typeOpt.get() instanceof ASTMCUnaryFunctionType);
    ASTMCUnaryFunctionType type =
        (ASTMCUnaryFunctionType) typeOpt.get();
    assertEquals(0, Log.getFindingsCount());
    return type;
  }

}
