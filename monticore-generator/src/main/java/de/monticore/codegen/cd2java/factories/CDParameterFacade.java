package de.monticore.codegen.cd2java.factories;

import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CDParameterFacade {

  private static CDParameterFacade cdParameterFacade;

  private CDParameterFacade() {
  }

  public static CDParameterFacade getInstance() {
    if (cdParameterFacade == null) {
      cdParameterFacade = new CDParameterFacade();
    }
    return cdParameterFacade;
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
    return createParameter(CDTypeFacade.getInstance().createSimpleReferenceType(type), name);
  }

  public ASTCDParameter createParameter(final Class<?> type) {
    return createParameter(CDTypeFacade.getInstance().createSimpleReferenceType(type), StringUtils.uncapitalize(type.getSimpleName()));
  }

  public ASTCDParameter createParameter(final ASTCDAttribute ast) {
    return createParameter(ast.getType().deepClone(), ast.getName());
  }

  public List<ASTCDParameter> createParameters(final ASTCDAttribute... attributes) {
    return createParameters(Arrays.asList(attributes));
  }

  public List<ASTCDParameter> createParameters(final List<ASTCDAttribute> attributes) {
    return attributes.stream()
        .map(this::createParameter)
        .collect(Collectors.toList());
  }
}
