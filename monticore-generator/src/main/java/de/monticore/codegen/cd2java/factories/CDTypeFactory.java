package de.monticore.codegen.cd2java.factories;

import de.monticore.types.types._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.Optional;

public class CDTypeFactory {

  private static CDTypeFactory CDTypeFactory;

  private final CD4AnalysisParser parser;

  private CDTypeFactory() {
    this.parser = new CD4AnalysisParser();
  }

  public static CDTypeFactory getInstance() {
    if (CDTypeFactory == null) {
      CDTypeFactory = new CDTypeFactory();
    }
    return CDTypeFactory;
  }

  public ASTType createTypeByDefinition(final String typeSignature) {
    Optional<ASTType> type = Optional.empty();
    try {
      type = parser.parse_StringType(typeSignature);
    } catch (IOException e) {
      Log.error("Could not create Type '" + typeSignature + "'.", e);
    }
    if (!type.isPresent()) {
      Log.error("Could not create Type '" + typeSignature + "'.");
    }
    return type.get();
  }

  public ASTReferenceType createReferenceTypeByDefinition(final String typeSignature) {
    Optional<ASTReferenceType> type = Optional.empty();
    try {
      type = parser.parse_StringReferenceType(typeSignature);
    } catch (IOException e) {
      Log.error("Could not create ReferenceType '" + typeSignature + "'.", e);
    }
    if (!type.isPresent()) {
      Log.error("Could not create ReferenceType '" + typeSignature + "'.");
    }
    return type.get();
  }

  public ASTReturnType createVoidType() {
    return TypesMill.voidTypeBuilder()
        .build();
  }

  public ASTType createBooleanType() {
    return TypesMill.primitiveTypeBuilder()
        .setPrimitive(ASTConstantsTypes.BOOLEAN)
        .build();
  }
}
