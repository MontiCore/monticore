/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsSymbols2Json;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
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
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    init();
  }

  public static void init(){
    ICombineExpressionsWithLiteralsGlobalScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    gs.add(DefsTypeBasic.type("Person"));
    gs.add(DefsTypeBasic.type("Map"));
    gs.add(DefsTypeBasic.type("List"));
    gs.add(DefsTypeBasic.type("Set"));
    gs.add(DefsTypeBasic.type("Optional"));
    gs.add(DefsTypeBasic.type("PersonKey"));
    gs.add(DefsTypeBasic.type("PersonValue"));

    CombineExpressionsWithLiteralsSymbols2Json symbols2Json = new CombineExpressionsWithLiteralsSymbols2Json();
    ICombineExpressionsWithLiteralsArtifactScope as = symbols2Json.load("src/test/resources/de/monticore/types/check/PairA.cesym");
    as.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as2 = symbols2Json.load("src/test/resources/de/monticore/types/check/PairB.cesym");
    as2.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as3 = symbols2Json.load("src/test/resources/de/monticore/types/check/PairC.cesym");
    as3.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as4 = symbols2Json.load("src/test/resources/de/monticore/types/check/Persondemc.cesym");
    as4.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as5 = symbols2Json.load("src/test/resources/de/monticore/types/check/PersonKey.cesym");
    as5.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as6 = symbols2Json.load("src/test/resources/de/monticore/types/check/PersonValue.cesym");
    as6.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as7 = symbols2Json.load("src/test/resources/de/monticore/types/check/Pair.cesym");
    as7.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as8 = symbols2Json.load("src/test/resources/de/monticore/types/check/Pair2.cesym");
    as8.setEnclosingScope(gs);
  }

  FlatExpressionScopeSetter scopeSetter = new FlatExpressionScopeSetter(CombineExpressionsWithLiteralsMill.globalScope());
  CombineExpressionsWithLiteralsTraverser traverser = CombineExpressionsWithLiteralsMill.traverser();

  @Before
  public void initScope(){
    traverser = CombineExpressionsWithLiteralsMill.traverser();
    traverser.add4MCSimpleGenericTypes(scopeSetter);
    traverser.add4MCCollectionTypes(scopeSetter);
    traverser.add4MCBasicTypes(scopeSetter);
  }

  List<String> primitiveTypes = Arrays
      .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");

  private SymTypeExpression mcType2TypeExpression(ASTMCType type) {
    type.accept(traverser);
    FullSynthesizeFromCombineExpressionsWithLiterals visitor = new FullSynthesizeFromCombineExpressionsWithLiterals();
    return visitor.synthesizeType(type).getResult();
  }

  private SymTypeExpression mcType2TypeExpression(ASTMCVoidType type){
    type.accept(traverser);
    FullSynthesizeFromCombineExpressionsWithLiterals visitor = new FullSynthesizeFromCombineExpressionsWithLiterals();
    TypeCalculator tc = new TypeCalculator(visitor, null);
    return tc.symTypeFromAST(type);
  }

  private SymTypeExpression mcType2TypeExpression(ASTMCQualifiedName qName){
    qName.accept(traverser);
    FullSynthesizeFromCombineExpressionsWithLiterals visitor = new FullSynthesizeFromCombineExpressionsWithLiterals();
    return visitor.synthesizeType(qName).getResult();
  }

  @Test
  public void testBasicGeneric() throws IOException {
    Optional<ASTMCBasicGenericType> type = new MCFullGenericTypesTestParser().parse_StringMCBasicGenericType("de.util.Pair<de.mc.PairA,de.mc.PairB>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("de.util.Pair", ((SymTypeOfGenerics) listSymTypeExpression).getTypeConstructorFullName());
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PairA", keyTypeArgument.printFullName());

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PairB", valueTypeArgument.printFullName());
  }

  @Test
  public void testBasicGenericRekursiv() throws IOException {
    Optional<ASTMCBasicGenericType> type = new MCFullGenericTypesTestParser().parse_StringMCBasicGenericType("de.util.Pair<de.mc.PairA,de.util.Pair2<de.mc.PairB,de.mc.PairC>>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("de.util.Pair", ((SymTypeOfGenerics) listSymTypeExpression).getTypeConstructorFullName());
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PairA", keyTypeArgument.printFullName());

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfGenerics);
    assertEquals("de.util.Pair2", ((SymTypeOfGenerics) valueTypeArgument).getTypeConstructorFullName());

    SymTypeOfGenerics valueTypeArg = (SymTypeOfGenerics) valueTypeArgument;

    SymTypeExpression argument1 = valueTypeArg.getArgumentList().get(0);
    assertEquals("de.mc.PairB", argument1.printFullName());

    SymTypeExpression argument2 = valueTypeArg.getArgumentList().get(1);
    assertEquals("de.mc.PairC", argument2.printFullName());


  }

  @Test
  public void testMap() throws IOException {
    Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<de.mc.PersonKey,de.mc.PersonValue>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("Map<de.mc.PersonKey,de.mc.PersonValue>", listSymTypeExpression.printFullName());
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PersonKey", keyTypeArgument.printFullName());

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfObject);
    assertEquals("de.mc.PersonValue", valueTypeArgument.printFullName());
  }

  @Test
  public void testMapUnqualified() throws IOException {
    Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<PersonKey,PersonValue>");
    assertTrue(type.isPresent());
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
    assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
    assertEquals("Map<PersonKey,PersonValue>", listSymTypeExpression.printFullName());
    SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
    assertTrue(keyTypeArgument instanceof SymTypeOfObject);
    assertEquals("PersonKey", keyTypeArgument.printFullName());

    SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(1);
    assertTrue(valueTypeArgument instanceof SymTypeOfObject);
    assertEquals("PersonValue", valueTypeArgument.printFullName());
  }

  @Test
  public void testMapPrimitives() throws IOException {
    for (String primitiveKey : primitiveTypes) {
      for (String primitiveValue : primitiveTypes) {
        Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<" + primitiveKey + "," + primitiveValue + ">");
        assertTrue(type.isPresent());
        SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get());
        assertTrue(listSymTypeExpression instanceof SymTypeOfGenerics);
        assertEquals(("Map<" + primitiveKey + "," + primitiveValue + ">"), listSymTypeExpression.printFullName());

        SymTypeExpression keyTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(0);
        assertTrue(keyTypeArgument instanceof SymTypeConstant);
        assertEquals(primitiveKey, keyTypeArgument.printFullName());

        SymTypeExpression valueTypeArgument = ((SymTypeOfGenerics) listSymTypeExpression).getArgumentList().get(1);
        assertTrue(valueTypeArgument instanceof SymTypeConstant);
        assertEquals(primitiveValue, valueTypeArgument.printFullName());
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
    assertEquals("de.mc.Person", listTypeArgument.printFullName());
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
    assertEquals("Person", listTypeArgument.printFullName());
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
      assertEquals(primitive, listTypeArgument.printFullName());
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
    assertEquals("de.mc.Person", listTypeArgument.printFullName());
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
    assertEquals("Person", listTypeArgument.printFullName());
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
      assertEquals(primitive, listTypeArgument.printFullName());
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
    assertEquals("de.mc.Person", listTypeArgument.printFullName());
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
    assertEquals("Person", listTypeArgument.printFullName());
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
      assertEquals(primitive, listTypeArgument.printFullName());
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
      assertEquals(primitive, symTypeExpression.printFullName());
    }
  }

  @Test
  public void testVoid() throws IOException {
    Optional<ASTMCVoidType> type = new MCCollectionTypesTestParser().parse_StringMCVoidType("void");
    assertTrue(type.isPresent());
    ASTMCVoidType booleanType = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(booleanType);
    assertTrue(symTypeExpression instanceof SymTypeVoid);
    assertEquals("void", symTypeExpression.printFullName());
  }


  @Test
  public void testQualifiedType() throws IOException {
    Optional<ASTMCQualifiedType> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedType("de.mc.Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedType qualifiedType = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedType);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("de.mc.Person", symTypeExpression.printFullName());
  }

  @Test
  public void testQualifiedTypeUnqualified() throws IOException {
    Optional<ASTMCQualifiedType> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedType("Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedType qualifiedType = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedType);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("Person", symTypeExpression.printFullName());
  }

  @Test
  public void testQualifiedName() throws IOException {
    Optional<ASTMCQualifiedName> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedName("de.mc.Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedName qualifiedName = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedName);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("de.mc.Person", symTypeExpression.printFullName());
  }

  @Test
  public void testQualifiedNameUnqualified() throws IOException {
    Optional<ASTMCQualifiedName> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedName("Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedName qualifiedName = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedName);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("Person", symTypeExpression.printFullName());
  }

}
