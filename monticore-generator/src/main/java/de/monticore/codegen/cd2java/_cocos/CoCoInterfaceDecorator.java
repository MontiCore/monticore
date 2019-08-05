package de.monticore.codegen.cd2java._cocos;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;

public class CoCoInterfaceDecorator extends AbstractDecorator<ASTCDDefinition, List<ASTCDInterface>> {

  private final CoCoService cocoService;

  private final ASTService astService;

  public CoCoInterfaceDecorator(final GlobalExtensionManagement glex, final CoCoService cocoService, final ASTService astService) {
    super(glex);
    this.cocoService = cocoService;
    this.astService = astService;
  }

  @Override
  public List<ASTCDInterface> decorate(ASTCDDefinition definition) {
    List<ASTCDInterface> cocoInterfaces = new ArrayList<>();
    cocoInterfaces.add(createCoCoInterface());

    cocoInterfaces.addAll(definition.getCDClassList().stream()
        .map(this::createCoCoInterface)
        .collect(Collectors.toList()));

    cocoInterfaces.addAll(definition.getCDInterfaceList().stream()
        .map(this::createCoCoInterface)
        .collect(Collectors.toList()));

    return cocoInterfaces;
  }

  protected ASTCDInterface createCoCoInterface() {
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(this.cocoService.getCoCoSimpleTypeName())
        .addCDMethod(createCheckMethod())
        .build();
  }

  protected ASTCDMethod createCheckMethod() {
    ASTType parameterType = astService.getASTBaseInterface();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(parameterType, "node");
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, CoCoConstants.CHECK, parameter);
  }

  protected ASTCDInterface createCoCoInterface(ASTCDType type) {
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(this.cocoService.getCoCoSimpleTypeName(type))
        .addCDMethod(createCheckMethod(type))
        .build();
  }

  protected ASTCDMethod createCheckMethod(ASTCDType cdType) {
    ASTType parameterType = astService.getASTType(cdType);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(parameterType, "node");
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, CoCoConstants.CHECK, parameter);
  }
}
