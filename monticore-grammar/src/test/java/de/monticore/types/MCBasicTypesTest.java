package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedReferenceType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCBasicTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
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

  }

  @Test
  public void testMCQualifiedReferenceType() throws IOException {
    String[] types = new String[]{"socnet.Person", "Person"};
    for (String type : types) {
      MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
      Optional<ASTMCType> astType = mcBasicTypesParser.parse_String(type);
      assertNotNull(astType);
      assertTrue(astType.isPresent());
      assertTrue(astType.get() instanceof ASTMCQualifiedReferenceType);
    }
  }

}
