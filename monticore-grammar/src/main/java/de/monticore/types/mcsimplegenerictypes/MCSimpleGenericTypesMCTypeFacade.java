/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcsimplegenerictypes;

import com.google.common.collect.Lists;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;

/**
 * facade for creation of ASTMCTypes when simple generic types are supported.
 * Replaces al printType() using methods to use MCCustomTypeArguments instead
 */
public class MCSimpleGenericTypesMCTypeFacade extends MCTypeFacade {

  @Override
  public ASTMCListType createListTypeOf(final ASTMCType type) {
    return createListTypeOf(createTypeArgument(type));
  }

  @Override
  public ASTMCListType createListTypeOf(ASTMCTypeArgument type) {
    var b = MCSimpleGenericTypesMill.mCListTypeBuilder();
    b.setMCTypeArgument(type);
    return b.build();
  }

  @Override
  public ASTMCSetType createSetTypeOf(final ASTMCType type) {
    return createSetTypeOf(createTypeArgument(type));
  }

  @Override
  public ASTMCSetType createSetTypeOf(final ASTMCTypeArgument type) {
    var b = MCSimpleGenericTypesMill.mCSetTypeBuilder();
    b.setMCTypeArgument(type);
    return b.build();
  }

  @Override
  public ASTMCGenericType createCollectionTypeOf(final ASTMCType type) {
    var b = MCSimpleGenericTypesMill.mCBasicGenericTypeBuilder();
    b.setNamesList(Lists.newArrayList("Collection"));
    b.addMCTypeArgument(createTypeArgument(type));
    return b.build();
  }


  @Override
  public ASTMCOptionalType createOptionalTypeOf(final ASTMCType type) {
    return createOptionalTypeOf(createTypeArgument(type));
  }

  @Override
  public ASTMCOptionalType createOptionalTypeOf(final ASTMCTypeArgument type) {
    var b = MCSimpleGenericTypesMill.mCOptionalTypeBuilder();
    b.setMCTypeArgument(type);
    return b.build();
  }

  @Override
  public ASTMCMapType createMapTypeOf(final ASTMCType firstType, final ASTMCType secondType) {
    return createMapTypeOf(createTypeArgument(firstType), createTypeArgument(secondType));
  }

  @Override
  public ASTMCMapType createMapTypeOf(final ASTMCTypeArgument firstType, final ASTMCTypeArgument secondType) {
    return MCFullGenericTypesMill.mCMapTypeBuilder()
            .setKey(firstType)
            .setValue(secondType)
            .build();
  }

  public ASTMCTypeArgument createTypeArgument(final ASTMCType type) {
    return MCSimpleGenericTypesMill.mCCustomTypeArgumentBuilder().setMCType(type.deepClone()).build();
  }

  public static void initializeAsMCTypeFacade() {
    setInstance(new MCSimpleGenericTypesMCTypeFacade());
  }

  public static void deinitializeAsMCTypeFacade() {
    setInstance(null);
  }
}
