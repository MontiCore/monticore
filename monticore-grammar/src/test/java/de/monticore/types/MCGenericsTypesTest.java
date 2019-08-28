/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
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
      MCFullGenericTypesTestParser mcBasicTypesParser = new MCFullGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_StringMCType(testType);
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
      MCFullGenericTypesTestParser mcBasicTypesParser = new MCFullGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_StringMCType(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCArrayType);
      ASTMCArrayType t = (ASTMCArrayType) type.get();
      assertEquals(2,t.getDimensions());
    }
  }

  @Test
  public void testMCComplexReferenceTypeValid() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCType> type = parser.parse_StringMCType("java.util.List<A>.Set<C>.some.Collection<B>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCMultipleGenericType);
  }

  @Test
  public void testMcWildcardTypeArgument() throws IOException {
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("? extends java.util.Set<Foo>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCWildcardTypeArgument);
  }

  @Test
  public void testOldComplexArrayTypes() {

    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
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
  
  
  @Test
  public void testOldComplexTypes() throws IOException {
    String[] typesToTest = new String[]{
            "Type"
            , "Type<Arg>"
            , "package.Type"
            , "packageName.OuterClass.Type<Arg>"
            , "java.util.Foo<A>.Set<C>.some.Collection"
            , "a.b.Type<Arg>.C"
            , "a.b.Type<Arg>.C.D"
            , "OwnClass"
            , "a.b.c"
            , "_$testABC_1._5"
            , "a.b<c>"
            , "Seq<Pair<T,S>>"
            , "Pair<T,S>"
            , "Seq<Pair<String,Number>>"
            , "A<B<C,D<E,F<G>>>>"
            , "A<B<C,D<E,F<G<H>>>>,I<J>>"
            , "Vector<String>"
            , "A.B<String>.C<Object>"
            , "A.B<int[][]>.C<int[]>"
            , "L<A[]>"
            , "C<L<A>[]>"
            , "a.b.c<arg>"
            , "a.b.c<arg>.d"
            // Wildcards:
            , "Collection<?>"
            , "List<? extends Number>"
            , "ReferenceQueue<? super T>"
            , "Pair<String,?>"
            , "B<? extends int[]>"
            , "Pair<T, ? super Object>"
    };

    for (String testType : typesToTest) {
      MCFullGenericTypesTestParser mcBasicTypesParser = new MCFullGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_StringMCType(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      //assertTrue(type.get() instanceof ASTMCMultipleGenericType);

    }


  }

}
