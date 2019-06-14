package de.monticore.codegen.cd2java.factories;

import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccollectiontypes._ast.MCCollectionTypesMill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CDTypeBuilder {

  public enum Wildcard {
    NONE, EXTENDS, SUPER
  }

  private static final String PACKAGE_SEPARATOR = "\\.";

  private Wildcard wildcard = null;

  private List<String> qualifiedName = new ArrayList<>();

  private List<CDTypeBuilder> genericTypes = new ArrayList<>();

  private CDTypeBuilder() {}

  public static CDTypeBuilder newTypeBuilder() {
    return new CDTypeBuilder();
  }

  public ASTMCObjectType build() {
    return MCCollectionTypesMill.mcObjectTypeBuilder()
        .setNameList(qualifiedName)
        .setTypeArguments(this.genericTypes.isEmpty() ? null : createTypeArguments())
        .build();
  }

  private ASTMCTypeArgument createTypeArguments() {
    List<ASTMCTypeArgument> typeArguments = new ArrayList<>();
    for (CDTypeBuilder cdTypeBuilder : this.genericTypes) {
      if (cdTypeBuilder.wildcard == null) {
        typeArguments.add(cdTypeBuilder.build());
      }
      else {
        ASTWildcardTypeBuilder wildCardTypeBuilder = TypesMill.wildcardTypeBuilder();
        if (cdTypeBuilder.wildcard == Wildcard.EXTENDS)
          wildCardTypeBuilder.setUpperBound(TypesMill.simpleReferenceTypeBuilder().setNameList(cdTypeBuilder.qualifiedName).build());
        if (cdTypeBuilder.wildcard == Wildcard.SUPER)
          wildCardTypeBuilder.setLowerBound(TypesMill.simpleReferenceTypeBuilder().setNameList(cdTypeBuilder.qualifiedName).build());
        typeArguments.add(wildCardTypeBuilder.build());
      }
    }

    return TypesMill.typeArgumentsBuilder()
        .setTypeArgumentList(typeArguments)
        .build();
  }

  public CDTypeBuilder simpleName(final Class<?> clazz) {
    return qualifiedName(clazz.getSimpleName());
  }

  public CDTypeBuilder qualifiedName(final Class<?> clazz) {
    return qualifiedName(clazz.getCanonicalName().split(PACKAGE_SEPARATOR));
  }

  public CDTypeBuilder qualifiedName(final String... names) {
    return qualifiedName(Arrays.asList(names));
  }

  public CDTypeBuilder qualifiedName(final List<String> names) {
    this.qualifiedName = names;
    return this;
  }

  public CDTypeBuilder simpleGenericType(final Class<?> clazz) {
    return qualifiedGenericType(clazz.getSimpleName());
  }

  public CDTypeBuilder qualifiedGenericType(final Class<?> clazz) {
    return qualifiedGenericType(clazz.getCanonicalName().split(PACKAGE_SEPARATOR));
  }

  public CDTypeBuilder qualifiedGenericType(final String... names) {
    return qualifiedGenericType(Arrays.asList(names));
  }

  public CDTypeBuilder qualifiedGenericType(final List<String> names) {
    this.genericTypes.add(newTypeBuilder().qualifiedName(names));
    return this;
  }

  public CDTypeBuilder wildCardGenericType() {
    return wildCardGenericType(Wildcard.NONE, Collections.emptyList());
  }

  public CDTypeBuilder wildCardGenericType(final Wildcard wildcard, final Class<?> clazz) {
    CDTypeBuilder cdTypeBuilder = newTypeBuilder().simpleName(clazz);
    cdTypeBuilder.wildcard = wildcard;
    this.genericTypes.add(cdTypeBuilder);
    return this;
  }

  public CDTypeBuilder wildCardGenericType(final Wildcard wildcard, final List<String> names) {
    CDTypeBuilder cdTypeBuilder = newTypeBuilder().qualifiedName(names);
    cdTypeBuilder.wildcard = wildcard;
    this.genericTypes.add(cdTypeBuilder);
    return this;
  }
}
