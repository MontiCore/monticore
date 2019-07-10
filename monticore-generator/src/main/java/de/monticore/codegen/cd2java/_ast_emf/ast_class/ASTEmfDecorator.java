package de.monticore.codegen.cd2java._ast_emf.ast_class;

import de.monticore.codegen.cd2java._ast.ast_class.ASTDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;

public class ASTEmfDecorator extends ASTDecorator {

  public ASTEmfDecorator(final GlobalExtensionManagement glex,
                         final ASTService astService,
                         final VisitorService visitorService,
                         final NodeFactoryService nodeFactoryService,
                         final ASTSymbolDecorator symbolDecorator,
                         final ASTScopeDecorator scopeDecorator,
                         final MethodDecorator methodDecorator,
                         final SymbolTableService symbolTableService) {
    super(glex, astService, visitorService, nodeFactoryService, symbolDecorator,
        scopeDecorator, methodDecorator, symbolTableService);
  }

  @Override
  public ASTCDClass decorate(ASTCDClass clazz) {
    clazz.addInterface(this.astService.getASTBaseInterface());
    clazz.addInterface(getCDTypeFacade().createSimpleReferenceType(E_PACKAGE));
    clazz.addCDMethod(createAcceptMethod(clazz));
    clazz.addAllCDMethods(createAcceptSuperMethods(clazz));
    clazz.addCDMethod(getConstructMethod(clazz));
    clazz.addCDMethod(createGetChildrenMethod(clazz));
    clazz.addAllCDMethods(createEMethods(clazz));

    if (!clazz.isPresentSuperclass()) {
      clazz.setSuperclass(this.getCDTypeFacade().createSimpleReferenceType(AST_EC_NODE));
    }

    List<ASTCDAttribute> symbolAttributes = symbolDecorator.decorate(clazz);
    addSymboltableMethods(symbolAttributes, clazz);

    List<ASTCDAttribute> scopeAttributes = scopeDecorator.decorate(clazz);
    addSymboltableMethods(scopeAttributes, clazz);

    return clazz;
  }

  public List<ASTCDMethod> createEMethods(ASTCDClass astcdClass) {
    List<ASTCDAttribute> astcdAttributes = astcdClass.deepClone().getCDAttributeList();
    String packageName = astService.getCDName() + PACKAGE_SUFFIX;
    String className = astcdClass.getName();
    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.add(createEGetMethod(astcdAttributes, packageName, className));
    methodList.add(createESetMethod(astcdAttributes, packageName, className));
    methodList.add(createEUnsetMethod(astcdAttributes, packageName, className));
    methodList.add(createEIsSetMethod(astcdAttributes, packageName, className));
    methodList.add(createEBaseStructuralFeatureIDMethod());
    methodList.add(createEDerivedStructuralFeatureIDMethod());
    methodList.add(createEToStringMethod(astcdAttributes));
    methodList.add(creatEStaticClassMethod(packageName, className));

    return methodList;
  }

  public ASTCDMethod createEGetMethod(List<ASTCDAttribute> astcdAttributes, String packageName, String className) {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter resolveParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "resolve");
    ASTCDParameter coreTypeParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "coreType");

    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, getCDTypeFacade().createSimpleReferenceType(Object.class), E_GET,
        featureParameter, resolveParameter, coreTypeParameter);
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EGet", astcdAttributes, packageName, className));
    return method;
  }

  public ASTCDMethod createESetMethod(List<ASTCDAttribute> astcdAttributes, String packageName, String className) {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter newValueParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createSimpleReferenceType(Object.class), "newValue");

    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, E_SET, featureParameter, newValueParameter);
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.ESet", astcdAttributes, packageName, className));
    return method;
  }

  public ASTCDMethod createEUnsetMethod(List<ASTCDAttribute> astcdAttributes, String packageName, String className) {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createIntType(), FEATURE_ID);

    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, E_UNSET, featureParameter);
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EUnset", astcdAttributes, packageName, className));
    return method;
  }

  public ASTCDMethod createEIsSetMethod(List<ASTCDAttribute> astcdAttributes, String packageName, String className) {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createIntType(), FEATURE_ID);

    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, getCDTypeFacade().createBooleanType(), E_IS_SET, featureParameter);
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EIsSet", astcdAttributes, packageName, className));
    return method;
  }

  public ASTCDMethod createEDerivedStructuralFeatureIDMethod() {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter baseClassParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createComplexReferenceType("Class<?>"), "baseClass");

    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, getCDTypeFacade().createIntType(),
        E_DERIVED_STRUCTURAL_FEATURE_ID, featureParameter, baseClassParameter);
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return super.eDerivedStructuralFeatureID(featureID, baseClass);"));
    return method;
  }

  public ASTCDMethod createEBaseStructuralFeatureIDMethod() {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter baseClassParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createComplexReferenceType("Class<?>"), "baseClass");

    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, getCDTypeFacade().createIntType(),
        E_BASE_STRUCTURAL_FEATURE_ID, featureParameter, baseClassParameter);
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return super.eBaseStructuralFeatureID(featureID, baseClass);"));
    return method;
  }

  public ASTCDMethod createEToStringMethod(List<ASTCDAttribute> astcdAttributes) {
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC,
        getCDTypeFacade().createSimpleReferenceType(String.class), "toString");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EToString", astcdAttributes));
    return method;
  }

  public ASTCDMethod creatEStaticClassMethod(String packageName, String className) {
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PROTECTED,
        getCDTypeFacade().createSimpleReferenceType(E_CLASS_TYPE), "eStaticClass");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return " + packageName + ".Literals." + className + ";"));
    return method;
  }
}
