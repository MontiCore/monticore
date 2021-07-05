/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.ASTE_PACKAGE;

/**
 * creates CoCo interfaces with a abstract check method for AST classes and interfaces
 */
public class CoCoInterfaceDecorator extends AbstractCreator<ASTCDDefinition, List<ASTCDInterface>> {

  protected final CoCoService cocoService;

  protected final ASTService astService;

  public CoCoInterfaceDecorator(final GlobalExtensionManagement glex, final CoCoService cocoService, final ASTService astService) {
    super(glex);
    this.cocoService = cocoService;
    this.astService = astService;
  }

  @Override
  public List<ASTCDInterface> decorate(ASTCDDefinition definition) {
    List<ASTCDInterface> cocoInterfaces = new ArrayList<>();
    String visitorName = new VisitorService(astService.getCDSymbol()).getVisitor2FullName();
    cocoInterfaces.addAll(definition.getCDClassesList().stream()
        .map(c -> createCoCoInterface(c, visitorName))
        .collect(Collectors.toList()));

    cocoInterfaces.addAll(definition.getCDInterfacesList().stream()
        .map(i -> createCoCoInterface(i, visitorName))
        .collect(Collectors.toList()));

    return cocoInterfaces;
  }

  protected ASTCDInterface createCoCoInterface(ASTCDType type, String visitorName) {
    ASTModifier modifier = cocoService.createModifierPublicModifier(type.getModifier());
    ASTMCQualifiedType interfaceType = getMCTypeFacade().createQualifiedType(visitorName);
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setModifier(modifier)
        .setName(this.cocoService.getCoCoSimpleTypeName(type))
        .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addSuperclass(interfaceType).build())
        .addCDMember(createCheckMethod(type))
        .addCDMember(createVisitMethod(type))
        .build();
  }

  protected ASTCDMethod createCheckMethod(ASTCDType cdType) {
    ASTMCType parameterType = astService.getASTType(cdType);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(parameterType, "node");
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT.build(), CoCoConstants.CHECK, parameter);
  }

  protected ASTCDMethod createVisitMethod(ASTCDType cdType) {
    ASTMCType parameterType = astService.getASTType(cdType);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(parameterType, "node");
    ASTCDMethod visitMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "visit", parameter);
    this.replaceTemplate(EMPTY_BODY, visitMethod, new StringHookPoint("check(node);"));
    return visitMethod;
  }
}
