/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.ast_class;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd.facade.CDModifier;
import de.monticore.codegen.cd2java._ast.ast_class.ASTDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;

/**
 * extension of the ASTDecorator with additional EMF functionality
 */
public class ASTEmfDecorator extends ASTDecorator {

  protected final EmfService emfService;

  public ASTEmfDecorator(final GlobalExtensionManagement glex,
                         final ASTService astService,
                         final VisitorService visitorService,
                         final ASTSymbolDecorator symbolDecorator,
                         final ASTScopeDecorator scopeDecorator,
                         final MethodDecorator methodDecorator,
                         final SymbolTableService symbolTableService,
                         final EmfService emfService) {
    super(glex, astService, visitorService,  symbolDecorator,
        scopeDecorator, methodDecorator, symbolTableService);
    this.emfService = emfService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass originalClass, ASTCDClass changedClass) {
    if (!changedClass.isPresentCDInterfaceUsage()) {
      changedClass.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
    }
    changedClass.getCDInterfaceUsage().addInterface(this.astService.getASTBaseInterface());
    // have to use the changed one here because this one will get the TOP prefix
    changedClass.addCDMember(createAcceptTraverserMethod(changedClass));
    changedClass.addCDMember(createEvaluateInterpreterMethod(changedClass));
    changedClass.addAllCDMembers(createAcceptTraverserSuperMethods(changedClass));
    //changedClass.addAllCDMembers(createEvaluateInterpreterSuperMethods(changedClass));
    changedClass.addCDMember(getConstructMethod(originalClass));
    changedClass.addAllCDMembers(createEMethods(originalClass));

    if (!originalClass.isPresentCDExtendUsage()) {
      changedClass.setCDExtendUsage(
              CD4CodeMill.cDExtendUsageBuilder().addSuperclass(this.getMCTypeFacade().createQualifiedType(AST_EC_NODE)).build());
    }

    List<ASTCDAttribute> symbolAttributes = symbolDecorator.decorate(originalClass);
    addSymbolTableMethods(symbolAttributes, changedClass);

    List<ASTCDAttribute> scopeAttributes = scopeDecorator.decorate(originalClass);
    addSymbolTableMethods(scopeAttributes, changedClass);

    return changedClass;
  }

  public List<ASTCDMethod> createEMethods(ASTCDClass astcdClass) {
    // with inherited attributes
    List<ASTCDAttribute> copiedAttributeList = astcdClass.getCDAttributeList();

    String packageName = astService.getCDName() + PACKAGE_SUFFIX;
    String className = astcdClass.getName();
    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.add(createEGetMethod(copiedAttributeList, packageName, className));
    methodList.add(createESetMethod(copiedAttributeList, packageName, className));
    methodList.add(createEUnsetMethod(copiedAttributeList, packageName, className));
    methodList.add(createEIsSetMethod(copiedAttributeList, packageName, className));
    methodList.add(createEBaseStructuralFeatureIDMethod());
    methodList.add(createEDerivedStructuralFeatureIDMethod());
    methodList.add(creatEStaticClassMethod(packageName, className));

    if (astcdClass.getCDMethodList().stream().noneMatch(x -> "toString".equals(x.getName()))) {
      methodList.add(createEToStringMethod(copiedAttributeList));
    }
    return methodList;
  }

  public ASTCDMethod createEGetMethod(List<ASTCDAttribute> astcdAttributes, String packageName, String className) {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter resolveParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), "resolve");
    ASTCDParameter coreTypeParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), "coreType");
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC.build(), getMCTypeFacade().createQualifiedType(Object.class), E_GET,
        featureParameter, resolveParameter, coreTypeParameter);
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EGet", astcdAttributes, packageName, className));
    return method;
  }

  public ASTCDMethod createESetMethod(List<ASTCDAttribute> astcdAttributes, String packageName, String className) {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter newValueParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(Object.class), "newValue");

    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC.build(), E_SET, featureParameter, newValueParameter);
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.ESet", astcdAttributes, packageName, className));
    return method;
  }

  public ASTCDMethod createEUnsetMethod(List<ASTCDAttribute> astcdAttributes, String packageName, String className) {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createIntType(), FEATURE_ID);

    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC.build(), E_UNSET, featureParameter);
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EUnset", astcdAttributes, packageName, className));
    return method;
  }

  public ASTCDMethod createEIsSetMethod(List<ASTCDAttribute> astcdAttributes, String packageName, String className) {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createIntType(), FEATURE_ID);

    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC.build(), getMCTypeFacade().createBooleanType(), E_IS_SET, featureParameter);
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EIsSet", astcdAttributes, packageName, className));
    return method;
  }

  public ASTCDMethod createEDerivedStructuralFeatureIDMethod() {
    //TODO generate mapping for inherited attributes
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter baseClassParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType("Class<?>"), "baseClass");

    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC.build(), getMCTypeFacade().createIntType(),
        E_DERIVED_STRUCTURAL_FEATURE_ID, featureParameter, baseClassParameter);
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return super.eDerivedStructuralFeatureID(featureID, baseClass);"));
    return method;
  }

  public ASTCDMethod createEBaseStructuralFeatureIDMethod() {
    //TODO generate mapping for inherited attributes
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter baseClassParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType("Class<?>"), "baseClass");

    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC.build(), getMCTypeFacade().createIntType(),
        E_BASE_STRUCTURAL_FEATURE_ID, featureParameter, baseClassParameter);
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return super.eBaseStructuralFeatureID(featureID, baseClass);"));
    return method;
  }

  public ASTCDMethod createEToStringMethod(List<ASTCDAttribute> astcdAttributes) {
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC.build(),
        getMCTypeFacade().createQualifiedType(String.class), "toString");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EToString", astcdAttributes));
    return method;
  }

  public ASTCDMethod creatEStaticClassMethod(String packageName, String className) {
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PROTECTED.build(),
        getMCTypeFacade().createQualifiedType(E_CLASS_TYPE), "eStaticClass");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return " + packageName + ".Literals." + className + ";"));
    return method;
  }
}
