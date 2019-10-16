// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;

import java.util.Arrays;

/**
 * Class that helps with the creation of ASTMCTypes
 */

public class MCTypeFacade {

  private static final String PACKAGE_SEPARATOR = "\\.";

  private static MCTypeFacade MCTypeFacade;

  private MCTypeFacade() {
  }

  public static MCTypeFacade getInstance() {
    if (MCTypeFacade == null) {
      MCTypeFacade = new MCTypeFacade();
    }
    return MCTypeFacade;
  }

  /**
   * qualified type creation methods
   */

  public ASTMCQualifiedType createQualifiedType(final Class<?> clazz) {
    return createQualifiedType(clazz.getSimpleName());
  }

  public ASTMCQualifiedType createQualifiedType(final String name) {
    ASTMCQualifiedName qualName = MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(Arrays.asList(name.split(PACKAGE_SEPARATOR))).build();
    return MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(qualName).build();
  }

  /**
   * typeArgument creation methods
   */

  public ASTMCBasicTypeArgument createBasicTypeArgumentOf(String name) {
    return MCFullGenericTypesMill.mCBasicTypeArgumentBuilder()
        .setMCQualifiedType(createQualifiedType(name))
        .build();
  }

  public ASTMCWildcardTypeArgument createWildCardWithUpperBoundType(final Class<?> upperBound) {
    return createWildCardWithUpperBoundType(this.createQualifiedType(upperBound));
  }

  public ASTMCWildcardTypeArgument createWildCardWithUpperBoundType(final String upperBound) {
    return createWildCardWithUpperBoundType(this.createQualifiedType(upperBound));
  }

  public ASTMCWildcardTypeArgument createWildCardWithUpperBoundType(final ASTMCType upperBound) {
    return MCFullGenericTypesMill.mCWildcardTypeArgumentBuilder()
        .setUpperBound(upperBound)
        .build();
  }

  /**
   * optional type of ASTMCBasicTypeArgument
   */

  public ASTMCOptionalType createOptionalTypeOf(final String name) {
    return MCFullGenericTypesMill.mCOptionalTypeBuilder()
        .addMCTypeArgument(createBasicTypeArgumentOf(name))
        .build();
  }

  public ASTMCOptionalType createOptionalTypeOf(final Class<?> clazz) {
    return createOptionalTypeOf(clazz.getSimpleName());
  }

  public ASTMCOptionalType createOptionalTypeOf(final ASTMCType type) {
    return createOptionalTypeOf(MCFullGenericTypesHelper.printType(type));
  }

  /**
   * list types of ASTMCBasicTypeArgument
   */

  public ASTMCListType createListTypeOf(final String name) {
    return MCFullGenericTypesMill.mCListTypeBuilder()
        .addMCTypeArgument(createBasicTypeArgumentOf(name))
        .build();
  }

  public ASTMCListType createListTypeOf(final Class<?> clazz) {
    return createListTypeOf(clazz.getSimpleName());
  }

  public ASTMCListType createListTypeOf(final ASTMCType type) {
    return createListTypeOf(MCCollectionTypesHelper.printType(type));
  }

  /**
   * set types of ASTMCBasicTypeArgument
   */

  public ASTMCSetType createSetTypeOf(final String name) {
    return MCFullGenericTypesMill.mCSetTypeBuilder()
        .addMCTypeArgument(createBasicTypeArgumentOf(name))
        .build();
  }

  public ASTMCSetType createSetTypeOf(final Class<?> clazz) {
    return createSetTypeOf(clazz.getSimpleName());
  }

  public ASTMCSetType createSetTypeOf(final ASTMCType type) {
    return createSetTypeOf(MCCollectionTypesHelper.printType(type));
  }

  /**
   * collection types of ASTMCBasicTypeArgument
   */

  public ASTMCType createCollectionTypeOf(final String name) {
    return MCFullGenericTypesMill.mCBasicGenericTypeBuilder()
        .setNameList(Lists.newArrayList("Collection"))
        .setMCTypeArgumentList(Lists.newArrayList(createBasicTypeArgumentOf(name)))
        .build();
  }

  public ASTMCType createCollectionTypeOf(final Class<?> clazz) {
    return createCollectionTypeOf(clazz.getSimpleName());
  }

  public ASTMCType createCollectionTypeOf(final ASTMCType type) {
    return createCollectionTypeOf(MCCollectionTypesHelper.printType(type));
  }

  /**
   * map types of ASTMCBasicTypeArgument
   */

  public ASTMCMapType createMapTypeOf(final String firstType, final String secondType) {
    return MCFullGenericTypesMill.mCMapTypeBuilder()
        .setKey(createBasicTypeArgumentOf(firstType))
        .setValue(createBasicTypeArgumentOf(secondType))
        .build();
  }

  public ASTMCMapType createMapTypeOf(final Class<?> firstType, final Class<?> secondType) {
    return createMapTypeOf(firstType.getSimpleName(), secondType.getSimpleName());
  }

  public ASTMCMapType createMapTypeOf(final ASTMCType firstType, final ASTMCType secondType) {
    return createMapTypeOf(MCCollectionTypesHelper.printType(firstType), MCCollectionTypesHelper.printType(secondType));
  }

  /**
   * array types
   */

  public ASTMCArrayType createArrayType(final ASTMCType type, int dimension) {
    return MCFullGenericTypesMill.mCArrayTypeBuilder()
        .setMCType(type)
        .setDimensions(dimension)
        .build();
  }

  public ASTMCArrayType createArrayType(final Class<?> clazz, int dimension) {
    return createArrayType(clazz.getSimpleName(), dimension);
  }

  public ASTMCArrayType createArrayType(final String name, int dimension) {
    return createArrayType(this.createQualifiedType(name), dimension);
  }

  /**
   * primitive types
   */

  public ASTMCVoidType createVoidType() {
    return MCBasicTypesMill.mCVoidTypeBuilder()
        .build();
  }

  public ASTMCType createBooleanType() {
    return createPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
  }

  public boolean isBooleanType(ASTMCType type) {
    // TODO : was ist die beste Möglichkeit, um ein Boolen zu erkennen return type.deepEquals(createBooleanType());
    return type instanceof ASTMCPrimitiveType && ((ASTMCPrimitiveType) type).isBoolean();
  }

  public ASTMCType createIntType() {
    return createPrimitiveType(ASTConstantsMCBasicTypes.INT);
  }

  private ASTMCType createPrimitiveType(int constantsType) {
    return MCBasicTypesMill.mCPrimitiveTypeBuilder()
        .setPrimitive(constantsType)
        .build();
  }

  public ASTMCType createStringType() {
    return createQualifiedType("String");
  }
}
