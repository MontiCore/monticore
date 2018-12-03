package de.monticore.types;


import de.monticore.types.mcbasictypes._ast.ASTMCReferenceType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccustomgenerictypestest._parser.MCCustomGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MCCustomGenericsTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testCustomGenericsTypes() throws IOException {




    String[] types = new String[]{"List<List<b.B>>","socnet.Person<socnet.Person<B>, SecondaryParam>"};

    for (String testType : types) {
      System.out.println("Teste "+testType);
      MCCustomGenericTypesTestParser mcBasicTypesParser = new MCCustomGenericTypesTestParser();

      Optional<ASTMCType> type = mcBasicTypesParser.parse_String(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCReferenceType);
      System.out.println(type.get().getClass());

      ASTMCReferenceType t = (ASTMCReferenceType) type.get();
      
    }
  }
}
