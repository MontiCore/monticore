/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.factories;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.cd.cd4code._parser.CD4CodeParser;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.MCFullGenericTypesHelper;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Optional;

public class CDTypeFacade {

  private static final String PACKAGE_SEPARATOR = "\\.";

  private static CDTypeFacade cdTypeFacade;

  private final CD4CodeParser parser;

  private CDTypeFacade() {
    this.parser = new CD4CodeParser();
  }

  public static CDTypeFacade getInstance() {
    if (cdTypeFacade == null) {
      cdTypeFacade = new CDTypeFacade();
    }
    return cdTypeFacade;
  }

  public ASTMCType createTypeByDefinition(final String typeSignature) {
    Optional<ASTMCType> type;
    try {
      type = parser.parseMCType(new StringReader(typeSignature));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature, e);
    }

    if (!type.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature);
    }

    return type.get();
  }

  public ASTMCQualifiedType createReferenceTypeByDefinition(final String typeSignature) {
    Optional<ASTMCQualifiedType> type;
    try {
      type = parser.parseMCQualifiedType(new StringReader(typeSignature));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature, e);
    }

    if (!type.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature);
    }

    return type.get();
  }

  public ASTMCQualifiedType createQualifiedType(final Class<?> clazz) {
    return createQualifiedType(clazz.getSimpleName());
  }

  public ASTMCQualifiedType createQualifiedType(final String name) {
    ASTMCQualifiedName qualName = MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(Arrays.asList(name.split(PACKAGE_SEPARATOR))).build();
    return MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(qualName).build();
  }

  public ASTMCObjectType createOptionalTypeOf(final Class<?> clazz) {
    return createOptionalTypeOf(clazz.getSimpleName());
  }

  public ASTMCOptionalType createOptionalTypeOf(final String name) {
    ASTMCTypeArgument arg = CD4CodeMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(createQualifiedType(name)).build();
    return CD4CodeMill.mCOptionalTypeBuilder().addMCTypeArgument(arg).build();
  }

  public ASTMCOptionalType createOptionalTypeOf(final ASTMCType type) {
    return createOptionalTypeOf(MCFullGenericTypesHelper.printType(type));
  }

  public ASTMCOptionalType createOptionalTypeOf(final ASTMCTypeArgument type) {
    return createOptionalTypeOf(MCFullGenericTypesHelper.printType(type));
  }

  public ASTMCListType createListTypeOf(final Class<?> clazz) {
    return createListTypeOf(clazz.getSimpleName());
  }

  public ASTMCListType createListTypeOf(final String name) {
    Optional<ASTMCTypeArgument> arg = null;
    try {
      arg = new CD4CodeParser().parse_StringMCTypeArgument(name);
    } catch (IOException e) {
      Log.error("0xA0115 Cannot generate TypeArgument for " + name);
    }
    if (!arg.isPresent()) {
      Log.error("0xA0116 Cannot generate TypeArgument for " + name);
    }
    return CD4CodeMill.mCListTypeBuilder().addMCTypeArgument(arg.get()).build();
  }

  public ASTMCListType createListTypeOf(final ASTMCType type) {
    return createListTypeOf(MCCollectionTypesHelper.printType(type));
  }

  public ASTMCSetType createSetTypeOf(final Class<?> clazz) {
    return createSetTypeOf(clazz.getSimpleName());
  }

  public ASTMCSetType createSetTypeOf(final String name) {
    Optional<ASTMCTypeArgument> arg = null;
    try {
      arg = new CD4CodeParser().parse_StringMCTypeArgument(name);
    } catch (IOException e) {
      Log.error("0xA0115 Cannot generate TypeArgument for " + name);
    }
    if (!arg.isPresent()) {
      Log.error("0xA0116 Cannot generate TypeArgument for " + name);
    }
    return CD4CodeMill.mCSetTypeBuilder().addMCTypeArgument(arg.get()).build();
  }

  public ASTMCSetType createSetTypeOf(final ASTMCType type) {
    return createSetTypeOf(MCCollectionTypesHelper.printType(type));
  }

  public ASTMCType createCollectionTypeOf(final Class<?> clazz) {
    return createCollectionTypeOf(clazz.getSimpleName());
  }

  public ASTMCType createCollectionTypeOf(final String name) {
    ASTMCTypeArgument arg = CD4CodeMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(createQualifiedType(name)).build();
    // TODO Ersetze CD4Code durch was besseres
    return CD4CodeMill.mCBasicGenericTypeBuilder().setNameList(Lists.newArrayList("Collection")).setMCTypeArgumentList(Lists.newArrayList(arg)).build();
  }

  public ASTMCType createCollectionTypeOf(final ASTMCType type) {
    return createCollectionTypeOf(MCCollectionTypesHelper.printType(type));
  }

  public ASTMCMapType createMapTypeOf(final String firstType, final String secondType) {
    ASTMCTypeArgument first = CD4CodeMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(createQualifiedType(firstType)).build();
    ASTMCTypeArgument second = CD4CodeMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(createQualifiedType(secondType)).build();
    return CD4CodeMill.mCMapTypeBuilder().setKey(first).setValue(second).build();
  }

  public ASTMCArrayType createArrayType(final Class<?> clazz, int dimension) {
    return createArrayType(clazz.getSimpleName(), dimension);
  }

  public ASTMCArrayType createArrayType(final String name, int dimension) {
    return createArrayType(this.createQualifiedType(name), dimension);
  }

  public ASTMCArrayType createArrayType(final ASTMCType type, int dimension) {
    return MCFullGenericTypesMill.mCArrayTypeBuilder()
        .setMCType(type)
        .setDimensions(dimension)
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
