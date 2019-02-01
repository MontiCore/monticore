package de.monticore.codegen.cd2java.factories;

import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;

import java.io.IOException;
import java.io.StringReader;
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
    Optional<ASTCDAttribute> attribute;
    try {
      attribute = parser.parseCDAttribute(new StringReader(signature));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_ATTRIBUTE, signature, e);
    }
    if (!attribute.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_ATTRIBUTE, signature);
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
