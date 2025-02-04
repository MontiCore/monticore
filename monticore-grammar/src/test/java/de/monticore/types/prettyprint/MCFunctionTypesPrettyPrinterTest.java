/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionType;
import de.monticore.types.mcfunctiontypes._prettyprint.MCFunctionTypesFullPrettyPrinter;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MCFunctionTypesPrettyPrinterTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCFunctionTypesTestMill.reset();
    MCFunctionTypesTestMill.init();
  }

  @Test
  public void testMCBasicTypeArgument() throws IOException {
    testPrintParseCompare("(int) -> int");
  }

  @Test
  public void testMCBasicTypeArgument2() throws IOException {
    testPrintParseCompare("(long, int) -> void");
  }

  @Test
  public void testSupplier() throws IOException {
    testPrintParseCompare("() -> int");
  }

  @Test
  public void testElliptic1() throws IOException {
    testPrintParseCompare("(int, int...) -> int");
  }

  @Test
  public void testElliptic2() throws IOException {
    testPrintParseCompare("(int...) -> void");
  }

  @Test
  public void testHigherOrderFunction1() throws IOException {
    testPrintParseCompare("((int) -> void) -> (int) -> long");
  }

  @Test
  public void testHigherOrderFunction2() throws IOException {
    testPrintParseCompare("(long, ((long) -> int) -> int) -> ((long) -> void) -> int");
  }

  protected ASTMCFunctionType parse(String mcTypeStr) throws IOException {
    MCFunctionTypesTestParser parser = new MCFunctionTypesTestParser();
    Optional<ASTMCFunctionType> typeOpt = parser.parse_StringMCFunctionType(mcTypeStr);
    Assertions.assertNotNull(typeOpt);
    Assertions.assertTrue(typeOpt.isPresent());
    Assertions.assertEquals(0, Log.getFindingsCount());
    return typeOpt.get();
  }

  protected String print(ASTMCFunctionType type) {
    MCFunctionTypesFullPrettyPrinter printer = new MCFunctionTypesFullPrettyPrinter(
        new IndentPrinter());
    String typeStr = printer.prettyprint(type);
    Assertions.assertEquals(0, Log.getFindingsCount());
    return typeStr;
  }

  protected void testPrintParseCompare(String typeStr) throws IOException {
    ASTMCFunctionType type = parse(typeStr);
    String printed = print(type);
    ASTMCFunctionType typeOfPrinted = parse(printed);
    Assertions.assertTrue(typeOfPrinted.deepEquals(type));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
