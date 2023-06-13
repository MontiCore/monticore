/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionType;
import de.monticore.types.mcfunctiontypestest.MCFunctionTypesTestMill;
import de.monticore.types.mcfunctiontypestest._parser.MCFunctionTypesTestParser;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesFullPrettyPrinter;
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
    ASTMCFunctionType type = parse("() -> void");
    assertEquals("void",
        type.getMCReturnType()
            .printType());
    assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(0, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  @Test
  public void testSupplierFunctionType() throws IOException {
    ASTMCFunctionType type = parse("() -> int");
    assertEquals("int",
        type.getMCReturnType()
            .printType());
    assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(0, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  @Test
  public void testWithInputFunctionType() throws IOException {
    ASTMCFunctionType type = parse("(int, long) -> void");
    assertEquals("void",
        type.getMCReturnType()
            .printType());
    assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(2, type.getMCFunctionParTypes().getMCTypeList().size());
    assertEquals("int",
        type.getMCFunctionParTypes().getMCType(0)
            .printType());
    assertEquals("long",
        type.getMCFunctionParTypes().getMCType(1)
            .printType());
  }

  @Test
  public void testEllipticFunctionType1() throws IOException {
    ASTMCFunctionType type = parse("(long...) -> void");
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
    ASTMCFunctionType type = parse("(int, long...) -> long");
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
    ASTMCFunctionType type = parse("() -> () -> void");
    assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(0, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  @Test
  public void testHigherOrderFunctionType2() throws IOException {
    ASTMCFunctionType type = parse("((long) -> void) -> (int) -> long");
    assertFalse(type.getMCFunctionParTypes().isPresentIsElliptic());
    assertEquals(1, type.getMCFunctionParTypes().getMCTypeList().size());
  }

  protected ASTMCFunctionType parse(String mcTypeStr) throws IOException {
    MCFunctionTypesTestParser parser = new MCFunctionTypesTestParser();
    Optional<ASTMCType> typeOpt = parser.parse_StringMCType(mcTypeStr);
    assertNotNull(typeOpt);
    assertTrue(typeOpt.isPresent());
    assertTrue(typeOpt.get() instanceof ASTMCFunctionType);
    ASTMCFunctionType type = (ASTMCFunctionType) typeOpt.get();
    assertEquals(0, Log.getFindingsCount());
    return type;
  }

}
