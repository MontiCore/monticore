package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcgenerictypes._ast.ASTMCComplexType;
import de.monticore.types.mcgenerictypes._ast.ASTMCWildcardType;
import de.monticore.types.mcgenerictypestest._parser.MCGenericTypesTestParser;
import de.monticore.types.types._ast.ASTArrayType;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
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
    String[] types = new String[]{"Foo<String>.Bar<List<Integer>>",
            "List<? extends Person>","List<P<String>>","Optional<String>",
            "Set<String>","Map<String,String>","List<socnet.Person>"
    };

    for (String testType : types) {
      MCGenericTypesTestParser mcBasicTypesParser = new MCGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_String(testType);
      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCObjectType);
      System.out.println(type.get().getClass());
      ASTMCObjectType t = (ASTMCObjectType) type.get();
    }
  }

  @Test
  public void testArrayTypes() throws IOException {
    Class foo = boolean.class;
    String[] types = new String[]{"String[][]","java.util.List<Foo>[][]",
            "boolean[][]"
    };

    for (String testType : types) {
      MCGenericTypesTestParser mcBasicTypesParser = new MCGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_String(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCArrayType);
      ASTMCArrayType t = (ASTMCArrayType) type.get();
      assertEquals(2,t.getDimensions());
    }
  }

  @Test
  public void testMCComplexReferenceTypeValid() throws IOException {
    MCGenericTypesTestParser parser = new MCGenericTypesTestParser();
    Optional<ASTMCType> type = parser.parse_StringMCType(
            "java.util.List<A>.Set<C>.some.Collection<B>"
    );
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCComplexType);
  }

  @Test
  public void testMcWildcardType() throws IOException {
    MCGenericTypesTestParser parser = new MCGenericTypesTestParser();
    Optional<ASTMCTypeArgument> type =
            parser.parse_StringMCTypeArgument(
                    "? extends java.util.Set<Foo>"
            );
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCWildcardType);
  }

  @Test
  public void testOldComplexArrayTypes() {

    MCGenericTypesTestParser parser = new MCGenericTypesTestParser();
    try {
      // test-data
      HashMap<String, Integer> testdata = new HashMap<String, Integer>();
      testdata.put("Collection<?>[]", 1);
      testdata.put("L<A>[]", 1);
      testdata.put("C<L<A>>[]", 1);
      testdata.put("Pair<String,String>[]", 1);
      testdata.put("A<B<C,D<E,F<G>>>>[]", 1);
      testdata.put("A<B<C,D<E,F<G<H>>>>,I<J>>[]", 1);

      // checks
      for (String teststring : testdata.keySet()) {
        Optional<ASTMCType> type = parser.parse_StringMCType(teststring);
        assertTrue(type.isPresent());
        // check typing and dimension:
        assertTrue(type.get() instanceof ASTMCArrayType);
        ASTMCArrayType arrayType = (ASTMCArrayType) type.get();
        assertEquals(testdata.get(teststring).intValue(), arrayType.getDimensions());
        assertTrue(arrayType.getMCType() instanceof ASTMCObjectType);
      }
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }


}
