/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcarraytypestest._parser.MCArrayTypesTestParser;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCArrayTypesPrettyPrinterTest {

  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    Log.getFindings().clear();
  }

  @Test
  public void testMCArrayType() throws IOException {
    //have to use ASTMCType because of left recursion in ASTMCArrayType there is no parse Method
    MCArrayTypesTestParser parser = new MCArrayTypesTestParser();
    Optional<ASTMCType> ast = parser.parse_StringMCType("String[][]");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTMCType type = ast.get();
    MCArrayTypesPrettyPrinter printer = new MCArrayTypesPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringMCType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(type.deepEquals(ast.get()));
  }
}
