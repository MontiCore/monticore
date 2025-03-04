/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.emf_package;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;

public class PackageImplDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected static final String GET = "get%s";

  protected final MandatoryAccessorDecorator accessorDecorator;

  protected final EmfService emfService;


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
    ASTCDDefinition definition = prepareCDForEmfPackageDecoration(compilationUnit.getCDDefinition());
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
        .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addSuperclass(getMCTypeFacade().createQualifiedType(E_PACKAGE_IMPL)).build())
        .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().addInterface(getMCTypeFacade().createQualifiedType(packageName)).build())
        .addCDMember(constantsEEnumAttribute)
        .addAllCDMembers(eAttributes)
        .addCDMember(createISCreatedAttribute())
        .addCDMember(createIsInitializedAttribute())
        .addCDMember(createIsInitedAttribute())
        .addCDMember(createConstructor(packageImplName, definitionName))
        .addCDMember(createInitMethod(packageName))
        .addAllCDMembers(eClassMethods)
        .addAllCDMembers(constantsEEnumMethod)
        .addCDMember(createGetMillMethod(definitionName))
        .addCDMember(createGetPackageMethod(definitionName))
        .addCDMember(createASTESuperPackagesMethod())
        .addAllCDMembers(createGetEAttributeMethods(definition))
        .addCDMember(createCreatePackageContentsMethod(definitionName, definition))
        .addCDMember(createInitializePackageContentsMethod(compilationUnit.getCDDefinition()))
        .build();
  }

  protected ASTCDDefinition prepareCDForEmfPackageDecoration(ASTCDDefinition astcdDefinition) {
    ASTCDDefinition copiedDefinition = astcdDefinition.deepClone();
    //remove inherited attributes
    copiedDefinition.getCDClassesList()
        .stream()
        .map(emfService::removeInheritedAttributes)
        .collect(Collectors.toList());

    //remove ast node Interface e.g. ASTAutomataNode
    List<ASTCDInterface> astcdInterfaces = copiedDefinition.getCDInterfacesList()
        .stream()
        .filter(x -> !emfService.isASTNodeInterface(x, copiedDefinition))
        .collect(Collectors.toList());

    //remove inherited attributes
    astcdInterfaces
        .stream()
        .map(emfService::removeInheritedAttributes)
        .collect(Collectors.toList());

    return copiedDefinition;
  }

  protected List<ASTCDAttribute> getEClassAttributes(ASTCDDefinition astcdDefinition) {
    //e.g.  private EClass automaton;
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassesList()) {
      attributeList.add(getCDAttributeFacade().createAttribute(PROTECTED.build(), E_CLASS_TYPE, StringTransformations.uncapitalize(astcdClass.getName())));
    }
    for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfacesList()) {
      attributeList.add(getCDAttributeFacade().createAttribute(PROTECTED.build(), E_CLASS_TYPE, StringTransformations.uncapitalize(astcdInterface.getName())));
    }
    return attributeList;
  }

  protected List<ASTCDAttribute> getEDataTypeAttributes(ASTCDDefinition astcdDefinition) {
    Set<String> eDataTypes = emfService.getEDataTypes(astcdDefinition);
    return eDataTypes.stream()
        .map(x -> getCDAttributeFacade().createAttribute(PUBLIC.build(), E_DATA_TYPE,
            StringTransformations.uncapitalize(getDecorationHelper().getSimpleNativeType(x))))
        .collect(Collectors.toList());
  }


  protected ASTCDAttribute createConstantsEEnumAttribute(String definitionName) {
    // private EEnum constantsAutomataEEnum
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), E_ENUM_TYPE,
        StringTransformations.uncapitalize(CONSTANTS_PREFIX) + definitionName);
  }

  protected ASTCDAttribute createISCreatedAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), getMCTypeFacade().createBooleanType(), IS_CREATED);
  }

  protected ASTCDAttribute createIsInitializedAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), getMCTypeFacade().createBooleanType(), IS_INITIALIZED);
  }


  protected ASTCDAttribute createIsInitedAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED_STATIC.build(), getMCTypeFacade().createBooleanType(), IS_INITED);
  }

  protected ASTCDConstructor createConstructor(String packageImplName, String definitionName) {
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PRIVATE.build(), packageImplName);
    replaceTemplate(EMPTY_BODY, constructor,
        new StringHookPoint("super(" + ENS_URI + ");" ));
    return constructor;
  }

  protected ASTCDMethod createInitMethod(String packageName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), getMCTypeFacade().createQualifiedType(packageName), "init");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.emf_package.InitEmfMethod", packageName));
    return method;
  }

  protected ASTCDMethod createGetMillMethod(String definitionName) {
    // e.g. AutomataNodeFactory getAutomataFactory();
    String methodName = String.format(GET, emfService.getMillSimpleName());
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createQualifiedType(emfService.getMillFullName()), methodName);
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return ("+ emfService.getMillFullName() +  ")getEFactoryInstance();"));
    return method;
  }

  protected ASTCDMethod createGetPackageMethod(String definitionName) {
    // e.g. public String getPackageName() { return "automata"; }
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createStringType(), "getPackageName");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return \"" + StringTransformations.uncapitalize(definitionName) + "\";"));
    return method;
  }

  protected ASTCDMethod createASTESuperPackagesMethod() {
    ASTMCType type = getMCTypeFacade().createListTypeOf(ASTE_PACKAGE);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), type, "getASTESuperPackages");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.emf_package.GetASTESuperPackages"));
    return method;
  }

  protected List<ASTCDMethod> createGetEAttributeMethods(ASTCDDefinition astcdDefinition) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassesList()) {
      List<ASTCDAttribute> attrList = astcdClass.getCDAttributeList();
      for (int i = 0; i < attrList.size(); i++) {
        methodList.add(createGetEAttributeMethod(attrList.get(i), i, astcdClass.getName()));
      }
    }

    for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfacesList()) {
      List<ASTCDAttribute> attrList = astcdInterface.getCDAttributeList();
      for (int i = 0; i < attrList.size(); i++) {
        methodList.add(createGetEAttributeMethod(attrList.get(i), i, astcdInterface.getName()));
      }
    }
    return methodList;
  }

  protected ASTCDMethod createGetEAttributeMethod(ASTCDAttribute astcdAttribute, int index, String astcdClassName) {
    ASTMCQualifiedType type = emfService.getEmfAttributeType(astcdAttribute);
    String methodName = String.format(GET, astcdClassName + "_" + StringTransformations.capitalize(astcdAttribute.getName()));
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), type, methodName);

    replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return ("
        + CD4CodeMill.prettyPrint(type, false) + ")" +
        StringTransformations.uncapitalize(astcdClassName) + ".getEStructuralFeatures().get(" + index + ");"));
    return method;
  }

  protected ASTCDMethod createCreatePackageContentsMethod(String definitionName, ASTCDDefinition astcdDefinition) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "createPackageContents");
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast_emf.emf_package.CreatePackageContents",
        definitionName, astcdDefinition));
    return method;
  }

  protected ASTCDMethod createInitializePackageContentsMethod(ASTCDDefinition astcdDefinition) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "initializePackageContents");
    //find literalsEnum in CD
    Optional<ASTCDEnum> literalsEnum = astcdDefinition.getCDEnumsList()
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
