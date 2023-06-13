/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypestest.MCBasicTypesTestMill;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCBasicTypesTest {
  
  @Before
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
    assertTrue(boolOpt.isPresent());

    ASTMCPrimitiveType bool = boolOpt.get();

    boolean isBool = bool.isBoolean();
    assertTrue(isBool);
    boolean isByte = bool.isByte();
    assertFalse(isByte);
    boolean isChar = bool.isChar();
    assertFalse(isChar);
    boolean isDouble = bool.isDouble();
    assertFalse(isDouble);
    boolean isFloat = bool.isFloat();
    assertFalse(isFloat);
    boolean isInt = bool.isInt();
    assertFalse(isInt);
    boolean isShort = bool.isShort();
    assertFalse(isShort);

    assertEquals(bool.toString(), "boolean");
  
    assertTrue(Log.getFindings().isEmpty());
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

        assertNotNull(type);
        assertTrue(type.isPresent());
        assertTrue(type.get() instanceof ASTMCPrimitiveType);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCQualifiedType() throws IOException {
    String[] types = new String[]{"socnet.Person", "Person"};
    for (String type : types) {
      MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
      Optional<ASTMCType> astType = mcBasicTypesParser.parse_String(type);
      assertNotNull(astType);
      assertTrue(astType.isPresent());
      assertTrue(astType.get() instanceof ASTMCQualifiedType);
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCQualifiedName() throws IOException {
    String[] types = new String[]{"socnet.Person", "Person"};
    for (String type : types) {
      MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
      Optional<ASTMCQualifiedName> astType = mcBasicTypesParser.parse_StringMCQualifiedName(type);
      assertNotNull(astType);
      assertTrue(astType.isPresent());
      //test toString
      assertEquals(astType.get().toString(), type);
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testMCImportStatement() throws IOException {
    String type = "import socnet.Person.*;";
    MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
    Optional<ASTMCImportStatement> astType = mcBasicTypesParser.parse_StringMCImportStatement(type);
    assertNotNull(astType);
    assertTrue(astType.isPresent());
    //test getQName method
    assertEquals(astType.get().getQName(), "socnet.Person");
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
