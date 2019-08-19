package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

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

    return CD4AnalysisMill.cDClassBuilder()
        .setName(visitorService.getParentAwareVisitorSimpleTypeName())
        .setModifier(PUBLIC_ABSTRACT.build())
        .addInterface(visitorService.getVisitorReferenceType())
        .addCDAttribute(getParentAttribute(languageInterfaceName))
        .addCDMethod(getParentMethod(languageInterfaceName))
        .addCDMethod(getParentsMethod(languageInterfaceName))
        .addAllCDMethods(getTraversMethod(compilationUnit.getCDDefinition()))
        .build();
  }

  protected ASTCDAttribute getParentAttribute(String languageInterfaceName) {
    ASTMCType stackType = getCDTypeFacade().createTypeByDefinition("java.util.Stack<" + languageInterfaceName + ">");
    ASTCDAttribute parentsAttribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE_FINAL, stackType, PARENTS_ATTRIBUTE);
    this.replaceTemplate(VALUE, parentsAttribute, new StringHookPoint("= new java.util.Stack<>();"));
    return parentsAttribute;
  }

  protected ASTCDMethod getParentMethod(String languageInterfaceName) {
    ASTMCType type = getCDTypeFacade().createOptionalTypeOf(languageInterfaceName);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(type).build();
    ASTCDMethod getParentMethod = getCDMethodFacade().createMethod(PUBLIC, returnType, GET_PARENT_METHOD);
    this.replaceTemplate(EMPTY_BODY, getParentMethod, new TemplateHookPoint("_visitor.parentaware.GetParent", languageInterfaceName));
    return getParentMethod;
  }

  protected ASTCDMethod getParentsMethod(String languageInterfaceName) {
    ASTMCType type = getCDTypeFacade().createListTypeOf(languageInterfaceName);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(type).build();
    ASTCDMethod getParentsMethod = getCDMethodFacade().createMethod(PUBLIC, returnType, GET_PARENTS_METHOD);
    this.replaceTemplate(EMPTY_BODY, getParentsMethod, new StringHookPoint("return new java.util.ArrayList<>(parents);"));
    return getParentsMethod;
  }

  protected List<ASTCDMethod> getTraversMethod(ASTCDDefinition astcdDefinition) {
    List<ASTCDMethod> traverseMethods = astcdDefinition.getCDClassList()
        .stream()
        .map(c -> visitorService.getVisitorMethod(TRAVERSE, getCDTypeFacade().createQualifiedType(c.getName())))
        .collect(Collectors.toList());

    // add template
    String visitorSimpleTypeName = visitorService.getVisitorSimpleTypeName();
    traverseMethods.forEach(m -> replaceTemplate(EMPTY_BODY, m,
        new TemplateHookPoint("_visitor.parentaware.Travers", visitorSimpleTypeName)));

    return traverseMethods;
  }
}
