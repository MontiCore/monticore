/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.helper;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
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

    ICombineExpressionsWithLiteralsArtifactScope demc = CombineExpressionsWithLiteralsMill.artifactScope();
    demc.setPackageName("de.mc");
    demc.setEnclosingScope(gs);
    demc.add(DefsTypeBasic.type("PairA"));
    demc.add(DefsTypeBasic.type("PairB"));
    demc.add(DefsTypeBasic.type("PairC"));
    demc.add(DefsTypeBasic.type("Person"));
    demc.add(DefsTypeBasic.type("PersonKey"));
    demc.add(DefsTypeBasic.type("PersonValue"));

    ICombineExpressionsWithLiteralsArtifactScope deutil = CombineExpressionsWithLiteralsMill.artifactScope();
    deutil.setPackageName("de.util");
    deutil.setEnclosingScope(gs);
    deutil.add(DefsTypeBasic.type("Pair"));
    deutil.add(DefsTypeBasic.type("Pair2"));
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

  private SymTypeExpression mcType2TypeExpression(ASTMCBasicTypesNode type, boolean setScope) {
    if(setScope) {
      type.accept(traverser);
    }
    SynthesizeSymTypeFromCombineExpressionsWithLiteralsDelegator visitor = new SynthesizeSymTypeFromCombineExpressionsWithLiteralsDelegator();
    type.accept(visitor.getTraverser());
    return visitor.getResult().get();
  }

  @Test
  public void testBasicGeneric() throws IOException {
    Optional<ASTMCBasicGenericType> type = new MCFullGenericTypesTestParser().parse_StringMCBasicGenericType("de.util.Pair<de.mc.PairA,de.mc.PairB>");
    assertTrue(type.isPresent());
    ICombineExpressionsWithLiteralsScope demc = CombineExpressionsWithLiteralsMill.globalScope().getSubScopes().get(0);
    ICombineExpressionsWithLiteralsScope deutil = CombineExpressionsWithLiteralsMill.globalScope().getSubScopes().get(1);
    type.get().setEnclosingScope(deutil);
    type.get().getMCTypeArgumentList().forEach(arg -> {
      arg.setEnclosingScope(demc);
      arg.getMCTypeOpt().get().setEnclosingScope(demc);
    });
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get(), false);
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
    ICombineExpressionsWithLiteralsScope demc = CombineExpressionsWithLiteralsMill.globalScope().getSubScopes().get(0);
    ICombineExpressionsWithLiteralsScope deutil = CombineExpressionsWithLiteralsMill.globalScope().getSubScopes().get(1);
    Optional<ASTMCBasicGenericType> type = new MCFullGenericTypesTestParser().parse_StringMCBasicGenericType("de.util.Pair<de.mc.PairA,de.util.Pair2<de.mc.PairB,de.mc.PairC>>");
    assertTrue(type.isPresent());
    type.get().setEnclosingScope(deutil);
    type.get().getMCTypeArgument(0).setEnclosingScope(demc);
    type.get().getMCTypeArgument(0).getMCTypeOpt().get().setEnclosingScope(demc);
    type.get().getMCTypeArgument(1).setEnclosingScope(deutil);
    type.get().getMCTypeArgument(1).getMCTypeOpt().get().setEnclosingScope(deutil);
    assertTrue(type.get().getMCTypeArgument(1).getMCTypeOpt().get() instanceof ASTMCBasicGenericType);
    ASTMCBasicGenericType first = (ASTMCBasicGenericType) type.get().getMCTypeArgument(1).getMCTypeOpt().get();
    first.getMCTypeArgument(0).setEnclosingScope(demc);
    first.getMCTypeArgument(0).getMCTypeOpt().get().setEnclosingScope(demc);
    first.getMCTypeArgument(1).setEnclosingScope(demc);
    first.getMCTypeArgument(1).getMCTypeOpt().get().setEnclosingScope(demc);
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get(), false);
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
    ICombineExpressionsWithLiteralsScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    ICombineExpressionsWithLiteralsScope demc = gs.getSubScopes().get(0);
    Optional<ASTMCMapType> type = new MCCollectionTypesTestParser().parse_StringMCMapType("Map<de.mc.PersonKey,de.mc.PersonValue>");
    assertTrue(type.isPresent());
    type.get().setEnclosingScope(gs);
    type.get().getMCTypeArgumentList().forEach(arg ->{
      arg.setEnclosingScope(demc);
      arg.getMCTypeOpt().get().setEnclosingScope(demc);
    });
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get(), false);
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
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get(), true);
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
        SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get(), true);
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
    ICombineExpressionsWithLiteralsScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    ICombineExpressionsWithLiteralsScope demc = gs.getSubScopes().get(0);
    Optional<ASTMCOptionalType> type = new MCCollectionTypesTestParser().parse_StringMCOptionalType("Optional<de.mc.Person>");
    assertTrue(type.isPresent());
    type.get().setEnclosingScope(gs);
    type.get().getMCTypeArgument().setEnclosingScope(demc);
    type.get().getMCTypeArgument().getMCTypeOpt().get().setEnclosingScope(demc);
    SymTypeExpression optSymTypeExpression = mcType2TypeExpression(type.get(), false);
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
    SymTypeExpression optSymTypeExpression = mcType2TypeExpression(type.get(), true);
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
      SymTypeExpression optSymTypeExpression = mcType2TypeExpression(type.get(), true);
      assertTrue(optSymTypeExpression instanceof SymTypeOfGenerics);
      assertEquals("Optional", ((SymTypeOfGenerics) optSymTypeExpression).getTypeConstructorFullName());
      SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) optSymTypeExpression).getArgumentList().get(0);
      assertTrue(listTypeArgument instanceof SymTypeConstant);
      assertEquals(primitive, listTypeArgument.printFullName());
    }
  }


  @Test
  public void testSet() throws IOException {
    ICombineExpressionsWithLiteralsScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    ICombineExpressionsWithLiteralsScope demc = gs.getSubScopes().get(0);
    Optional<ASTMCSetType> type = new MCCollectionTypesTestParser().parse_StringMCSetType("Set<de.mc.Person>");
    assertTrue(type.isPresent());
    type.get().setEnclosingScope(gs);
    type.get().getMCTypeArgument().setEnclosingScope(demc);
    type.get().getMCTypeArgument().getMCTypeOpt().get().setEnclosingScope(demc);
    SymTypeExpression setSymTypeExpression = mcType2TypeExpression(type.get(), false);
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
    SymTypeExpression setSymTypeExpression = mcType2TypeExpression(type.get(), true);
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
              mcType2TypeExpression(type.get(), true);
      assertTrue(setSymTypeExpression instanceof SymTypeOfGenerics);
      assertEquals("Set", ((SymTypeOfGenerics) setSymTypeExpression).getTypeConstructorFullName());
      SymTypeExpression listTypeArgument = ((SymTypeOfGenerics) setSymTypeExpression).getArgumentList().get(0);
      assertTrue(listTypeArgument instanceof SymTypeConstant);
      assertEquals(primitive, listTypeArgument.printFullName());
    }
  }

  @Test
  public void testList() throws IOException {
    ICombineExpressionsWithLiteralsScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    ICombineExpressionsWithLiteralsScope demc = gs.getSubScopes().get(0);
    Optional<ASTMCListType> type = new MCCollectionTypesTestParser().parse_StringMCListType("List<de.mc.Person>");
    assertTrue(type.isPresent());
    type.get().setEnclosingScope(gs);
    type.get().getMCTypeArgument().setEnclosingScope(demc);
    type.get().getMCTypeArgument().getMCTypeOpt().get().setEnclosingScope(demc);
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get(), false);
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
    SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get(), true);
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
      SymTypeExpression listSymTypeExpression = mcType2TypeExpression(type.get(), true);
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
      SymTypeExpression symTypeExpression = mcType2TypeExpression(booleanType, true);
      assertTrue(symTypeExpression instanceof SymTypeConstant);
      assertEquals(primitive, symTypeExpression.printFullName());
    }
  }

  @Test
  public void testVoid() throws IOException {
    Optional<ASTMCVoidType> type = new MCCollectionTypesTestParser().parse_StringMCVoidType("void");
    assertTrue(type.isPresent());
    ASTMCVoidType booleanType = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(booleanType, true);
    assertTrue(symTypeExpression instanceof SymTypeVoid);
    assertEquals("void", symTypeExpression.printFullName());
  }


  @Test
  public void testQualifiedType() throws IOException {
    ICombineExpressionsWithLiteralsScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    Optional<ASTMCQualifiedType> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedType("de.mc.Person");
    assertTrue(type.isPresent());
    type.get().setEnclosingScope(gs);
    ASTMCQualifiedType qualifiedType = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedType, false);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("de.mc.Person", symTypeExpression.printFullName());
  }

  @Test
  public void testQualifiedTypeUnqualified() throws IOException {
    Optional<ASTMCQualifiedType> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedType("Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedType qualifiedType = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedType, true);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("Person", symTypeExpression.printFullName());
  }

  @Test
  public void testQualifiedName() throws IOException {
    ICombineExpressionsWithLiteralsScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    Optional<ASTMCQualifiedName> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedName("de.mc.Person");
    assertTrue(type.isPresent());
    type.get().setEnclosingScope(gs);
    ASTMCQualifiedName qualifiedName = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedName, false);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("de.mc.Person", symTypeExpression.printFullName());
  }

  @Test
  public void testQualifiedNameUnqualified() throws IOException {
    Optional<ASTMCQualifiedName> type = new MCCollectionTypesTestParser().parse_StringMCQualifiedName("Person");
    assertTrue(type.isPresent());
    ASTMCQualifiedName qualifiedName = type.get();
    SymTypeExpression symTypeExpression = mcType2TypeExpression(qualifiedName, true);
    assertTrue(symTypeExpression instanceof SymTypeOfObject);
    assertEquals("Person", symTypeExpression.printFullName());
  }

}
