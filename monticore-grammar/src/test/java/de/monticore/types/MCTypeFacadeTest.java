/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class MCTypeFacadeTest {

  private MCTypeFacade mcTypeFacade;

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
  }

  @Test
  public void testCreateQualifiedTypeName() {
    ASTMCQualifiedType qualifiedType = mcTypeFacade.createQualifiedType("a.b.c.Foo");
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), qualifiedType.getNameList());
  }

  @Test
  public void testCreateQualifiedTypeClass() {
    ASTMCQualifiedType qualifiedType = mcTypeFacade.createQualifiedType(java.lang.String.class);
    assertEquals(Lists.newArrayList("String"), qualifiedType.getNameList());
  }

  @Test
  public void testCreateBasicTypeArgumentOf() {
    ASTMCBasicTypeArgument type = mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo");
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), type.getMCQualifiedType().getNameList());
    assertTrue(type.getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateWildCardWithNoBounds() {
    ASTMCWildcardTypeArgument type = mcTypeFacade.createWildCardWithNoBounds();
    assertFalse(type.isPresentLowerBound());
    assertFalse(type.isPresentUpperBound());
  }

  @Test
  public void testCreateWildCardWithUpperBoundTypeClass() {
    ASTMCWildcardTypeArgument type = mcTypeFacade.createWildCardWithUpperBoundType(String.class);
    assertFalse(type.isPresentLowerBound());
    assertTrue(type.isPresentUpperBound());
    assertTrue(type.getUpperBound() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getUpperBound()).getNameList());
  }


  @Test
  public void testCreateWildCardWithUpperBoundTypeName() {
    ASTMCWildcardTypeArgument type = mcTypeFacade.createWildCardWithUpperBoundType("a.b.c.Foo");
    assertFalse(type.isPresentLowerBound());
    assertTrue(type.isPresentUpperBound());
    assertTrue(type.getUpperBound() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getUpperBound()).getNameList());
  }

  @Test
  public void testCreateWildCardWithUpperBoundTypeType() {
    ASTMCWildcardTypeArgument type = mcTypeFacade.createWildCardWithUpperBoundType(mcTypeFacade.createQualifiedType("a.b.c.Foo"));
    assertFalse(type.isPresentLowerBound());
    assertTrue(type.isPresentUpperBound());
    assertTrue(type.getUpperBound() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getUpperBound()).getNameList());
  }

  @Test
  public void testCreateOptionalTypeOfClass() {
    ASTMCOptionalType type = mcTypeFacade.createOptionalTypeOf(String.class);
    assertEquals(1, type.sizeNames());
    assertEquals("Optional", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }


  @Test
  public void testCreateOptionalTypeOfName() {
    ASTMCOptionalType type = mcTypeFacade.createOptionalTypeOf("a.b.c.Foo");
    assertEquals(1, type.sizeNames());
    assertEquals("Optional", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateOptionalTypeOfType() {
    ASTMCOptionalType type = mcTypeFacade.createOptionalTypeOf(mcTypeFacade.createQualifiedType("a.b.c.Foo"));
    assertEquals(1, type.sizeNames());
    assertEquals("Optional", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateOptionalTypeOfTypeArgument() {
    ASTMCOptionalType type = mcTypeFacade.createOptionalTypeOf(mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo"));
    assertEquals(1, type.sizeNames());
    assertEquals("Optional", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateListTypeOfClass() {
    ASTMCListType type = mcTypeFacade.createListTypeOf(String.class);
    assertEquals(1, type.sizeNames());
    assertEquals("List", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }


  @Test
  public void testCreateListTypeOfName() {
    ASTMCListType type = mcTypeFacade.createListTypeOf("a.b.c.Foo");
    assertEquals(1, type.sizeNames());
    assertEquals("List", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateListTypeOfType() {
    ASTMCListType type = mcTypeFacade.createListTypeOf(mcTypeFacade.createQualifiedType("a.b.c.Foo"));
    assertEquals(1, type.sizeNames());
    assertEquals("List", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateListTypeOfTypeArgument() {
    ASTMCListType type = mcTypeFacade.createListTypeOf(mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo"));
    assertEquals(1, type.sizeNames());
    assertEquals("List", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateSetTypeOfClass() {
    ASTMCSetType type = mcTypeFacade.createSetTypeOf(String.class);
    assertEquals(1, type.sizeNames());
    assertEquals("Set", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }


  @Test
  public void testCreateSetTypeOfName() {
    ASTMCSetType type = mcTypeFacade.createSetTypeOf("a.b.c.Foo");
    assertEquals(1, type.sizeNames());
    assertEquals("Set", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateSetTypeOfType() {
    ASTMCSetType type = mcTypeFacade.createSetTypeOf(mcTypeFacade.createQualifiedType("a.b.c.Foo"));
    assertEquals(1, type.sizeNames());
    assertEquals("Set", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateSetTypeOfTypeArgument() {
    ASTMCSetType type = mcTypeFacade.createSetTypeOf(mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo"));
    assertEquals(1, type.sizeNames());
    assertEquals("Set", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateCollectionTypeOfClass() {
    ASTMCGenericType type = mcTypeFacade.createCollectionTypeOf(String.class);
    assertEquals(1, type.sizeNames());
    assertEquals("Collection", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }


  @Test
  public void testCreateCollectionTypeOfName() {
    ASTMCGenericType type = mcTypeFacade.createCollectionTypeOf("a.b.c.Foo");
    assertEquals(1, type.sizeNames());
    assertEquals("Collection", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateCollectionTypeOfType() {
    ASTMCGenericType type = mcTypeFacade.createCollectionTypeOf(mcTypeFacade.createQualifiedType("a.b.c.Foo"));
    assertEquals(1, type.sizeNames());
    assertEquals("Collection", type.getNames(0));
    assertEquals(1, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateMapTypeOfClass() {
    ASTMCMapType type = mcTypeFacade.createMapTypeOf(String.class, Integer.class);
    assertEquals(1, type.sizeNames());
    assertEquals("Map", type.getNames(0));
    assertEquals(2, type.sizeMCTypeArguments());
    assertTrue(type.getKey().getMCTypeOpt().isPresent());
    assertTrue(type.getKey().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getKey().getMCTypeOpt().get()).getNameList());

    assertTrue(type.getValue().getMCTypeOpt().isPresent());
    assertTrue(type.getValue().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("Integer"), ((ASTMCQualifiedType) type.getValue().getMCTypeOpt().get()).getNameList());
  }


  @Test
  public void testCreateMapTypeOfName() {
    ASTMCMapType type = mcTypeFacade.createMapTypeOf("a.b.c.Foo", "d.e.f.Bla");
    assertEquals(1, type.sizeNames());
    assertEquals("Map", type.getNames(0));
    assertEquals(2, type.sizeMCTypeArguments());
    assertTrue(type.getKey().getMCTypeOpt().isPresent());
    assertTrue(type.getKey().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getKey().getMCTypeOpt().get()).getNameList());

    assertTrue(type.getValue().getMCTypeOpt().isPresent());
    assertTrue(type.getValue().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getValue().getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateMapTypeOfType() {
    ASTMCMapType type = mcTypeFacade.createMapTypeOf(mcTypeFacade.createQualifiedType("a.b.c.Foo"), mcTypeFacade.createQualifiedType("d.e.f.Bla"));
    assertEquals(1, type.sizeNames());
    assertEquals("Map", type.getNames(0));
    assertEquals(2, type.sizeMCTypeArguments());
    assertTrue(type.getKey().getMCTypeOpt().isPresent());
    assertTrue(type.getKey().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getKey().getMCTypeOpt().get()).getNameList());

    assertTrue(type.getValue().getMCTypeOpt().isPresent());
    assertTrue(type.getValue().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getValue().getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateMapTypeOfTypeArgument() {
    ASTMCMapType type = mcTypeFacade.createMapTypeOf(mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo"), mcTypeFacade.createBasicTypeArgumentOf("d.e.f.Bla"));
    assertEquals(1, type.sizeNames());
    assertEquals("Map", type.getNames(0));
    assertEquals(2, type.sizeMCTypeArguments());
    assertTrue(type.getKey().getMCTypeOpt().isPresent());
    assertTrue(type.getKey().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getKey().getMCTypeOpt().get()).getNameList());

    assertTrue(type.getValue().getMCTypeOpt().isPresent());
    assertTrue(type.getValue().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getValue().getMCTypeOpt().get()).getNameList());
  }


  @Test
  public void testCreateBasicGenericTypeOfNameList() {
    ASTMCBasicTypeArgument basicTypeArgumentOfFoo = mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo");
    ASTMCBasicTypeArgument basicTypeArgumentOfBla = mcTypeFacade.createBasicTypeArgumentOf("d.e.f.Bla");

    ASTMCBasicGenericType type = mcTypeFacade.createBasicGenericTypeOf(
        Lists.newArrayList("my", "special", "GenericType"), Lists.newArrayList(basicTypeArgumentOfFoo, basicTypeArgumentOfBla));
    assertEquals(3, type.sizeNames());
    assertEquals(Lists.newArrayList("my","special","GenericType"), type.getNamesList());
    assertEquals(2, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());

    assertTrue(type.getMCTypeArguments(1).getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArguments(1).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getMCTypeArguments(1).getMCTypeOpt().get()).getNameList());
  }


  @Test
  public void testCreateBasicGenericTypeOfArgumentList() {
    ASTMCBasicTypeArgument basicTypeArgumentOfFoo = mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo");
    ASTMCBasicTypeArgument basicTypeArgumentOfBla = mcTypeFacade.createBasicTypeArgumentOf("d.e.f.Bla");

    ASTMCBasicGenericType type = mcTypeFacade.createBasicGenericTypeOf(
        "my.special.GenericType", Lists.newArrayList(basicTypeArgumentOfFoo, basicTypeArgumentOfBla));
    assertEquals(3, type.sizeNames());
    assertEquals(Lists.newArrayList("my","special","GenericType"), type.getNamesList());
    assertEquals(2, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());

    assertTrue(type.getMCTypeArguments(1).getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArguments(1).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getMCTypeArguments(1).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateBasicGenericTypeOfArgumentVarArgs() {
    ASTMCBasicTypeArgument basicTypeArgumentOfFoo = mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo");
    ASTMCBasicTypeArgument basicTypeArgumentOfBla = mcTypeFacade.createBasicTypeArgumentOf("d.e.f.Bla");

    ASTMCBasicGenericType type = mcTypeFacade.createBasicGenericTypeOf(
        "my.special.GenericType", basicTypeArgumentOfFoo, basicTypeArgumentOfBla);
    assertEquals(3, type.sizeNames());
    assertEquals(Lists.newArrayList("my","special","GenericType"), type.getNamesList());
    assertEquals(2, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());

    assertTrue(type.getMCTypeArguments(1).getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArguments(1).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getMCTypeArguments(1).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateBasicGenericTypeOfArgumentString() {
    ASTMCBasicGenericType type = mcTypeFacade.createBasicGenericTypeOf(
        "my.special.GenericType", "a.b.c.Foo", "d.e.f.Bla");
    assertEquals(3, type.sizeNames());
    assertEquals(Lists.newArrayList("my","special","GenericType"), type.getNamesList());
    assertEquals(2, type.sizeMCTypeArguments());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArguments(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArguments(0).getMCTypeOpt().get()).getNameList());

    assertTrue(type.getMCTypeArguments(1).getMCTypeOpt().isPresent());
    assertTrue(type.getMCTypeArguments(1).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getMCTypeArguments(1).getMCTypeOpt().get()).getNameList());
  }

  @Test
  public void testCreateArrayTypeString() {
    ASTMCArrayType type = mcTypeFacade.createArrayType("int", 3);
    assertEquals(3, type.getDimensions());
    assertTrue(type.getMCType() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("int"), ((ASTMCQualifiedType) type.getMCType()).getNameList());
  }

  @Test
  public void testCreateArrayTypeClass() {
    ASTMCArrayType type = mcTypeFacade.createArrayType(Integer.class, 3);
    assertEquals(3, type.getDimensions());
    assertTrue(type.getMCType() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("Integer"), ((ASTMCQualifiedType) type.getMCType()).getNameList());
  }

  @Test
  public void testCreateArrayTypeType() {
    ASTMCArrayType type = mcTypeFacade.createArrayType(mcTypeFacade.createQualifiedType("int"), 3);
    assertEquals(3, type.getDimensions());
    assertTrue(type.getMCType() instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("int"), ((ASTMCQualifiedType) type.getMCType()).getNameList());
  }

  @Test
  public void testCreateVoidType() {
    ASTMCVoidType type = mcTypeFacade.createVoidType();
  }

  @Test
  public void testCreateBooleanType() {
    ASTMCPrimitiveType type = mcTypeFacade.createBooleanType();
    assertTrue(type.isBoolean());
  }

  @Test
  public void testIsBooleanType() {
    ASTMCPrimitiveType booleanType = mcTypeFacade.createBooleanType();
    assertTrue(mcTypeFacade.isBooleanType(booleanType));

    ASTMCType stringType = mcTypeFacade.createStringType();
    assertFalse(mcTypeFacade.isBooleanType(stringType));
  }

  @Test
  public void testCreateIntType() {
    ASTMCPrimitiveType type = mcTypeFacade.createIntType();
    assertTrue(type.isInt());
  }

  @Test
  public void testCreatePrimitiveType() {
    ASTMCType type = mcTypeFacade.createStringType();
    assertTrue(type instanceof ASTMCQualifiedType);
    assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type).getNameList());
  }
}
