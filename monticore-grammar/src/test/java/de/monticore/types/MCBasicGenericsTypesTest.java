package de.monticore.types;


import de.monticore.types.mcbasicgenericstypestest._parser.MCBasicGenericsTypesTestParser;
import de.monticore.types.mcbasictypes._ast.ASTReferenceType;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MCBasicGenericsTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testBasicGenericsTypes() {
    Class foo = boolean.class;
    String[] types = new String[]{"List<String>","Optional<String>"};
    try {
      for (String testType : types) {
        MCBasicGenericsTypesTestParser mcBasicTypesParser = new MCBasicGenericsTypesTestParser();
        // .parseType(primitive);

        Optional<ASTReferenceType> type = mcBasicTypesParser.parse_StringReferenceType(testType);

        assertNotNull(type);
        assertTrue(type.isPresent());

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
