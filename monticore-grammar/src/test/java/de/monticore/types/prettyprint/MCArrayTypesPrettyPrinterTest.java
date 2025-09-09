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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.get() instanceof ASTMCArrayType);
    ASTMCArrayType type = (ASTMCArrayType) ast.get();
    MCArrayTypesFullPrettyPrinter printer = new MCArrayTypesFullPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(type);
    ast = parser.parse_StringMCType(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(type.deepEquals(ast.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
