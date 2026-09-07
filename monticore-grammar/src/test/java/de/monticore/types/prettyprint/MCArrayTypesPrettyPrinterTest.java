/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypes._prettyprint.MCArrayTypesFullPrettyPrinter;
import de.monticore.types.mcarraytypestest.MCArrayTypesTestMill;
import de.monticore.types.mcarraytypestest._parser.MCArrayTypesTestParser;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MCArrayTypesPrettyPrinterTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCArrayTypesTestMill.reset();
    MCArrayTypesTestMill.init();
  }

  @Test
  public void testMCArrayType() throws IOException {
    //have to use ASTMCType because of left recursion in ASTMCArrayType there is no parse Method
    MCArrayTypesTestParser parser = new MCArrayTypesTestParser();
    Optional<ASTMCType> ast = parser.parse_StringMCType("String[][]");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    assertInstanceOf(ASTMCArrayType.class, ast.get());
    ASTMCArrayType type = (ASTMCArrayType) ast.get();
    MCArrayTypesFullPrettyPrinter printer = new MCArrayTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(type);
    ast = parser.parse_StringMCType(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(type.deepEquals(ast.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
