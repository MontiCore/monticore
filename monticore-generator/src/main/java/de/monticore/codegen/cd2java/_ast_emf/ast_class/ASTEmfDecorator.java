package de.monticore.codegen.cd2java._ast_emf.ast_class;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java._ast.ast_class.ASTDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.CDModifier;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;

public class ASTEmfDecorator extends ASTDecorator {

  protected final EmfService emfService;

  public ASTEmfDecorator(final GlobalExtensionManagement glex,
                         final ASTService astService,
                         final VisitorService visitorService,
                         final NodeFactoryService nodeFactoryService,
                         final ASTSymbolDecorator symbolDecorator,
                         final ASTScopeDecorator scopeDecorator,
                         final MethodDecorator methodDecorator,
                         final SymbolTableService symbolTableService,
                         final EmfService emfService) {
    super(glex, astService, visitorService, nodeFactoryService, symbolDecorator,
        scopeDecorator, methodDecorator, symbolTableService);
    this.emfService = emfService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass originalClass, ASTCDClass changedClass) {
    changedClass.addInterface(this.astService.getASTBaseInterface());
    changedClass.addCDMethod(createAcceptMethod(originalClass));
    changedClass.addAllCDMethods(createAcceptSuperMethods(originalClass));
    changedClass.addCDMethod(getConstructMethod(originalClass));
    changedClass.addCDMethod(createGetChildrenMethod(originalClass));
    changedClass.addAllCDMethods(createEMethods(originalClass));

    if (!originalClass.isPresentSuperclass()) {
      changedClass.setSuperclass(this.getCDTypeFacade().createQualifiedType(AST_EC_NODE));
    }

    List<ASTCDAttribute> symbolAttributes = symbolDecorator.decorate(originalClass);
    addSymboltableMethods(symbolAttributes, originalClass);

    List<ASTCDAttribute> scopeAttributes = scopeDecorator.decorate(originalClass);
    addSymboltableMethods(scopeAttributes, originalClass);

    return changedClass;
  }

  public List<ASTCDMethod> createEMethods(ASTCDClass astcdClass) {
    // with inherited attributes
    List<ASTCDAttribute> copiedAttibuteList = astcdClass.deepClone().getCDAttributeList();

    String packageName = astService.getCDName() + PACKAGE_SUFFIX;
    String className = astcdClass.getName();
    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.add(createEGetMethod(copiedAttibuteList, packageName, className));
    methodList.add(createESetMethod(copiedAttibuteList, packageName, className));
    methodList.add(createEUnsetMethod(copiedAttibuteList, packageName, className));
    methodList.add(createEIsSetMethod(copiedAttibuteList, packageName, className));
    methodList.add(createEBaseStructuralFeatureIDMethod());
    methodList.add(createEDerivedStructuralFeatureIDMethod());
    methodList.add(creatEStaticClassMethod(packageName, className));

    if (astcdClass.getCDMethodList().stream().noneMatch(x -> x.getName().equals("toString"))) {
      methodList.add(createEToStringMethod(copiedAttibuteList));
    }
    return methodList;
  }

  public ASTCDMethod createEGetMethod(List<ASTCDAttribute> astcdAttributes, String packageName, String className) {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter resolveParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "resolve");
    ASTCDParameter coreTypeParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "coreType");
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createQualifiedType(Object.class)).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, returnType, E_GET,
        featureParameter, resolveParameter, coreTypeParameter);
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EGet", astcdAttributes, packageName, className));
    return method;
  }

  public ASTCDMethod createESetMethod(List<ASTCDAttribute> astcdAttributes, String packageName, String className) {
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter newValueParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(Object.class), "newValue");

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

    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createBooleanType()).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, returnType, E_IS_SET, featureParameter);
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EIsSet", astcdAttributes, packageName, className));
    return method;
  }

  public ASTCDMethod createEDerivedStructuralFeatureIDMethod() {
    //TODO generate mapping for inherited attributes
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter baseClassParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType("Class<?>"), "baseClass");

    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createIntType()).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, returnType,
        E_DERIVED_STRUCTURAL_FEATURE_ID, featureParameter, baseClassParameter);
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return super.eDerivedStructuralFeatureID(featureID, baseClass);"));
    return method;
  }

  public ASTCDMethod createEBaseStructuralFeatureIDMethod() {
    //TODO generate mapping for inherited attributes
    ASTCDParameter featureParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createIntType(), FEATURE_ID);
    ASTCDParameter baseClassParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType("Class<?>"), "baseClass");

    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createIntType()).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC, returnType,
        E_BASE_STRUCTURAL_FEATURE_ID, featureParameter, baseClassParameter);
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return super.eBaseStructuralFeatureID(featureID, baseClass);"));
    return method;
  }

  public ASTCDMethod createEToStringMethod(List<ASTCDAttribute> astcdAttributes) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createQualifiedType(String.class)).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PUBLIC,
        returnType, "toString");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.ast_class.EToString", astcdAttributes));
    return method;
  }

  public ASTCDMethod creatEStaticClassMethod(String packageName, String className) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createTypeByDefinition(E_CLASS_TYPE)).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(CDModifier.PROTECTED,
        returnType, "eStaticClass");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return " + packageName + ".Literals." + className + ";"));
    return method;
  }
}
