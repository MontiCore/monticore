package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTPrimitiveType2;
import de.monticore.types.mcbasictypes._ast.ASTType;
import de.monticore.types.mcbasictypes._parser.MCBasicTypesParser;
import de.monticore.types.types._ast.ASTPrimitiveType;

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

    String[] primitives = new String[]{"boolean", "byte", "char", "short", "int", "long",
            "float", "double"};
    try {
      for (String primitive : primitives) {
        MCBasicTypesParser mcBasicTypesParser = new MCBasicTypesParser();
        // .parseType(primitive);

        Optional<? extends ASTType> type = mcBasicTypesParser.parse_StringPrimitiveType2(primitive);

        assertNotNull(type);
        assertTrue(type.isPresent());
        assertTrue(type.get() instanceof ASTPrimitiveType2);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
