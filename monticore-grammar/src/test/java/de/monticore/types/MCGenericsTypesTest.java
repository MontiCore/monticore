/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.generictypestest.GenericTypesTestMill;
import de.monticore.types.generictypestest._parser.GenericTypesTestParser;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCMultipleGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypestest.MCFullGenericTypesTestMill;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class MCGenericsTypesTest {
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  
  @Test
  public void testBasicGenericsTypes() throws IOException {
    MCFullGenericTypesTestMill.reset();
    MCFullGenericTypesTestMill.init();
    Class foo = boolean.class;
    String[] types = new String[]{"Foo<String>.Bar<List<Integer>>",
            "List<? extends Person>","List<P<String>>","Optional<String>",
            "Set<String>","Map<String,String>","List<socnet.Person>"
    };

    for (String testType : types) {
      MCFullGenericTypesTestParser mcBasicTypesParser = new MCFullGenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_StringMCType(testType);
      Assertions.assertNotNull(type);
      Assertions.assertTrue(type.isPresent());
      Assertions.assertTrue(type.get() instanceof ASTMCObjectType);
      System.out.println(type.get().getClass());
      ASTMCObjectType t = (ASTMCObjectType) type.get();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testArrayTypes() throws IOException {
    GenericTypesTestMill.reset();
    GenericTypesTestMill.init();
    Class foo = boolean.class;
    String[] types = new String[]{"String[][]","java.util.List<Foo>[][]",
            "boolean[][]"
    };

    for (String testType : types) {
      GenericTypesTestParser genericTypesTestParser = new GenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = genericTypesTestParser.parse_StringMCType(testType);

      Assertions.assertNotNull(type);
      Assertions.assertTrue(type.isPresent());
      Assertions.assertTrue(type.get() instanceof ASTMCArrayType);
      ASTMCArrayType t = (ASTMCArrayType) type.get();
      Assertions.assertEquals(2, t.getDimensions());
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCComplexReferenceTypeValid() throws IOException {
    MCFullGenericTypesTestMill.reset();
    MCFullGenericTypesTestMill.init();
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCType> type = parser.parse_StringMCType("java.util.List<A>.Set<C>.some.Collection<B>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCMultipleGenericType);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMcWildcardTypeArgument() throws IOException {
    MCFullGenericTypesTestMill.reset();
    MCFullGenericTypesTestMill.init();
    MCFullGenericTypesTestParser parser = new MCFullGenericTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("? extends java.util.Set<Foo>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCWildcardTypeArgument);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOldComplexArrayTypes() {
    GenericTypesTestMill.reset();
    GenericTypesTestMill.init();
    GenericTypesTestParser parser = new GenericTypesTestParser();
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
        Assertions.assertTrue(type.isPresent());
        // check typing and dimension:
        Assertions.assertTrue(type.get() instanceof ASTMCArrayType);
        ASTMCArrayType arrayType = (ASTMCArrayType) type.get();
        Assertions.assertEquals(testdata.get(teststring).intValue(), arrayType.getDimensions());
        Assertions.assertTrue(arrayType.getMCType() instanceof ASTMCObjectType);
      }
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  
  @Test
  public void testOldComplexTypes() throws IOException {
    GenericTypesTestMill.reset();
    GenericTypesTestMill.init();
    String[] typesToTest = new String[]{
            "Type"
            , "Type<Arg>"
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
      GenericTypesTestParser genericTypesTestParser = new GenericTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = genericTypesTestParser.parse_StringMCType(testType);

      Assertions.assertNotNull(type);
      Assertions.assertTrue(type.isPresent());
      //assertTrue(type.get() instanceof ASTMCMultipleGenericType);

    }
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
