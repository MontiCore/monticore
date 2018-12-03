package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTMCReferenceType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcgenerictypestest._parser.MCGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCGenericsTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testBasicGenericsTypes() throws IOException {
    Class foo = boolean.class;
    String[] types = new String[]{"Foo<String>.Bar<List<Integer>>","List<? extends Person>","List<P<String>>","Optional<String>","Set<String>","Map<String,String>","List<socnet.Person>"};

    for (String testType : types) {
      MCGenericTypesTestParser mcBasicTypesParser = new MCGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_String(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCReferenceType);
      System.out.println(type.get().getClass());
      ASTMCReferenceType t = (ASTMCReferenceType) type.get();
    }
  }

  @Test
  public void testArrayTypes() throws IOException {
    Class foo = boolean.class;
    String[] types = new String[]{"String[][]"};

    for (String testType : types) {
      MCGenericTypesTestParser mcBasicTypesParser = new MCGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_String(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCArrayType);
      System.out.println(type.get().getClass());
      ASTMCArrayType t = (ASTMCArrayType) type.get();
      assertEquals(2,t.getDimensions());
    }
  }

}
