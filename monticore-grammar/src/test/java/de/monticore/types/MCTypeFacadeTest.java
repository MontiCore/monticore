/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import com.google.common.collect.Lists;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MCTypeFacadeTest {

  private MCTypeFacade mcTypeFacade;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    this.mcTypeFacade = MCTypeFacade.getInstance();
  }

  @Test
  public void testCreateQualifiedTypeName() {
    ASTMCQualifiedType qualifiedType = mcTypeFacade.createQualifiedType("a.b.c.Foo");
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), qualifiedType.getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateQualifiedTypeClass() {
    ASTMCQualifiedType qualifiedType = mcTypeFacade.createQualifiedType(java.lang.String.class);
    Assertions.assertEquals(Lists.newArrayList("String"), qualifiedType.getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateBasicTypeArgumentOf() {
    ASTMCBasicTypeArgument type = mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo");
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), type.getMCQualifiedType().getNameList());
    Assertions.assertTrue(type.getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeOpt().get()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateWildCardWithNoBounds() {
    ASTMCWildcardTypeArgument type = mcTypeFacade.createWildCardWithNoBounds();
    Assertions.assertFalse(type.isPresentLowerBound());
    Assertions.assertFalse(type.isPresentUpperBound());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateWildCardWithUpperBoundTypeClass() {
    ASTMCWildcardTypeArgument type = mcTypeFacade.createWildCardWithUpperBoundType(String.class);
    Assertions.assertFalse(type.isPresentLowerBound());
    Assertions.assertTrue(type.isPresentUpperBound());
    Assertions.assertTrue(type.getUpperBound() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getUpperBound()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testCreateWildCardWithUpperBoundTypeName() {
    ASTMCWildcardTypeArgument type = mcTypeFacade.createWildCardWithUpperBoundType("a.b.c.Foo");
    Assertions.assertFalse(type.isPresentLowerBound());
    Assertions.assertTrue(type.isPresentUpperBound());
    Assertions.assertTrue(type.getUpperBound() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getUpperBound()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateWildCardWithUpperBoundTypeType() {
    ASTMCWildcardTypeArgument type = mcTypeFacade.createWildCardWithUpperBoundType(mcTypeFacade.createQualifiedType("a.b.c.Foo"));
    Assertions.assertFalse(type.isPresentLowerBound());
    Assertions.assertTrue(type.isPresentUpperBound());
    Assertions.assertTrue(type.getUpperBound() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getUpperBound()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateOptionalTypeOfClass() {
    ASTMCOptionalType type = mcTypeFacade.createOptionalTypeOf(String.class);
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Optional", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testCreateOptionalTypeOfName() {
    ASTMCOptionalType type = mcTypeFacade.createOptionalTypeOf("a.b.c.Foo");
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Optional", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateOptionalTypeOfType() {
    ASTMCOptionalType type = mcTypeFacade.createOptionalTypeOf(mcTypeFacade.createQualifiedType("a.b.c.Foo"));
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Optional", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateOptionalTypeOfTypeArgument() {
    ASTMCOptionalType type = mcTypeFacade.createOptionalTypeOf(mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo"));
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Optional", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateListTypeOfClass() {
    ASTMCListType type = mcTypeFacade.createListTypeOf(String.class);
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("List", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testCreateListTypeOfName() {
    ASTMCListType type = mcTypeFacade.createListTypeOf("a.b.c.Foo");
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("List", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateListTypeOfType() {
    ASTMCListType type = mcTypeFacade.createListTypeOf(mcTypeFacade.createQualifiedType("a.b.c.Foo"));
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("List", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateListTypeOfTypeArgument() {
    ASTMCListType type = mcTypeFacade.createListTypeOf(mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo"));
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("List", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateSetTypeOfClass() {
    ASTMCSetType type = mcTypeFacade.createSetTypeOf(String.class);
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Set", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testCreateSetTypeOfName() {
    ASTMCSetType type = mcTypeFacade.createSetTypeOf("a.b.c.Foo");
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Set", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateSetTypeOfType() {
    ASTMCSetType type = mcTypeFacade.createSetTypeOf(mcTypeFacade.createQualifiedType("a.b.c.Foo"));
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Set", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateSetTypeOfTypeArgument() {
    ASTMCSetType type = mcTypeFacade.createSetTypeOf(mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo"));
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Set", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateCollectionTypeOfClass() {
    ASTMCGenericType type = mcTypeFacade.createCollectionTypeOf(String.class);
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Collection", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testCreateCollectionTypeOfName() {
    ASTMCGenericType type = mcTypeFacade.createCollectionTypeOf("a.b.c.Foo");
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Collection", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateCollectionTypeOfType() {
    ASTMCGenericType type = mcTypeFacade.createCollectionTypeOf(mcTypeFacade.createQualifiedType("a.b.c.Foo"));
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Collection", type.getName(0));
    Assertions.assertEquals(1, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateMapTypeOfClass() {
    ASTMCMapType type = mcTypeFacade.createMapTypeOf(String.class, Integer.class);
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Map", type.getName(0));
    Assertions.assertEquals(2, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getKey().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getKey().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type.getKey().getMCTypeOpt().get()).getNameList());

    Assertions.assertTrue(type.getValue().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getValue().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("Integer"), ((ASTMCQualifiedType) type.getValue().getMCTypeOpt().get()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testCreateMapTypeOfName() {
    ASTMCMapType type = mcTypeFacade.createMapTypeOf("a.b.c.Foo", "d.e.f.Bla");
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Map", type.getName(0));
    Assertions.assertEquals(2, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getKey().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getKey().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getKey().getMCTypeOpt().get()).getNameList());

    Assertions.assertTrue(type.getValue().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getValue().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getValue().getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateMapTypeOfType() {
    ASTMCMapType type = mcTypeFacade.createMapTypeOf(mcTypeFacade.createQualifiedType("a.b.c.Foo"), mcTypeFacade.createQualifiedType("d.e.f.Bla"));
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Map", type.getName(0));
    Assertions.assertEquals(2, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getKey().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getKey().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getKey().getMCTypeOpt().get()).getNameList());

    Assertions.assertTrue(type.getValue().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getValue().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getValue().getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateMapTypeOfTypeArgument() {
    ASTMCMapType type = mcTypeFacade.createMapTypeOf(mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo"), mcTypeFacade.createBasicTypeArgumentOf("d.e.f.Bla"));
    Assertions.assertEquals(1, type.sizeNames());
    Assertions.assertEquals("Map", type.getName(0));
    Assertions.assertEquals(2, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getKey().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getKey().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getKey().getMCTypeOpt().get()).getNameList());

    Assertions.assertTrue(type.getValue().getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getValue().getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getValue().getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testCreateBasicGenericTypeOfNameList() {
    ASTMCBasicTypeArgument basicTypeArgumentOfFoo = mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo");
    ASTMCBasicTypeArgument basicTypeArgumentOfBla = mcTypeFacade.createBasicTypeArgumentOf("d.e.f.Bla");

    ASTMCBasicGenericType type = mcTypeFacade.createBasicGenericTypeOf(
        Lists.newArrayList("my", "special", "GenericType"), Lists.newArrayList(basicTypeArgumentOfFoo, basicTypeArgumentOfBla));
    Assertions.assertEquals(3, type.sizeNames());
    Assertions.assertEquals(Lists.newArrayList("my","special","GenericType"), type.getNameList());
    Assertions.assertEquals(2, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());

    Assertions.assertTrue(type.getMCTypeArgument(1).getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument(1).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getMCTypeArgument(1).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testCreateBasicGenericTypeOfArgumentList() {
    ASTMCBasicTypeArgument basicTypeArgumentOfFoo = mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo");
    ASTMCBasicTypeArgument basicTypeArgumentOfBla = mcTypeFacade.createBasicTypeArgumentOf("d.e.f.Bla");

    ASTMCBasicGenericType type = mcTypeFacade.createBasicGenericTypeOf(
        "my.special.GenericType", Lists.newArrayList(basicTypeArgumentOfFoo, basicTypeArgumentOfBla));
    Assertions.assertEquals(3, type.sizeNames());
    Assertions.assertEquals(Lists.newArrayList("my","special","GenericType"), type.getNameList());
    Assertions.assertEquals(2, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());

    Assertions.assertTrue(type.getMCTypeArgument(1).getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument(1).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getMCTypeArgument(1).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateBasicGenericTypeOfArgumentVarArgs() {
    ASTMCBasicTypeArgument basicTypeArgumentOfFoo = mcTypeFacade.createBasicTypeArgumentOf("a.b.c.Foo");
    ASTMCBasicTypeArgument basicTypeArgumentOfBla = mcTypeFacade.createBasicTypeArgumentOf("d.e.f.Bla");

    ASTMCBasicGenericType type = mcTypeFacade.createBasicGenericTypeOf(
        "my.special.GenericType", basicTypeArgumentOfFoo, basicTypeArgumentOfBla);
    Assertions.assertEquals(3, type.sizeNames());
    Assertions.assertEquals(Lists.newArrayList("my","special","GenericType"), type.getNameList());
    Assertions.assertEquals(2, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());

    Assertions.assertTrue(type.getMCTypeArgument(1).getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument(1).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getMCTypeArgument(1).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateBasicGenericTypeOfArgumentString() {
    ASTMCBasicGenericType type = mcTypeFacade.createBasicGenericTypeOf(
        "my.special.GenericType", "a.b.c.Foo", "d.e.f.Bla");
    Assertions.assertEquals(3, type.sizeNames());
    Assertions.assertEquals(Lists.newArrayList("my","special","GenericType"), type.getNameList());
    Assertions.assertEquals(2, type.sizeMCTypeArguments());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument(0).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("a", "b", "c", "Foo"), ((ASTMCQualifiedType) type.getMCTypeArgument(0).getMCTypeOpt().get()).getNameList());

    Assertions.assertTrue(type.getMCTypeArgument(1).getMCTypeOpt().isPresent());
    Assertions.assertTrue(type.getMCTypeArgument(1).getMCTypeOpt().get() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("d", "e", "f", "Bla"), ((ASTMCQualifiedType) type.getMCTypeArgument(1).getMCTypeOpt().get()).getNameList());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateArrayTypeString() {
    ASTMCArrayType type = mcTypeFacade.createArrayType("int", 3);
    Assertions.assertEquals(3, type.getDimensions());
    Assertions.assertTrue(type.getMCType() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("int"), ((ASTMCQualifiedType) type.getMCType()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateArrayTypeClass() {
    ASTMCArrayType type = mcTypeFacade.createArrayType(Integer.class, 3);
    Assertions.assertEquals(3, type.getDimensions());
    Assertions.assertTrue(type.getMCType() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("Integer"), ((ASTMCQualifiedType) type.getMCType()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateArrayTypeType() {
    ASTMCArrayType type = mcTypeFacade.createArrayType(mcTypeFacade.createQualifiedType("int"), 3);
    Assertions.assertEquals(3, type.getDimensions());
    Assertions.assertTrue(type.getMCType() instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("int"), ((ASTMCQualifiedType) type.getMCType()).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateVoidType() {
    ASTMCVoidType type = mcTypeFacade.createVoidType();
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateBooleanType() {
    ASTMCPrimitiveType type = mcTypeFacade.createBooleanType();
    Assertions.assertTrue(type.isBoolean());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsBooleanType() {
    ASTMCPrimitiveType booleanType = mcTypeFacade.createBooleanType();
    Assertions.assertTrue(mcTypeFacade.isBooleanType(booleanType));

    ASTMCType stringType = mcTypeFacade.createStringType();
    Assertions.assertFalse(mcTypeFacade.isBooleanType(stringType));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreateIntType() {
    ASTMCPrimitiveType type = mcTypeFacade.createIntType();
    Assertions.assertTrue(type.isInt());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCreatePrimitiveType() {
    ASTMCType type = mcTypeFacade.createStringType();
    Assertions.assertTrue(type instanceof ASTMCQualifiedType);
    Assertions.assertEquals(Lists.newArrayList("String"), ((ASTMCQualifiedType) type).getNameList());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
