/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._od;

import de.monticore.cdbasis._ast.*;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._od.ODConstants.INDENT_PRINTER;
import static de.monticore.codegen.cd2java._od.ODConstants.REPORTING_REPOSITORY;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSER;

public class ODDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected ODService odService;

  protected VisitorService visitorService;

  protected MethodDecorator methodDecorator;

  public ODDecorator(final GlobalExtensionManagement glex,
                     final MethodDecorator methodDecorator,
                     final ODService odService,
                     final VisitorService visitorService) {
    super(glex);
    this.odService = odService;
    this.visitorService = visitorService;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String odName = odService.getODName(input.getCDDefinition());
    String visitorFullName = visitorService.getVisitor2FullName();
    String traverserFullName = visitorService.getTraverserInterfaceFullName();
    String handlerFullName = visitorService.getHandlerFullName();

    ASTCDAttribute printEmptyOptionalAttribute = createPrintEmptyOptionalAttribute();
    List<ASTCDMethod> printEmptyOptionalMethods = methodDecorator.decorate(printEmptyOptionalAttribute);

    ASTCDAttribute printEmptyListAttribute = createPrintEmptyListAttribute();
    List<ASTCDMethod> printEmptyListMethods = methodDecorator.decorate(printEmptyListAttribute);

    ASTCDAttribute traverserAttribute = createTraverserAttribute(traverserFullName);
    List<ASTCDMethod> traverserMethods = methodDecorator.decorate(traverserAttribute);

    return CD4CodeMill.cDClassBuilder()
        .setName(odName)
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder()
                .addInterface(getMCTypeFacade().createQualifiedType(visitorFullName))
                .addInterface(getMCTypeFacade().createQualifiedType(handlerFullName)).build())
        .addCDMember(createConstructor(odName))
        .addCDMember(traverserAttribute)
        .addCDMember(createIndentPrinterAttribute())
        .addCDMember(createReportingRepositoryAttribute())
        .addCDMember(printEmptyOptionalAttribute)
        .addCDMember(printEmptyListAttribute)
        .addAllCDMembers(createHandleMethods(input.getCDDefinition()))
        .addAllCDMembers(traverserMethods)
        .addCDMember(createPrintAttributeMethod())
        .addCDMember(createPrintObjectMethod())
        .addCDMember(createPrintObjectDiagramMethod())
        .addAllCDMembers(printEmptyOptionalMethods)
        .addAllCDMembers(printEmptyListMethods)
        .build();
  }

  protected ASTCDConstructor createConstructor(String odName) {
    ASTCDParameter printerParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(INDENT_PRINTER), "printer");
    ASTCDParameter reportingParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(REPORTING_REPOSITORY),
        "reporting");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), odName, printerParam, reportingParam);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("_od.ConstructorOD"));
    return constructor;
  }

  protected ASTCDAttribute createTraverserAttribute(String visitorName) {
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), visitorName, TRAVERSER);
    return attribute;
  }


  protected ASTCDAttribute createIndentPrinterAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), INDENT_PRINTER, "pp");
  }


  protected ASTCDAttribute createReportingRepositoryAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), REPORTING_REPOSITORY, "reporting");
  }


  protected ASTCDAttribute createPrintEmptyOptionalAttribute() {
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), getMCTypeFacade().createBooleanType(), "printEmptyOptional");
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= false;"));
    return attribute;
  }


  protected ASTCDAttribute createPrintEmptyListAttribute() {
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), getMCTypeFacade().createBooleanType(), "printEmptyList");
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= false;"));
    return attribute;
  }

  protected List<ASTCDMethod> createHandleMethods(ASTCDDefinition astcdDefinition) {
    List<ASTCDMethod> handleMethodList = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassesList()) {
      String astFullName = odService.getASTPackage() + "." + astcdClass.getName();
      ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, getMCTypeFacade().createQualifiedType(astFullName));
      replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint("_od.HandleOD", astcdClass, astFullName));
      handleMethodList.add(handleMethod);
    }
    return handleMethodList;
  }

  protected ASTCDMethod createPrintAttributeMethod() {
    ASTCDParameter nameParam = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "name");
    ASTCDParameter valueParam = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "value");
    ASTCDMethod printAttributeMethod = getCDMethodFacade().createMethod(PROTECTED.build(), "printAttribute", nameParam, valueParam);
    replaceTemplate(EMPTY_BODY, printAttributeMethod, new TemplateHookPoint("_od.PrintAttribute"));
    return printAttributeMethod;
  }

  protected ASTCDMethod createPrintObjectMethod() {
    ASTCDParameter nameParam = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "objName");
    ASTCDParameter valueParam = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "objType");
    ASTCDMethod printAttributeMethod = getCDMethodFacade().createMethod(PROTECTED.build(), "printObject", nameParam, valueParam);
    replaceTemplate(EMPTY_BODY, printAttributeMethod, new TemplateHookPoint("_od.PrintObject"));
    return printAttributeMethod;
  }

  protected ASTCDMethod createPrintObjectDiagramMethod() {
    ASTCDParameter nameParam = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "modelName");
    String languageInterfaceName = odService.getLanguageInterfaceName();
    ASTCDParameter valueParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(languageInterfaceName), "node");
    ASTCDMethod printAttributeMethod = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createStringType(),
        "printObjectDiagram", nameParam, valueParam);
    replaceTemplate(EMPTY_BODY, printAttributeMethod, new TemplateHookPoint("_od.PrintObjectDiagram"));
    return printAttributeMethod;
  }

}
