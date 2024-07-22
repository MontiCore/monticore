/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypestest.MCBasicTypesTestMill;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MCBasicTypesTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCBasicTypesTestMill.reset();
    MCBasicTypesTestMill.init();
  }
  
  
  @Test
  public void testPrimitiveTypesAPI() throws IOException {
    MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
    Optional<ASTMCPrimitiveType> boolOpt = mcBasicTypesParser.parse_StringMCPrimitiveType("boolean");
    Assertions.assertTrue(boolOpt.isPresent());

    ASTMCPrimitiveType bool = boolOpt.get();

    boolean isBool = bool.isBoolean();
    Assertions.assertTrue(isBool);
    boolean isByte = bool.isByte();
    Assertions.assertFalse(isByte);
    boolean isChar = bool.isChar();
    Assertions.assertFalse(isChar);
    boolean isDouble = bool.isDouble();
    Assertions.assertFalse(isDouble);
    boolean isFloat = bool.isFloat();
    Assertions.assertFalse(isFloat);
    boolean isInt = bool.isInt();
    Assertions.assertFalse(isInt);
    boolean isShort = bool.isShort();
    Assertions.assertFalse(isShort);

    Assertions.assertEquals(bool.toString(), "boolean");
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testPrimitiveTypes() {

    Class foo = boolean.class;

    String[] primitives = new String[]{"boolean", "byte", "char", "short", "int", "long",
        "float", "double"};
    try {
      for (String primitive : primitives) {
        MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
        // .parseType(primitive);

        Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_String(primitive);

        Assertions.assertNotNull(type);
        Assertions.assertTrue(type.isPresent());
        Assertions.assertTrue(type.get() instanceof ASTMCPrimitiveType);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCQualifiedType() throws IOException {
    String[] types = new String[]{"socnet.Person", "Person"};
    for (String type : types) {
      MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
      Optional<ASTMCType> astType = mcBasicTypesParser.parse_String(type);
      Assertions.assertNotNull(astType);
      Assertions.assertTrue(astType.isPresent());
      Assertions.assertTrue(astType.get() instanceof ASTMCQualifiedType);
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCQualifiedName() throws IOException {
    String[] types = new String[]{"socnet.Person", "Person"};
    for (String type : types) {
      MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
      Optional<ASTMCQualifiedName> astType = mcBasicTypesParser.parse_StringMCQualifiedName(type);
      Assertions.assertNotNull(astType);
      Assertions.assertTrue(astType.isPresent());
      //test toString
      Assertions.assertEquals(astType.get().toString(), type);
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testMCImportStatement() throws IOException {
    String type = "import socnet.Person.*;";
    MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
    Optional<ASTMCImportStatement> astType = mcBasicTypesParser.parse_StringMCImportStatement(type);
    Assertions.assertNotNull(astType);
    Assertions.assertTrue(astType.isPresent());
    //test getQName method
    Assertions.assertEquals(astType.get().getQName(), "socnet.Person");
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
