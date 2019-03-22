package de.monticore.codegen.cd2java.factories;

import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTValue;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import org.apache.commons.lang3.StringUtils;

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

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final ASTType type, final String name) {
    return CD4AnalysisMill.cDAttributeBuilder()
        .setModifier(modifier)
        .setType(type.deepClone())
        .setName(name)
        .build();
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final ASTType type, final String name, final String value) {
    Optional<ASTValue> astValue ;
    try {
      astValue = parser.parseValue(new StringReader(value));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_ATTRIBUTE_VALUE, value, e);
    }
    if (!astValue.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_ATTRIBUTE_VALUE, value);
    }
    return CD4AnalysisMill.cDAttributeBuilder()
        .setModifier(modifier)
        .setType(type.deepClone())
        .setName(name)
        .setValue(astValue.get())
        .build();
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final ASTType type) {
    return createAttribute(modifier, type, StringUtils.uncapitalize(TypesHelper.printType(type)));
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final String type, final String name) {
    return createAttribute(modifier, CDTypeFactory.getInstance().createSimpleReferenceType(type), name);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final String type) {
    return createAttribute(modifier, CDTypeFactory.getInstance().createSimpleReferenceType(type), StringUtils.uncapitalize(type));
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final Class<?> type, final String name) {
    return createAttribute(modifier, CDTypeFactory.getInstance().createSimpleReferenceType(type), name);
  }

  public ASTCDAttribute createAttribute(final ASTModifier modifier, final Class<?> type) {
    return createAttribute(modifier, CDTypeFactory.getInstance().createSimpleReferenceType(type), StringUtils.uncapitalize(type.getSimpleName()));
  }


  public ASTCDAttribute createAttribute(final CDModifier modifier, final ASTType type, final String name) {
    return createAttribute(modifier.build(), type, name);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final ASTType type) {
    return createAttribute(modifier.build(), type);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final String type, final String name) {
    return createAttribute(modifier.build(), type, name);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final String type) {
    return createAttribute(modifier.build(), type);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final Class<?> type, final String name) {
    return createAttribute(modifier.build(), type, name);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final Class<?> type) {
    return createAttribute(modifier.build(), type);
  }

  public ASTCDAttribute createAttribute(final CDModifier modifier, final ASTType type, final String name, final String value) {
    return createAttribute(modifier.build(), type, name, value);
  }
}
