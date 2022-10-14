/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionType;
import de.monticore.types.mcfunctiontypestest._parser.MCFunctionTypesTestParser;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
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
  public void disableFailQuick() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testRunnableFunctionType() throws IOException {
    ASTMCFunctionType type = parse("() -> void");
    assertEquals("void",
        type.getMCReturnType()
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
    assertFalse(type.getMCFunctionParameters().isPresentIsElliptic());
    assertEquals(0, type.getMCFunctionParameters().getMCTypeList().size());
  }

  @Test
  public void testSupplierFunctionType() throws IOException {
    ASTMCFunctionType type = parse("() -> int");
    assertEquals("int",
        type.getMCReturnType()
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
    assertFalse(type.getMCFunctionParameters().isPresentIsElliptic());
    assertEquals(0, type.getMCFunctionParameters().getMCTypeList().size());
  }

  @Test
  public void testWithInputFunctionType() throws IOException {
    ASTMCFunctionType type = parse("(int, long) -> void");
    assertEquals("void",
        type.getMCReturnType()
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
    assertFalse(type.getMCFunctionParameters().isPresentIsElliptic());
    assertEquals(2, type.getMCFunctionParameters().getMCTypeList().size());
    assertEquals("int",
        type.getMCFunctionParameters().getMCType(0)
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
    assertEquals("long",
        type.getMCFunctionParameters().getMCType(1)
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
  }

  @Test
  public void testEllipticFunctionType1() throws IOException {
    ASTMCFunctionType type = parse("(long...) -> void");
    assertEquals("void",
        type.getMCReturnType()
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
    assertTrue(type.getMCFunctionParameters().isPresentIsElliptic());
    assertEquals(1, type.getMCFunctionParameters().getMCTypeList().size());
    assertEquals("long",
        type.getMCFunctionParameters().getMCType(0)
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
  }

  @Test
  public void testEllipticFunctionType2() throws IOException {
    ASTMCFunctionType type = parse("(int, long...) -> long");
    assertEquals("long",
        type.getMCReturnType()
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
    assertTrue(type.getMCFunctionParameters().isPresentIsElliptic());
    assertEquals(2, type.getMCFunctionParameters().getMCTypeList().size());
    assertEquals("int",
        type.getMCFunctionParameters().getMCType(0)
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
    assertEquals("long",
        type.getMCFunctionParameters().getMCType(1)
            .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
  }

  @Test
  public void testHigherOrderFunctionType1() throws IOException {
    ASTMCFunctionType type = parse("() -> () -> void");
    assertFalse(type.getMCFunctionParameters().isPresentIsElliptic());
    assertEquals(0, type.getMCFunctionParameters().getMCTypeList().size());
  }

  @Test
  public void testHigherOrderFunctionType2() throws IOException {
    ASTMCFunctionType type = parse("((long) -> void) -> (int) -> long");
    assertFalse(type.getMCFunctionParameters().isPresentIsElliptic());
    assertEquals(1, type.getMCFunctionParameters().getMCTypeList().size());
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
