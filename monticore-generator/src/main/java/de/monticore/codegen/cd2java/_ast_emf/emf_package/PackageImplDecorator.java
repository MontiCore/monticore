package de.monticore.codegen.cd2java._ast_emf.emf_package;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants.*;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class PackageImplDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDClass> {

  private static final String GET = "get%s";

  private final MandatoryAccessorDecorator accessorDecorator;

  private final DecorationHelper decorationHelper;

  public PackageImplDecorator(GlobalExtensionManagement glex, MandatoryAccessorDecorator accessorDecorator,
                              DecorationHelper decorationHelper) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.decorationHelper = decorationHelper;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit compilationUnit) {
    String definitionName = compilationUnit.deepClone().getCDDefinition().getName();
    String packageImplName = definitionName + PACKAGE_IMPL_SUFFIX;
    String packageName = definitionName + PACKAGE_SUFFIX;

    List<ASTCDClass> classList = compilationUnit.deepClone().getCDDefinition().getCDClassList();

    List<ASTCDAttribute> eClassAttributes = getEClassAttributes(classList);
    //e.g. public EClass getAutomaton() { return automaton; }
    List<ASTCDMethod> eClassMethods = new ArrayList<>();
    for (ASTCDAttribute eClassAttribute : eClassAttributes) {
      eClassMethods.addAll(accessorDecorator.decorate(eClassAttribute));
    }

    ASTCDAttribute constantsEEnumAttribute = createConstantsEEnumAttribute(definitionName);
    // e.g. public EEnum getConstantsAutomata(){ return constantsAutomata;}
    List<ASTCDMethod> constantsEEnumMethod = accessorDecorator.decorate(constantsEEnumAttribute);

    return CD4AnalysisMill.cDClassBuilder()
        .setName(packageImplName)
        .setSuperclass(getCDTypeFacade().createSimpleReferenceType(E_PACKAGE_IMPL))
        .addInterface(getCDTypeFacade().createSimpleReferenceType(packageName))
        .addCDAttribute(constantsEEnumAttribute)
        .addAllCDAttributes(eClassAttributes)
        .addCDAttribute(createISCreatedAttribute())
        .addCDAttribute(createIsInitializedAttribute())
        .addCDAttribute(createIsIntitedAttribute())
        .addCDConstructor(createContructor(packageImplName, definitionName))
        .addCDMethod(createInitMethod(packageName))
        .addAllCDMethods(eClassMethods)
        .addAllCDMethods(constantsEEnumMethod)
        .addCDMethod(createGetNodeFactoryMethod(definitionName))
        .addCDMethod(createGetPackageMethod(definitionName))
        .addCDMethod(createASTESuperPackagesMethod())
        .addAllCDMethods(createGetEAttributeMethods(classList))
        .addCDMethod(createCreatePackageContentsMethod(definitionName, classList))
        .build();
  }

  protected List<ASTCDAttribute> getEClassAttributes(List<ASTCDClass> astcdClassList) {
    //e.g.  private EClass automaton;
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      attributeList.add(getCDAttributeFacade().createAttribute(PRIVATE, E_CLASS_TYPE, StringTransformations.uncapitalize(astcdClass.getName())));
    }
    return attributeList;
  }


  protected ASTCDAttribute createConstantsEEnumAttribute(String definitionName) {
    // private EEnum constantsAutomataEEnum
    return getCDAttributeFacade().createAttribute(PRIVATE, E_ENUM_TYPE,
        StringTransformations.uncapitalize(CONSTANTS_PREFIX) + definitionName);
  }

  protected ASTCDAttribute createISCreatedAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, getCDTypeFacade().createBooleanType(), IS_CREATED);
  }

  protected ASTCDAttribute createIsInitializedAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, getCDTypeFacade().createBooleanType(), IS_INITIALIZED);
  }


  protected ASTCDAttribute createIsIntitedAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE_STATIC, getCDTypeFacade().createBooleanType(), IS_INITED);
  }

  protected ASTCDConstructor createContructor(String packageImplName, String definitionName) {
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PRIVATE, packageImplName);
    replaceTemplate(EMPTY_BODY, constructor,
        new StringHookPoint("super(" + ENS_URI + "," + definitionName + NODE_FACTORY_SUFFIX + "." + GET_FACTORY_METHOD + "())"));
    return constructor;
  }

  protected ASTCDMethod createInitMethod(String packageName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC_STATIC, getCDTypeFacade().createSimpleReferenceType(packageName), "init");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.emf_package.InitMethod", packageName));
    return method;
  }

  protected ASTCDMethod createGetNodeFactoryMethod(String definitionName) {
    // e.g. AutomataNodeFactory getAutomataFactory();
    ASTSimpleReferenceType nodeFactoryType = getCDTypeFacade().createSimpleReferenceType(definitionName + NODE_FACTORY_SUFFIX);
    String methodName = String.format(GET, definitionName + FACTORY_SUFFIX);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, nodeFactoryType, methodName);
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return (" + definitionName + NODE_FACTORY_SUFFIX + ")getENodeFactoryInstance()"));
    return method;
  }

  protected ASTCDMethod createGetPackageMethod(String definitionName) {
    // e.g. public String getPackageName() { return "automata"; }
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createStringType(), "getPackageName");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return \"" + StringTransformations.uncapitalize(definitionName) + "\""));
    return method;
  }

  protected ASTCDMethod createASTESuperPackagesMethod() {
    ASTSimpleReferenceType returnType = getCDTypeFacade().createListTypeOf(ASTE_PACKAGE);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, "getASTESuperPackages");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.emf_package.GetASTESuperPackages"));
    return method;
  }

  protected List<ASTCDMethod> createGetEAttributeMethods(List<ASTCDClass> astcdClassList) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      for (int i = 0; i < astcdClass.getCDAttributeList().size(); i++) {
        ASTCDAttribute astcdAttribute = astcdClass.getCDAttribute(i);
        ASTSimpleReferenceType returnType;
        if (decorationHelper.isAstNode(astcdAttribute) || decorationHelper.isOptionalAstNode(astcdAttribute)
            || decorationHelper.isListAstNode(astcdAttribute)) {
          returnType = getCDTypeFacade().createSimpleReferenceType(E_REFERENCE_TYPE);
        } else {
          returnType = getCDTypeFacade().createSimpleReferenceType(E_ATTRIBUTE_TYPE);
        }
        String methodName = String.format(GET, astcdClass.getName() + "_" + StringTransformations.capitalize(astcdAttribute.getName()));
        ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName);

        replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return (" + TypesPrinter.printType(returnType) + ")" +
            StringTransformations.uncapitalize(astcdClass.getName()) + "getEStructuralFeatures().get(" + i + ")"));
        methodList.add(method);
      }
    }
    return methodList;
  }

  protected ASTCDMethod createCreatePackageContentsMethod(String definitionName, List<ASTCDClass> classList) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "createPackageContents");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.emf_package.CreatePackageContents", definitionName, classList));
    return method;
  }

  protected ASTCDMethod createInitializePackageContentsMethod(){
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "initializePackageContents");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.emf_package.InitializePackageContents"));
    return method;
  }
}
