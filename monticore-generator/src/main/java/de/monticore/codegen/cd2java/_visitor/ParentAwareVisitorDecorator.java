/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class ParentAwareVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {


  private final VisitorService visitorService;

  public ParentAwareVisitorDecorator(final GlobalExtensionManagement glex,
                                     final VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    ASTCDCompilationUnit compilationUnit = visitorService.calculateCDTypeNamesWithPackage(input);
    String languageInterfaceName = visitorService.getLanguageInterfaceName();

    return CD4CodeMill.cDClassBuilder()
        .setName(visitorService.getParentAwareVisitorSimpleName())
        .setModifier(PUBLIC_ABSTRACT.build())
        .addInterface(visitorService.getVisitorType())
        .addCDAttribute(getParentAttribute(languageInterfaceName))
        .addCDMethod(getParentMethod(languageInterfaceName))
        .addCDMethod(getParentsMethod(languageInterfaceName))
        .addAllCDMethods(getTraversMethod(compilationUnit.getCDDefinition().getCDClassList()))
        .build();
  }

  protected ASTCDAttribute getParentAttribute(String languageInterfaceName) {
    ASTMCType stackType = getMCTypeFacade().createBasicGenericTypeOf("java.util.Stack", languageInterfaceName);
    ASTCDAttribute parentsAttribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE_FINAL, stackType, PARENTS_ATTRIBUTE);
    this.replaceTemplate(VALUE, parentsAttribute, new StringHookPoint("= new java.util.Stack<>();"));
    return parentsAttribute;
  }

  protected ASTCDMethod getParentMethod(String languageInterfaceName) {
    ASTMCType type = getMCTypeFacade().createOptionalTypeOf(languageInterfaceName);
    ASTCDMethod getParentMethod = getCDMethodFacade().createMethod(PUBLIC, type, GET_PARENT_METHOD);
    this.replaceTemplate(EMPTY_BODY, getParentMethod, new TemplateHookPoint(GET_PARENT_PAREENTAWARE_TEMPLATE, languageInterfaceName));
    return getParentMethod;
  }

  protected ASTCDMethod getParentsMethod(String languageInterfaceName) {
    ASTMCType type = getMCTypeFacade().createListTypeOf(languageInterfaceName);
    ASTCDMethod getParentsMethod = getCDMethodFacade().createMethod(PUBLIC, type, GET_PARENTS_METHOD);
    this.replaceTemplate(EMPTY_BODY, getParentsMethod, new StringHookPoint("return new java.util.ArrayList<>(parents);"));
    return getParentsMethod;
  }

  protected List<ASTCDMethod> getTraversMethod(List<ASTCDClass> astcdClasses) {
    // only add travers method for non abstract classes
    List<ASTCDMethod> traverseMethods = astcdClasses
        .stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(c -> !c.getModifier().isAbstract())
        .map(c -> visitorService.getVisitorMethod(TRAVERSE, getMCTypeFacade().createQualifiedType(c.getName())))
        .collect(Collectors.toList());

    // add template
    String visitorSimpleTypeName = visitorService.getVisitorSimpleName();
    traverseMethods.forEach(m -> replaceTemplate(EMPTY_BODY, m,
        new TemplateHookPoint(TRAVERSE_PAREENTAWARE_TEMPLATE, visitorSimpleTypeName)));

    return traverseMethods;
  }
}
