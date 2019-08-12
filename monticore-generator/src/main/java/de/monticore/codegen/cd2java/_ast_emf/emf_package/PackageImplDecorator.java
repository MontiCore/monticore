/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.emf_package;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants.*;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class PackageImplDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  private static final String GET = "get%s";

  private final MandatoryAccessorDecorator accessorDecorator;

  private final EmfService emfService;


  public PackageImplDecorator(final GlobalExtensionManagement glex,
                              final MandatoryAccessorDecorator accessorDecorator,
                              final EmfService emfService
  ) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.emfService = emfService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDCompilationUnit compilationUnit) {
    ASTCDDefinition definition = emfService.prepareCDForEmfPackageDecoration(compilationUnit.getCDDefinition());
    String definitionName = definition.getName();
    String packageImplName = definitionName + PACKAGE_IMPL_SUFFIX;
    String packageName = definitionName + PACKAGE_SUFFIX;

    List<ASTCDAttribute> eAttributes = getEClassAttributes(definition);
    eAttributes.addAll(getEDataTypeAttributes(definition));
    //e.g. public EClass getAutomaton() { return automaton; }
    List<ASTCDMethod> eClassMethods = new ArrayList<>();
    for (ASTCDAttribute eClassAttribute : eAttributes) {
      eClassMethods.addAll(accessorDecorator.decorate(eClassAttribute));
    }

    ASTCDAttribute constantsEEnumAttribute = createConstantsEEnumAttribute(definitionName);
    // e.g. public EEnum getConstantsAutomata(){ return constantsAutomata;}
    List<ASTCDMethod> constantsEEnumMethod = accessorDecorator.decorate(constantsEEnumAttribute);

    return CD4AnalysisMill.cDClassBuilder()
        .setName(packageImplName)
        .setModifier(PUBLIC.build())
        .setSuperclass(getCDTypeFacade().createQualifiedType(E_PACKAGE_IMPL))
        .addInterface(getCDTypeFacade().createQualifiedType(packageName))
        .addCDAttribute(constantsEEnumAttribute)
        .addAllCDAttributes(eAttributes)
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
        .addAllCDMethods(createGetEAttributeMethods(definition))
        .addCDMethod(createCreatePackageContentsMethod(definitionName, definition))
        .addCDMethod(createInitializePackageContentsMethod(compilationUnit.getCDDefinition()))
        .build();
  }

  protected List<ASTCDAttribute> getEClassAttributes(ASTCDDefinition astcdDefinition) {
    //e.g.  private EClass automaton;
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
      attributeList.add(getCDAttributeFacade().createAttribute(PRIVATE, E_CLASS_TYPE, StringTransformations.uncapitalize(astcdClass.getName())));
    }
    for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfaceList()) {
        attributeList.add(getCDAttributeFacade().createAttribute(PRIVATE, E_CLASS_TYPE, StringTransformations.uncapitalize(astcdInterface.getName())));
    }
    return attributeList;
  }

  protected List<ASTCDAttribute> getEDataTypeAttributes(ASTCDDefinition astcdDefinition) {
    //map of <nativeAttributeType, attributeName>
    Set< String> eDataTypes = emfService.getEDataTypes(astcdDefinition);
    return eDataTypes.stream()
        .map(x -> getCDAttributeFacade().createAttribute(PUBLIC, E_DATA_TYPE,
            StringTransformations.uncapitalize(emfService.getSimpleNativeType(x))))
        .collect(Collectors.toList());
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
        new StringHookPoint("super(" + ENS_URI + "," + definitionName + NODE_FACTORY_SUFFIX + "." + GET_FACTORY_METHOD + "());"));
    return constructor;
  }

  protected ASTCDMethod createInitMethod(String packageName) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createQualifiedType(packageName)).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC_STATIC, returnType, "init");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.emf_package.InitMethod", packageName));
    return method;
  }

  protected ASTCDMethod createGetNodeFactoryMethod(String definitionName) {
    // e.g. AutomataNodeFactory getAutomataFactory();
    ASTMCReturnType nodeFactoryType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createQualifiedType(definitionName + NODE_FACTORY_SUFFIX)).build();
    String methodName = String.format(GET, definitionName + FACTORY_SUFFIX);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, nodeFactoryType, methodName);
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return (" + definitionName + NODE_FACTORY_SUFFIX + ")getEFactoryInstance();"));
    return method;
  }

  protected ASTCDMethod createGetPackageMethod(String definitionName) {
    // e.g. public String getPackageName() { return "automata"; }
    ASTMCReturnType type = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createStringType()).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, type,"getPackageName");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return \"" + StringTransformations.uncapitalize(definitionName) + "\";"));
    return method;
  }

  protected ASTCDMethod createASTESuperPackagesMethod() {
    ASTMCType type = getCDTypeFacade().createListTypeOf(ASTE_PACKAGE);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(type).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, "getASTESuperPackages");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.emf_package.GetASTESuperPackages"));
    return method;
  }

  protected List<ASTCDMethod> createGetEAttributeMethods(ASTCDDefinition astcdDefinition) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
      for (int i = 0; i < astcdClass.getCDAttributeList().size(); i++) {
        methodList.add(createGetEAttributeMethod(astcdClass.getCDAttribute(i), i, astcdClass.getName()));
      }
    }

    for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfaceList()) {
      for (int i = 0; i < astcdInterface.getCDAttributeList().size(); i++) {
        methodList.add(createGetEAttributeMethod(astcdInterface.getCDAttribute(i), i, astcdInterface.getName()));
      }
    }
    return methodList;
  }

  protected ASTCDMethod createGetEAttributeMethod(ASTCDAttribute astcdAttribute, int index, String astcdClassName){
    ASTMCQualifiedType type = emfService.getEmfAttributeType(astcdAttribute);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(type).build();
    String methodName = String.format(GET, astcdClassName + "_" + StringTransformations.capitalize(astcdAttribute.getName()));
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, methodName);

    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return (" + returnType.printType() + ")" +
        StringTransformations.uncapitalize(astcdClassName) + ".getEStructuralFeatures().get(" + index + ");"));
    return method;
  }

  protected ASTCDMethod createCreatePackageContentsMethod(String definitionName, ASTCDDefinition astcdDefinition) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "createPackageContents");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.emf_package.CreatePackageContents",
        definitionName, astcdDefinition));
    return method;
  }

  protected ASTCDMethod createInitializePackageContentsMethod(ASTCDDefinition astcdDefinition) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "initializePackageContents");
    //find literalsEnum in CD
    Optional<ASTCDEnum> literalsEnum = astcdDefinition.getCDEnumList()
        .stream()
        .filter(x -> emfService.isLiteralsEnum(x, astcdDefinition.getName()))
        .findFirst();
    if (literalsEnum.isPresent()) {
      replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.emf_package.InitializePackageContents",
          astcdDefinition, literalsEnum.get()));
    }
    return method;
  }
}
