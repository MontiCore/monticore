/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE_INHERITANCE_TEMPLATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

/**
 * creates a InheritanceVisitor class from a grammar
 */
public class InheritanceVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final VisitorService visitorService;

  public InheritanceVisitorDecorator(final GlobalExtensionManagement glex,
                                     final VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    ASTCDCompilationUnit compilationUnit = visitorService.calculateCDTypeNamesWithPackage(input);
    String languageInterfaceName = visitorService.getLanguageInterfaceName();

    return CD4CodeMill.cDInterfaceBuilder()
        .setName(visitorService.getInheritanceVisitorSimpleName())
        .setModifier(PUBLIC.build())
        .addInterface(visitorService.getVisitorType())
        .addAllInterfaces(visitorService.getSuperInheritanceVisitors())
        .addAllCDMethods(getHandleMethods(compilationUnit.getCDDefinition(), languageInterfaceName))
        .build();
  }

  protected List<ASTCDMethod> getHandleMethods(ASTCDDefinition astcdDefinition, String languageInterfaceName) {
    String visitorSimpleTypeName = visitorService.getVisitorSimpleName();

    List<ASTCDMethod> handleMethods = new ArrayList<>();
    handleMethods.addAll(astcdDefinition.getCDClassList()
        .stream()
        .map(c -> getHandleMethod(c, languageInterfaceName, visitorSimpleTypeName))
        .collect(Collectors.toList()));

    handleMethods.addAll(astcdDefinition.getCDInterfaceList()
        .stream()
        .map(c -> getHandleMethod(c, languageInterfaceName, visitorSimpleTypeName))
        .collect(Collectors.toList()));

    return handleMethods;
  }

  protected ASTCDMethod getHandleMethod(ASTCDClass astcdClass, String languageInterfaceName, String visitorSimpleTypeName) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, getCDTypeFacade().createQualifiedType(astcdClass.getName()));
    List<String> superTypeList = new ArrayList<>();
    // super classes
    if (astcdClass.isPresentSuperclass() && !astcdClass.printSuperClass().isEmpty()) {
      superTypeList= visitorService.getAllSuperClassesTransitive(astcdClass);
    }
    // super interfaces
    superTypeList.addAll(visitorService.getAllSuperInterfacesTransitive(astcdClass));
    replaceTemplate(EMPTY_BODY, handleMethod,
        new TemplateHookPoint(HANDLE_INHERITANCE_TEMPLATE,
            languageInterfaceName, visitorSimpleTypeName, superTypeList));
    return handleMethod;
  }

  protected ASTCDMethod getHandleMethod(ASTCDInterface astcdInterface, String languageInterfaceName, String visitorSimpleTypeName) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, getCDTypeFacade().createQualifiedType(astcdInterface.getName()));
    replaceTemplate(EMPTY_BODY, handleMethod,
        new TemplateHookPoint(HANDLE_INHERITANCE_TEMPLATE,
            languageInterfaceName, visitorSimpleTypeName, new ArrayList<>()));
    return handleMethod;
  }
}
