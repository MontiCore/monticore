/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.types.MCTypesHelper;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.monticore.types2.SymTypeOfGenerics;
import de.monticore.types2.SymTypeOfObject;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MCType2SymTypeExpressionTest {

  List<String> primitiveTypes = Arrays
      .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");


  //TODO RE Fix Types Grammar/AST
  @Ignore
  @Test
  public void testBasicGeneric() throws IOException {
    Optional<ASTMCType> type = new MCFullGenericTypesTestParser().parse_StringMCType("de.util.Pair<de.mc.PairA,de.mc.PairB>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertTrue("de.util.Pair".equals(listSymTypeExpression.getName()));
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertTrue("de.mc.PairA".equals(keyTypeArgument.getName()));

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfObject);
    assertTrue("de.mc.PairB".equals(valueTypeArgument.getName()));
  }
  //TODO RE Fix Types Grammar/AST
  @Ignore
  @Test
  public void testBasicGenericRekursiv() throws IOException {
    Optional<ASTMCType> type = new MCFullGenericTypesTestParser().parse_StringMCType("de.util.Pair<de.mc.PairA,de.util.Pair2<de.mc.PairB,de.mc.PairC>>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertTrue("de.util.Pair".equals(listSymTypeExpression.getName()));
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertTrue("de.mc.PairA".equals(keyTypeArgument.getName()));

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfGenerics);
    assertEquals("de.util.Pair2",valueTypeArgument.getName());

    SymTypeOfGenerics valueTypeArg = (SymTypeOfGenerics) valueTypeArgument;

    SymTypeExpression argument1 = valueTypeArg.getArguments().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PairB",argument1.getName());

    SymTypeExpression argument2 = valueTypeArg.getArguments().get(1);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PairC",argument2.getName());


  }

  @Test
  public void testMap() throws IOException {
    Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<de.mc.PersonKey,de.mc.PersonValue>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertTrue("Map".equals(listSymTypeExpression.getName()));
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertTrue("de.mc.PersonKey".equals(keyTypeArgument.getName()));

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfObject);
    assertTrue("de.mc.PersonValue".equals(valueTypeArgument.getName()));

  }

  @Test
  public void testMapUnqualified() throws IOException {
    Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<PersonKey,PersonValue>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertTrue("Map".equals(listSymTypeExpression.getName()));
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertTrue("PersonKey".equals(keyTypeArgument.getName()));

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfObject);
    assertTrue("PersonValue".equals(valueTypeArgument.getName()));
  }

  @Test
  public void testMapPrimitives() throws IOException {
    for(String primitiveKey : primitiveTypes) {
      for(String primitiveValue : primitiveTypes) {
        Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<"+primitiveKey+","+primitiveValue+">");
        assertTrue(type.isPresent());
        SymTypeExpression listSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
        assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
        assertTrue("Map".equals(listSymTypeExpression.getName()));

        SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(0);
        assertTrue(keyTypeArgument instanceof SymTypeConstant);
        assertTrue(primitiveKey.equals(keyTypeArgument.getName()));

        SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(1);
        assertTrue(valueTypeArgument instanceof SymTypeConstant);
        assertTrue(primitiveValue.equals(valueTypeArgument.getName()));
      }
    }

  }
  //TODO RE Fix Types Grammar/AST
  @Ignore
  @Test
  public void testOptional() throws IOException {
    Optional<ASTMCOptionalType> type = new MCCollectionTypesTestParser().parse_StringMCOptionalType("Optional<de.mc.Person>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertTrue("Optional".equals(listSymTypeExpression.getName()));
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertTrue("de.mc.Person".equals(listTypeArgument.getName()));
  }
  //TODO RE Fix Types Grammar/AST
  @Ignore
  @Test
  public void testOptionalUnqualified() throws IOException {
    Optional<ASTMCOptionalType> type = new MCCollectionTypesTestParser().parse_StringMCOptionalType("Optional<Person>");
    assertTrue(type.isPresent());
    SymTypeExpression setSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
    assertTrue(setSymTypeExpression instanceof SymTypeOfGenerics);
    assertTrue("Optional".equals(setSymTypeExpression.getName()));
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) setSymTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertTrue("Person".equals(listTypeArgument.getName()));
  }
  //TODO RE Fix Types Grammar/AST
  @Ignore
  @Test
  public void testOptionalPrimitive() throws IOException {
    for(String primitive : primitiveTypes) {
      Optional<ASTMCOptionalType> type = new MCCollectionTypesTestParser().parse_StringMCOptionalType("Optional<"+primitive+">");
      assertTrue(type.isPresent());
      SymTypeExpression setSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
      assertTrue(setSymTypeExpression instanceof SymTypeOfGenerics);
      assertTrue("Optional".equals(setSymTypeExpression.getName()));
      SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) setSymTypeExpression).getArguments().get(0);
      assertTrue(listTypeArgument instanceof SymTypeConstant);
      assertTrue(primitive.equals(listTypeArgument.getName()));
    }
  }


  //TODO RE Fix Types Grammar/AST
  @Ignore
  @Test
  public void testSet() throws IOException {
    Optional<ASTMCSetType> type = new MCCollectionTypesTestParser().parse_StringMCSetType("Set<de.mc.Person>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertTrue("Set".equals(listSymTypeExpression.getName()));
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertTrue("de.mc.Person".equals(listTypeArgument.getName()));
  }
//TODO RE Fix Types Grammar/AST
  @Ignore
    @Test
  public void testSetUnqualified() throws IOException {
    Optional<ASTMCSetType> type = new MCCollectionTypesTestParser().parse_StringMCSetType("Set<Person>");
    assertTrue(type.isPresent());
    SymTypeExpression setSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
    assertTrue(setSymTypeExpression instanceof SymTypeOfGenerics);
    assertTrue("Set".equals(setSymTypeExpression.getName()));
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) setSymTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertTrue("Person".equals(listTypeArgument.getName()));
  }
  //TODO RE Fix Types Grammar/AST
  @Ignore
  @Test
  public void testSetPrimitives() throws IOException {
    for(String primitive : primitiveTypes) {
      Optional<ASTMCSetType> type = new MCCollectionTypesTestParser().parse_StringMCSetType("Set<" + primitive + ">");
      assertTrue(type.isPresent());
      SymTypeExpression setSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
      assertTrue(setSymTypeExpression instanceof SymTypeOfGenerics);
      assertTrue("Set".equals(setSymTypeExpression.getName()));
      SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) setSymTypeExpression).getArguments().get(0);
      assertTrue(listTypeArgument instanceof SymTypeConstant);
      assertTrue(primitive.equals(listTypeArgument.getName()));
    }
  }
  //TODO RE Fix Types Grammar/AST
  @Ignore
  @Test
  public void testList() throws IOException {
    Optional<ASTMCListType> type = new MCCollectionTypesTestParser().parse_StringMCListType("List<de.mc.Person>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertTrue("List".equals(listSymTypeExpression.getName()));
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertTrue("de.mc.Person".equals(listTypeArgument.getName()));
  }
  //TODO RE Fix Types Grammar/AST
  @Ignore
  @Test
  public void testListUnqualified() throws IOException {
    Optional<ASTMCListType> type = new MCCollectionTypesTestParser().parse_StringMCListType("List<Person>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertTrue("List".equals(listSymTypeExpression.getName()));
    SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(0);
    assertTrue(listTypeArgument instanceof SymTypeOfObject);
    assertTrue("Person".equals(listTypeArgument.getName()));
  }

  //TODO RE Fix Types Grammar/AST
  @Ignore
  @Test
  public void testListPrimitive() throws IOException {
    for(String primitive : primitiveTypes) {
      Optional<ASTMCListType> type = new MCCollectionTypesTestParser().parse_StringMCListType("List<" + primitive + ">");
      assertTrue(type.isPresent());
      SymTypeExpression listSymTypeExpression = MCTypesHelper.mcType2TypeExpression(type.get());
      assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
      assertTrue("List".equals(listSymTypeExpression.getName()));
      SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArguments().get(0);
      assertTrue(listTypeArgument instanceof SymTypeConstant);
      assertTrue(primitive.equals(listTypeArgument.getName()));
    }
  }


  @Test
  public void testPrimitives() throws IOException {

    for(String primitive : primitiveTypes) {
      Optional<ASTMCPrimitiveType> type = new MCCollectionTypesTestParser().parse_StringMCPrimitiveType(primitive);
      assertTrue(type.isPresent());
      ASTMCPrimitiveType booleanType = type.get();
      SymTypeExpression symTypeExpression = MCTypesHelper.mcType2TypeExpression(booleanType);
      assertTrue(symTypeExpression instanceof SymTypeConstant);
      assertTrue(primitive.equals(symTypeExpression.getName()));
    }
  }
  @Test
  public void testVoid() throws IOException {
    Optional<ASTMCVoidType> type = new MCCollectionTypesTestParser().parse_StringMCVoidType("void");
    assertTrue(type.isPresent());
    ASTMCVoidType booleanType = type.get();
    SymTypeExpression symTypeExpression = MCTypesHelper.mcType2TypeExpression(booleanType);
    assertTrue(symTypeExpression instanceof SymTypeConstant);
    assertTrue("void".equals(symTypeExpression.getName()));
  }


  @Test
  public void testQualifiedType() throws IOException {
    Optional<ASTMCQualifiedType> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedType("de.mc.Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedType qualifiedType = type.get();
    SymTypeExpression symTypeExpression = MCTypesHelper.mcType2TypeExpression(qualifiedType);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertTrue("de.mc.Person".equals(symTypeExpression.getName()));
  }
  @Test
  public void testQualifiedTypeUnqualified() throws IOException {
    Optional<ASTMCQualifiedType> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedType("Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedType qualifiedType = type.get();
    SymTypeExpression symTypeExpression = MCTypesHelper.mcType2TypeExpression(qualifiedType);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertTrue("Person".equals(symTypeExpression.getName()));
  }
  @Test
  public void testQualifiedName() throws IOException {
    Optional<ASTMCQualifiedName> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedName("de.mc.Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedName qualifiedName = type.get();
    SymTypeExpression symTypeExpression = MCTypesHelper.mcType2TypeExpression(qualifiedName);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertTrue("de.mc.Person".equals(symTypeExpression.getName()));
  }
  @Test
  public void testQualifiedNameUnqualified() throws IOException {
    Optional<ASTMCQualifiedName> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedName("Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedName qualifiedName = type.get();
    SymTypeExpression symTypeExpression = MCTypesHelper.mcType2TypeExpression(qualifiedName);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertTrue("Person".equals(symTypeExpression.getName()));
  }

}
