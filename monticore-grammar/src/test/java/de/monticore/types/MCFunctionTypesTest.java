/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionType;
import de.monticore.types.mcfunctiontypes._ast.ASTMCUnaryFunctionType;
import de.monticore.types.mcfunctiontypestest.MCFunctionTypesTestMill;
import de.monticore.types.mcfunctiontypestest._parser.MCFunctionTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MCFunctionTypesTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCFunctionTypesTestMill.reset();
    MCFunctionTypesTestMill.init();
  }

  @Test
  public void testRunnableFunctionType() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("() -> void");
    Assertions.assertEquals("void", type.getMCReturnType()
        .printType());
    Assertions.assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    Assertions.assertEquals(0, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  @Test
  public void testSupplierFunctionType() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("() -> int");
    Assertions.assertEquals("int", type.getMCReturnType()
        .printType());
    Assertions.assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    Assertions.assertEquals(0, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  @Test
  public void testWithInputFunctionType1() throws IOException {
    ASTMCUnaryFunctionType type =
        parseMCFunctionTypeNoParentheses("int -> void");
    Assertions.assertEquals("void", type.getMCReturnType().printType());
    Assertions.assertEquals("int", type.getMCType().printType());
  }

  @Test
  public void testWithInputFunctionType2() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("(int, long) -> void");
    Assertions.assertEquals("void", type.getMCReturnType().printType());
    Assertions.assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    Assertions.assertEquals(2, type.getMCFunctionParTypes().getMCTypeList().size());
    Assertions.assertEquals("int", type.getMCFunctionParTypes().getMCType(0).printType());
    Assertions.assertEquals("long", type.getMCFunctionParTypes().getMCType(1).printType());
  }

  @Test
  public void testEllipticFunctionType1() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("(long...) -> void");
    Assertions.assertEquals("void", type.getMCReturnType()
        .printType());
    Assertions.assertTrue(type.getMCFunctionParTypes().isPresentIsElliptic());
    Assertions.assertEquals(1, type.getMCFunctionParTypes().getMCTypeList().size());
    Assertions.assertEquals("long", type.getMCFunctionParTypes().getMCType(0)
        .printType());
  }

  @Test
  public void testEllipticFunctionType2() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("(int, long...) -> long");
    Assertions.assertEquals("long", type.getMCReturnType()
        .printType());
    Assertions.assertTrue(type.getMCFunctionParTypes().isPresentIsElliptic());
    Assertions.assertEquals(2, type.getMCFunctionParTypes().getMCTypeList().size());
    Assertions.assertEquals("int", type.getMCFunctionParTypes().getMCType(0)
        .printType());
    Assertions.assertEquals("long", type.getMCFunctionParTypes().getMCType(1)
        .printType());
  }

  @Test
  public void testHigherOrderFunctionType1() throws IOException {
    ASTMCFunctionType type = parseMCFunctionType("() -> () -> void");
    Assertions.assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    Assertions.assertEquals(0, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  @Test
  public void testHigherOrderFunctionType2() throws IOException {
    ASTMCFunctionType type =
        parseMCFunctionType("((long) -> void) -> (int) -> long");
    Assertions.assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    Assertions.assertEquals(1, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  @Test
  public void testHigherOrderFunctionType3() throws IOException {
    ASTMCUnaryFunctionType type =
        parseMCFunctionTypeNoParentheses("int -> long -> void");
    Assertions.assertEquals("int", type.getMCType().printType());
    Assertions.assertTrue(type.getMCReturnType().isPresentMCType());
    ASTMCType returnType = type.getMCReturnType().getMCType();
    Assertions.assertTrue(returnType instanceof ASTMCUnaryFunctionType);
    ASTMCUnaryFunctionType returnFuncType =
        (ASTMCUnaryFunctionType) returnType;
    Assertions.assertEquals("long", returnFuncType.getMCType().printType());
    Assertions.assertEquals("void", returnFuncType.getMCReturnType().printType());
  }

  @Test
  public void testHigherOrderFunctionType5() throws IOException {
    ASTMCUnaryFunctionType type =
        parseMCFunctionTypeNoParentheses("int -> (long -> void) -> long");
    Assertions.assertEquals("int", type.getMCType().printType());
    Assertions.assertEquals("(long->void)->long", type.getMCReturnType().printType());
  }

  protected ASTMCFunctionType parseMCFunctionType(String mcTypeStr) throws IOException {
    MCFunctionTypesTestParser parser = new MCFunctionTypesTestParser();
    Optional<ASTMCType> typeOpt = parser.parse_StringMCType(mcTypeStr);
    Assertions.assertNotNull(typeOpt);
    Assertions.assertTrue(typeOpt.isPresent());
    Assertions.assertTrue(typeOpt.get() instanceof ASTMCFunctionType);
    ASTMCFunctionType type = (ASTMCFunctionType) typeOpt.get();
    Assertions.assertEquals(0, Log.getFindingsCount());
    return type;
  }

  protected ASTMCUnaryFunctionType parseMCFunctionTypeNoParentheses(
      String mcTypeStr
  ) throws IOException {
    MCFunctionTypesTestParser parser = new MCFunctionTypesTestParser();
    Optional<ASTMCType> typeOpt = parser.parse_StringMCType(mcTypeStr);
    Assertions.assertNotNull(typeOpt);
    Assertions.assertTrue(typeOpt.isPresent());
    Assertions.assertTrue(typeOpt.get() instanceof ASTMCUnaryFunctionType);
    ASTMCUnaryFunctionType type =
        (ASTMCUnaryFunctionType) typeOpt.get();
    Assertions.assertEquals(0, Log.getFindingsCount());
    return type;
  }

}
