package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTBuiltInType;
import de.monticore.types.mcbasictypes._ast.ASTPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTType;

import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MCBasicTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
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

        Optional<? extends ASTType> type = mcBasicTypesParser.parse_StringPrimitiveType(primitive);

        assertNotNull(type);
        assertTrue(type.isPresent());
        assertTrue(type.get() instanceof ASTPrimitiveType);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void testOwnSimpleTypes() {

    Class foo = boolean.class;

    String[] primitives = new String[]{"socnet.Person", "Person"};
    try {
      for (String primitive : primitives) {
        MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();

        Optional<ASTType> type = mcBasicTypesParser.parse_StringType(primitive);

        assertNotNull(type);
        assertTrue(type.isPresent());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void testBuiltInTypes() {

    Class foo = boolean.class;

    String[] primitives = new String[]{"Boolean","String"};
    try {
      for (String primitive : primitives) {
        MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();

        Optional<ASTBuiltInType> type = mcBasicTypesParser.parse_StringBuiltInType(primitive);

        assertNotNull(type);
        assertTrue(type.isPresent());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


}
