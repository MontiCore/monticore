/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCType2SymTypeExpressionTest {


  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  List<String> primitiveTypes = Arrays
      .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");


  private SymTypeExpression mcType2TypeExpression(ASTMCBasicTypesNode type) {
    SynthesizeSymTypeFromMCSimpleGenericTypes visitor = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    type.accept(visitor);
    return visitor.getResult().get();
  }

  @Test
  public void testBasicGeneric() throws IOException {
    Optional<ASTMCType> type = new MCFullGenericTypesTestParser().parse_StringMCType("de.util.Pair<de.mc.PairA,de.mc.PairB>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("de.util.Pair", ((SymTypeOfGenerics) listSymTypeExpression).getTypeConstructorFullName());
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PairA", keyTypeArgument.print());

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PairB", valueTypeArgument.print());
  }

  @Test
  public void testBasicGenericRekursiv() throws IOException {
    Optional<ASTMCType> type = new MCFullGenericTypesTestParser().parse_StringMCType("de.util.Pair<de.mc.PairA,de.util.Pair2<de.mc.PairB,de.mc.PairC>>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("de.util.Pair", ((SymTypeOfGenerics) listSymTypeExpression).getTypeConstructorFullName());
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PairA", keyTypeArgument.print());

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfGenerics);
    assertEquals("de.util.Pair2", ((SymTypeOfGenerics) valueTypeArgument).getTypeConstructorFullName());

    SymTypeOfGenerics valueTypeArg = (SymTypeOfGenerics) valueTypeArgument;

    SymTypeExpression argument1 = valueTypeArg.getArgumentList().get(0);
    assertEquals("de.mc.PairB", argument1.print());

    SymTypeExpression argument2 = valueTypeArg.getArgumentList().get(1);
    assertEquals("de.mc.PairC", argument2.print());


  }

  @Test
  public void testMap() throws IOException {
    Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<de.mc.PersonKey,de.mc.PersonValue>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("Map<de.mc.PersonKey,de.mc.PersonValue>", listSymTypeExpression.print());
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PersonKey", keyTypeArgument.print());

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PersonValue", valueTypeArgument.print());
  }

  @Test
  public void testMapUnqualified() throws IOException {
    Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<PersonKey,PersonValue>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("Map<PersonKey,PersonValue>", listSymTypeExpression.print());
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertEquals("PersonKey", keyTypeArgument.print());

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfObject);
    assertEquals("PersonValue", valueTypeArgument.print());
  }

  @Test
  public void testMapPrimitives() throws IOException {
    for (String primitiveKey : primitiveTypes) {
      for (String primitiveValue : primitiveTypes) {
        Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<" + primitiveKey + "," + primitiveValue + ">");
        assertTrue(type.isPresent());
        SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
        assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
        assertEquals(("Map<" + primitiveKey + "," + primitiveValue + ">"), listSymTypeExpression.print());

        SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
        assertTrue(keyTypeArgument instanceof SymTypeConstant);
        assertEquals(primitiveKey, keyTypeArgument.print());

        SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(1);
        assertTrue(valueTypeArgument instanceof SymTypeConstant);
        assertEquals(primitiveValue, valueTypeArgument.print());
      }
    }

  }

  @Test
  public void testOptional() throws IOException {
    Optional<ASTMCOptionalType> type = new MCCollectionTypesTestParser().parse_StringMCOptionalType("Optional<de.mc.Person>");
    assertTrue(type.isPresent());
    SymTypeExpression optSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(optSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("Optional", ((SymTypeOfGenerics) optSymTypeExpression).getTypeConstructorFullName());
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) optSymTypeExpression).getArgumentList().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.Person", listTypeArgument.print());
  }

  @Test
  public void testOptionalUnqualified() throws IOException {
    Optional<ASTMCOptionalType> type = new MCCollectionTypesTestParser().parse_StringMCOptionalType("Optional<Person>");
    assertTrue(type.isPresent());
    SymTypeExpression optSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(optSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("Optional", ((SymTypeOfGenerics) optSymTypeExpression).getTypeConstructorFullName());
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) optSymTypeExpression).getArgumentList().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertEquals("Person", listTypeArgument.print());
  }

  @Test
  public void testOptionalPrimitive() throws IOException {
    for (String primitive : primitiveTypes) {
      Optional<ASTMCOptionalType> type = new MCCollectionTypesTestParser().parse_StringMCOptionalType("Optional<" + primitive + ">");
      assertTrue(type.isPresent());
      SymTypeExpression optSymTypeExpression = mcType2TypeExpression(type.get());
      assertTrue(optSymTypeExpression instanceof SymTypeOfGenerics);
      assertEquals("Optional", ((SymTypeOfGenerics) optSymTypeExpression).getTypeConstructorFullName());
      SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) optSymTypeExpression).getArgumentList().get(0);
      assertTrue(listTypeArgument instanceof SymTypeConstant);
      assertEquals(primitive, listTypeArgument.print());
    }
  }


  @Test
  public void testSet() throws IOException {
    Optional<ASTMCSetType> type = new MCCollectionTypesTestParser().parse_StringMCSetType("Set<de.mc.Person>");
    assertTrue(type.isPresent());
    SymTypeExpression setSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(setSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("Set", ((SymTypeOfGenerics) setSymTypeExpression).getTypeConstructorFullName());
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) setSymTypeExpression).getArgumentList().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.Person", listTypeArgument.print());
  }

  @Test
  public void testSetUnqualified() throws IOException {
    Optional<ASTMCSetType> type = new MCCollectionTypesTestParser().parse_StringMCSetType("Set<Person>");
    assertTrue(type.isPresent());
    SymTypeExpression setSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(setSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("Set", ((SymTypeOfGenerics) setSymTypeExpression).getTypeConstructorFullName());
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) setSymTypeExpression).getArgumentList().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertEquals("Person", listTypeArgument.print());
  }

  @Test
  public void testSetPrimitives() throws IOException {
    for (String primitive : primitiveTypes) {
      Optional<ASTMCSetType> type = new MCCollectionTypesTestParser().parse_StringMCSetType("Set<" + primitive + ">");
      assertTrue(type.isPresent());
      SymTypeExpression setSymTypeExpression =
              mcType2TypeExpression(type.get());
      assertTrue(setSymTypeExpression instanceof SymTypeOfGenerics);
      assertEquals("Set", ((SymTypeOfGenerics) setSymTypeExpression).getTypeConstructorFullName());
      SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) setSymTypeExpression).getArgumentList().get(0);
      assertTrue(listTypeArgument instanceof SymTypeConstant);
      assertEquals(primitive, listTypeArgument.print());
    }
  }

  @Test
  public void testList() throws IOException {
    Optional<ASTMCListType> type = new MCCollectionTypesTestParser().parse_StringMCListType("List<de.mc.Person>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("List", ((SymTypeOfGenerics) listSymTypeExpression).getTypeConstructorFullName());
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.Person", listTypeArgument.print());
  }

  @Test
  public void testListUnqualified() throws IOException {
    Optional<ASTMCListType> type = new MCCollectionTypesTestParser().parse_StringMCListType("List<Person>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("List", ((SymTypeOfGenerics) listSymTypeExpression).getTypeConstructorFullName());
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertEquals("Person", listTypeArgument.print());
  }

  @Test
  public void testListPrimitive() throws IOException {
    for (String primitive : primitiveTypes) {
      Optional<ASTMCListType> type = new MCCollectionTypesTestParser().parse_StringMCListType("List<" + primitive + ">");
      assertTrue(type.isPresent());
      SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
      assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
      assertEquals("List", ((SymTypeOfGenerics) listSymTypeExpression).getTypeConstructorFullName());
      SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
      assertTrue(listTypeArgument instanceof SymTypeConstant);
      assertEquals(primitive, listTypeArgument.print());
    }
  }


  @Test
  public void testPrimitives() throws IOException {
    for (String primitive : primitiveTypes) {
      Optional<ASTMCPrimitiveType> type = new MCCollectionTypesTestParser().parse_StringMCPrimitiveType(primitive);
      assertTrue(type.isPresent());
      ASTMCPrimitiveType booleanType = type.get();
      SymTypeExpression symTypeExpression = mcType2TypeExpression(booleanType);
      assertTrue(symTypeExpression instanceof SymTypeConstant);
      assertEquals(primitive, symTypeExpression.print());
    }
  }

  @Test
  public void testVoid() throws IOException {
    Optional<ASTMCVoidType> type = new MCCollectionTypesTestParser().parse_StringMCVoidType("void");
    assertTrue(type.isPresent());
    ASTMCVoidType booleanType = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(booleanType);
    assertTrue(symTypeExpression instanceof SymTypeVoid);
    assertEquals("void", symTypeExpression.print());
  }


  @Test
  public void testQualifiedType() throws IOException {
    Optional<ASTMCQualifiedType> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedType("de.mc.Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedType qualifiedType = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedType);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("de.mc.Person", symTypeExpression.print());
  }

  @Test
  public void testQualifiedTypeUnqualified() throws IOException {
    Optional<ASTMCQualifiedType> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedType("Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedType qualifiedType = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedType);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("Person", symTypeExpression.print());
  }

  @Test
  public void testQualifiedName() throws IOException {
    Optional<ASTMCQualifiedName> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedName("de.mc.Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedName qualifiedName = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedName);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("de.mc.Person", symTypeExpression.print());
  }

  @Test
  public void testQualifiedNameUnqualified() throws IOException {
    Optional<ASTMCQualifiedName> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedName("Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedName qualifiedName = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedName);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("Person", symTypeExpression.print());
  }

}
