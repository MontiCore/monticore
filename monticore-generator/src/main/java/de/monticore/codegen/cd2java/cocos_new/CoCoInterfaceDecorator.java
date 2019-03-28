package de.monticore.codegen.cd2java.cocos_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.cocos_new.CoCoConstants.CHECK;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;

public class CoCoInterfaceDecorator extends AbstractDecorator<ASTCDDefinition, List<ASTCDInterface>> {

  private final CoCoService cocoService;

  public CoCoInterfaceDecorator(final GlobalExtensionManagement glex, final CoCoService cocoService) {
    super(glex);
    this.cocoService = cocoService;
  }

  @Override
  public List<ASTCDInterface> decorate(ASTCDDefinition definition) {
    List<ASTCDInterface> cocoInterfaces = definition.getCDClassList().stream()
        .map(this::createCoCoInterface)
        .collect(Collectors.toList());

    cocoInterfaces.addAll(definition.getCDInterfaceList().stream()
        .map(this::createCoCoInterface)
        .collect(Collectors.toList()));

    return cocoInterfaces;
  }

  protected ASTCDInterface createCoCoInterface(ASTCDType type) {
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(this.cocoService.getCoCoSimpleTypeName(type))
        .addCDMethod(createCheckMethod(type))
        .build();
  }

  protected ASTCDMethod createCheckMethod(ASTCDType cdType) {
    ASTType parameterType = getCDTypeFactory().createSimpleReferenceType(cdType.getName());
    ASTCDParameter parameter = getCDParameterFactory().createParameter(parameterType, "node");
    return getCDMethodFactory().createMethod(PUBLIC_ABSTRACT, CHECK, parameter);
  }
}
