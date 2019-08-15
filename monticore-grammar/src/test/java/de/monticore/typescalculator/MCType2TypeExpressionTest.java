/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCType2TypeExpressionTest {

  List<String> primitiveTypes = Arrays
      .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");



  @Test
  public void testBasicGeneric() throws IOException {
    Optional<ASTMCType> type = new MCFullGenericTypesTestParser().parse_StringMCType("de.util.Pair<de.mc.PairA,de.mc.PairB>");
    assertTrue(type.isPresent());
    TypeExpression listTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
    assertTrue(listTypeExpression instanceof GenericTypeExpression);
    assertTrue("de.util.Pair".equals(listTypeExpression.getName()));
    TypeExpression keyTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(0);
    assertTrue(keyTypeArgument instanceof ObjectType);
    assertTrue("de.mc.PairA".equals(keyTypeArgument.getName()));

    TypeExpression valueTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(1);
    assertTrue(valueTypeArgument instanceof ObjectType);
    assertTrue("de.mc.PairB".equals(valueTypeArgument.getName()));
  }

  @Test
  public void testBasicGenericRekursiv() throws IOException {
    Optional<ASTMCType> type = new MCFullGenericTypesTestParser().parse_StringMCType("de.util.Pair<de.mc.PairA,de.util.Pair2<de.mc.PairB,de.mc.PairC>>");
    assertTrue(type.isPresent());
    TypeExpression listTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
    assertTrue(listTypeExpression instanceof GenericTypeExpression);
    assertTrue("de.util.Pair".equals(listTypeExpression.getName()));
    TypeExpression keyTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(0);
    assertTrue(keyTypeArgument instanceof ObjectType);
    assertTrue("de.mc.PairA".equals(keyTypeArgument.getName()));

    TypeExpression valueTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(1);
    assertTrue(valueTypeArgument instanceof GenericTypeExpression);
    assertEquals("de.util.Pair2",valueTypeArgument.getName());

    GenericTypeExpression valueTypeArg = (GenericTypeExpression) valueTypeArgument;

    TypeExpression argument1 = valueTypeArg.getArguments().get(0);
    assertTrue(keyTypeArgument instanceof ObjectType);
    assertEquals("de.mc.PairB",argument1.getName());

    TypeExpression argument2 = valueTypeArg.getArguments().get(1);
    assertTrue(keyTypeArgument instanceof ObjectType);
    assertEquals("de.mc.PairC",argument2.getName());


  }

  @Test
  public void testMap() throws IOException {
    Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<de.mc.PersonKey,de.mc.PersonValue>");
    assertTrue(type.isPresent());
    TypeExpression listTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
    assertTrue(listTypeExpression instanceof GenericTypeExpression);
    assertTrue("Map".equals(listTypeExpression.getName()));
    TypeExpression keyTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(0);
    assertTrue(keyTypeArgument instanceof ObjectType);
    assertTrue("de.mc.PersonKey".equals(keyTypeArgument.getName()));

    TypeExpression valueTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(1);
    assertTrue(valueTypeArgument instanceof ObjectType);
    assertTrue("de.mc.PersonValue".equals(valueTypeArgument.getName()));

  }

  @Test
  public void testMapUnqualified() throws IOException {
    Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<PersonKey,PersonValue>");
    assertTrue(type.isPresent());
    TypeExpression listTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
    assertTrue(listTypeExpression instanceof GenericTypeExpression);
    assertTrue("Map".equals(listTypeExpression.getName()));
    TypeExpression keyTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(0);
    assertTrue(keyTypeArgument instanceof ObjectType);
    assertTrue("PersonKey".equals(keyTypeArgument.getName()));

    TypeExpression valueTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(1);
    assertTrue(valueTypeArgument instanceof ObjectType);
    assertTrue("PersonValue".equals(valueTypeArgument.getName()));
  }

  @Test
  public void testMapPrimitives() throws IOException {
    for(String primitiveKey : primitiveTypes) {
      for(String primitiveValue : primitiveTypes) {
        Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<"+primitiveKey+","+primitiveValue+">");
        assertTrue(type.isPresent());
        TypeExpression listTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
        assertTrue(listTypeExpression instanceof GenericTypeExpression);
        assertTrue("Map".equals(listTypeExpression.getName()));

        TypeExpression keyTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(0);
        assertTrue(keyTypeArgument instanceof TypeConstant);
        assertTrue(primitiveKey.equals(keyTypeArgument.getName()));

        TypeExpression valueTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(1);
        assertTrue(valueTypeArgument instanceof TypeConstant);
        assertTrue(primitiveValue.equals(valueTypeArgument.getName()));
      }
    }

  }

  @Test
  public void testOptional() throws IOException {
    Optional<ASTMCOptionalType> type = new MCCollectionTypesTestParser().parse_StringMCOptionalType("Optional<de.mc.Person>");
    assertTrue(type.isPresent());
    TypeExpression listTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
    assertTrue(listTypeExpression instanceof GenericTypeExpression);
    assertTrue("Optional".equals(listTypeExpression.getName()));
    TypeExpression listTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof ObjectType);
    assertTrue("de.mc.Person".equals(listTypeArgument.getName()));
  }

  @Test
  public void testOptionalUnqualified() throws IOException {
    Optional<ASTMCOptionalType> type = new MCCollectionTypesTestParser().parse_StringMCOptionalType("Optional<Person>");
    assertTrue(type.isPresent());
    TypeExpression setTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
    assertTrue(setTypeExpression instanceof GenericTypeExpression);
    assertTrue("Optional".equals(setTypeExpression.getName()));
    TypeExpression listTypeArgument = ((GenericTypeExpression) setTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof ObjectType);
    assertTrue("Person".equals(listTypeArgument.getName()));
  }
  @Test
  public void testOptionalPrimitive() throws IOException {
    for(String primitive : primitiveTypes) {
      Optional<ASTMCOptionalType> type = new MCCollectionTypesTestParser().parse_StringMCOptionalType("Optional<"+primitive+">");
      assertTrue(type.isPresent());
      TypeExpression setTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
      assertTrue(setTypeExpression instanceof GenericTypeExpression);
      assertTrue("Optional".equals(setTypeExpression.getName()));
      TypeExpression listTypeArgument = ((GenericTypeExpression) setTypeExpression).getArguments().get(0);
      assertTrue(listTypeArgument instanceof TypeConstant);
      assertTrue(primitive.equals(listTypeArgument.getName()));
    }
  }



  @Test
  public void testSet() throws IOException {
    Optional<ASTMCSetType> type = new MCCollectionTypesTestParser().parse_StringMCSetType("Set<de.mc.Person>");
    assertTrue(type.isPresent());
    TypeExpression listTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
    assertTrue(listTypeExpression instanceof GenericTypeExpression);
    assertTrue("Set".equals(listTypeExpression.getName()));
    TypeExpression listTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof ObjectType);
    assertTrue("de.mc.Person".equals(listTypeArgument.getName()));
  }

  @Test
  public void testSetUnqualified() throws IOException {
    Optional<ASTMCSetType> type = new MCCollectionTypesTestParser().parse_StringMCSetType("Set<Person>");
    assertTrue(type.isPresent());
    TypeExpression setTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
    assertTrue(setTypeExpression instanceof GenericTypeExpression);
    assertTrue("Set".equals(setTypeExpression.getName()));
    TypeExpression listTypeArgument = ((GenericTypeExpression) setTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof ObjectType);
    assertTrue("Person".equals(listTypeArgument.getName()));
  }

  @Test
  public void testSetPrimitives() throws IOException {
    for(String primitive : primitiveTypes) {
      Optional<ASTMCSetType> type = new MCCollectionTypesTestParser().parse_StringMCSetType("Set<" + primitive + ">");
      assertTrue(type.isPresent());
      TypeExpression setTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
      assertTrue(setTypeExpression instanceof GenericTypeExpression);
      assertTrue("Set".equals(setTypeExpression.getName()));
      TypeExpression listTypeArgument = ((GenericTypeExpression) setTypeExpression).getArguments().get(0);
      assertTrue(listTypeArgument instanceof TypeConstant);
      assertTrue(primitive.equals(listTypeArgument.getName()));
    }
  }

  @Test
  public void testList() throws IOException {
    Optional<ASTMCListType> type = new MCCollectionTypesTestParser().parse_StringMCListType("List<de.mc.Person>");
    assertTrue(type.isPresent());
    TypeExpression listTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
    assertTrue(listTypeExpression instanceof GenericTypeExpression);
    assertTrue("List".equals(listTypeExpression.getName()));
    TypeExpression listTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof ObjectType);
    assertTrue("de.mc.Person".equals(listTypeArgument.getName()));
  }

  @Test
  public void testListUnqualified() throws IOException {
    Optional<ASTMCListType> type = new MCCollectionTypesTestParser().parse_StringMCListType("List<Person>");
    assertTrue(type.isPresent());
    TypeExpression listTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
    assertTrue(listTypeExpression instanceof GenericTypeExpression);
    assertTrue("List".equals(listTypeExpression.getName()));
    TypeExpression listTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof ObjectType);
    assertTrue("Person".equals(listTypeArgument.getName()));
  }


  @Test
  public void testListPrimitive() throws IOException {
    for(String primitive : primitiveTypes) {
      Optional<ASTMCListType> type = new MCCollectionTypesTestParser().parse_StringMCListType("List<" + primitive + ">");
      assertTrue(type.isPresent());
      TypeExpression listTypeExpression = TypesCalculatorHelper.mcType2TypeExpression(type.get());
      assertTrue(listTypeExpression instanceof GenericTypeExpression);
      assertTrue("List".equals(listTypeExpression.getName()));
      TypeExpression listTypeArgument = ((GenericTypeExpression) listTypeExpression).getArguments().get(0);
      assertTrue(listTypeArgument instanceof TypeConstant);
      assertTrue(primitive.equals(listTypeArgument.getName()));
    }
  }


  @Test
  public void testPrimitives() throws IOException {

    for(String primitive : primitiveTypes) {
      Optional<ASTMCPrimitiveType> type = new MCCollectionTypesTestParser().parse_StringMCPrimitiveType(primitive);
      assertTrue(type.isPresent());
      ASTMCPrimitiveType booleanType = type.get();
      TypeExpression typeExpression = TypesCalculatorHelper.mcType2TypeExpression(booleanType);
      assertTrue(typeExpression instanceof TypeConstant);
      assertTrue(primitive.equals(typeExpression.getName()));
    }
  }
  @Test
  public void testVoid() throws IOException {
    Optional<ASTMCVoidType> type = new MCCollectionTypesTestParser().parse_StringMCVoidType("void");
    assertTrue(type.isPresent());
    ASTMCVoidType booleanType = type.get();
    TypeExpression typeExpression = TypesCalculatorHelper.mcType2TypeExpression(booleanType);
    assertTrue(typeExpression instanceof TypeConstant);
    assertTrue("void".equals(typeExpression.getName()));
  }


  @Test
  public void testQualifiedType() throws IOException {
    Optional<ASTMCQualifiedType> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedType("de.mc.Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedType qualifiedType = type.get();
    TypeExpression typeExpression = TypesCalculatorHelper.mcType2TypeExpression(qualifiedType);
    assertTrue(typeExpression instanceof ObjectType);
    assertTrue("de.mc.Person".equals(typeExpression.getName()));
  }
  @Test
  public void testQualifiedTypeUnqualified() throws IOException {
    Optional<ASTMCQualifiedType> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedType("Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedType qualifiedType = type.get();
    TypeExpression typeExpression = TypesCalculatorHelper.mcType2TypeExpression(qualifiedType);
    assertTrue(typeExpression instanceof ObjectType);
    assertTrue("Person".equals(typeExpression.getName()));
  }
  @Test
  public void testQualifiedName() throws IOException {
    Optional<ASTMCQualifiedName> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedName("de.mc.Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedName qualifiedName = type.get();
    TypeExpression typeExpression = TypesCalculatorHelper.mcType2TypeExpression(qualifiedName);
    assertTrue(typeExpression instanceof ObjectType);
    assertTrue("de.mc.Person".equals(typeExpression.getName()));
  }
  @Test
  public void testQualifiedNameUnqualified() throws IOException {
    Optional<ASTMCQualifiedName> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedName("Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedName qualifiedName = type.get();
    TypeExpression typeExpression = TypesCalculatorHelper.mcType2TypeExpression(qualifiedName);
    assertTrue(typeExpression instanceof ObjectType);
    assertTrue("Person".equals(typeExpression.getName()));
  }

}
