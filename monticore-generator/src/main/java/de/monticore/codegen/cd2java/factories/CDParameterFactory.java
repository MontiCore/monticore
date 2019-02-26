package de.monticore.codegen.cd2java.factories;

import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CDParameterFactory {

  private static CDParameterFactory cdParameterFactory;

  private CDParameterFactory() {
  }

  public static CDParameterFactory getInstance() {
    if (cdParameterFactory == null) {
      cdParameterFactory = new CDParameterFactory();
    }
    return cdParameterFactory;
  }

  public ASTCDParameter createParameter(final ASTType type, final String name) {
    return CD4AnalysisMill.cDParameterBuilder()
        .setType(type)
        .setName(name)
        .build();
  }

  public ASTCDParameter createParameter(final ASTType type) {
    return createParameter(type, StringUtils.uncapitalize(TypesHelper.printType(type)));
  }

  public ASTCDParameter createParameter(final Class<?> type, final String name) {
    return createParameter(CDTypeFactory.getInstance().createSimpleReferenceType(type), name);
  }

  public ASTCDParameter createParameter(final Class<?> type) {
    return createParameter(CDTypeFactory.getInstance().createSimpleReferenceType(type), StringUtils.uncapitalize(type.getSimpleName()));
  }

  public ASTCDParameter createParameter(final ASTCDAttribute ast) {
    return createParameter(ast.getType(), ast.getName());
  }

  public List<ASTCDParameter> createParameters(final ASTCDAttribute... attributes) {
    return Stream.of(attributes)
        .map(this::createParameter)
        .collect(Collectors.toList());
  }

  public List<ASTCDParameter> createParameters(final List<ASTCDAttribute> attributes) {
    return attributes.stream()
        .map(this::createParameter)
        .collect(Collectors.toList());
  }
}
