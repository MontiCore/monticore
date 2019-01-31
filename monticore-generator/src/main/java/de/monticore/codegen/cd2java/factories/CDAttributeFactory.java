package de.monticore.codegen.cd2java.factories;

import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.Optional;

public class CDAttributeFactory {

  private static CDAttributeFactory cdAttributeFactory;

  private final CD4AnalysisParser parser;

  private CDAttributeFactory() {
    this.parser = new CD4AnalysisParser();
  }

  public static CDAttributeFactory getInstance() {
    if (cdAttributeFactory == null) {
      cdAttributeFactory = new CDAttributeFactory();
    }
    return cdAttributeFactory;
  }

  public ASTCDAttribute createAttributeByDefinition(final String signature) {
    Optional<ASTCDAttribute> attribute = Optional.empty();
    try {
      attribute = parser.parse_StringCDAttribute(signature);
    } catch (IOException e) {
      Log.error("Could not create CDAttribute with signature '" + signature + "'.", e);
    }
    if (!attribute.isPresent()) {
      Log.error("Could not create CDAttribute with signature '" + signature + "'.");
    }
    return attribute.get();
  }

  public ASTCDAttribute createProtectedAttribute(final ASTType type, final String name) {
    return CD4AnalysisMill.cDAttributeBuilder()
        .setModifier(ModifierBuilder.builder().Protected().build())
        .setType(type)
        .setName(name)
        .build();
  }

  public ASTCDAttribute createProtectedStaticAttribute(final ASTType type, final String name) {
    return CD4AnalysisMill.cDAttributeBuilder()
        .setModifier(ModifierBuilder.builder().Protected().Static().build())
        .setType(type)
        .setName(name)
        .build();
  }

}
