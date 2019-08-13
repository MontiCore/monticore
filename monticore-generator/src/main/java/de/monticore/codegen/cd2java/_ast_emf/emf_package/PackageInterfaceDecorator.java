/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.emf_package;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants.FACTORY_SUFFIX;
import static de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants.NODE_FACTORY_SUFFIX;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class PackageInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  private static final String GET = "get%s";

  private final EmfService emfService;

  public PackageInterfaceDecorator(final GlobalExtensionManagement glex,
                                   final EmfService emfService) {
    super(glex);
    this.emfService = emfService;
  }

  @Override
  public ASTCDInterface decorate(final ASTCDCompilationUnit compilationUnit) {
    ASTCDDefinition astcdDefinition = compilationUnit.deepClone().getCDDefinition();

    String definitionName = astcdDefinition.getName();
    String interfaceName = definitionName + PACKAGE_SUFFIX;

    List<ASTCDAttribute> prodAttributes = createProdAttributes(astcdDefinition);

    //prodAttributeSize + 1 because they start with 0 and you need the next free index
    //e.g. prodAttributes.size = 2 -> last index used is 2 -> next free index 3
    List<ASTCDAttribute> eDataTypeAttributes = createEDataTypeAttributes(astcdDefinition, prodAttributes.size() + 1);
    List<ASTCDMethod> eDataTypeMethods = createEDataTypeMethods(eDataTypeAttributes);

    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(interfaceName)
        .setModifier(PUBLIC.build())
        .addInterface(getCDTypeFacade().createQualifiedType(ASTE_PACKAGE))
        .addCDAttribute(createENameAttribute(definitionName))
        .addCDAttribute(createENSURIAttribute(definitionName))
        .addCDAttribute(createENSPrefixAttribute(definitionName))
        .addCDAttribute(createEInstanceAttribute(interfaceName))
        .addCDAttribute(createConstantsAttribute(definitionName))
        .addAllCDAttributes(prodAttributes)
        .addAllCDAttributes(eDataTypeAttributes)
        .addAllCDAttributes(createNonTerminalAttributes(astcdDefinition))
        .addCDMethod(createNodeFactoryMethod(definitionName))
        .addCDMethod(createEEnumMethod(definitionName))
        .addAllCDMethods(eDataTypeMethods)
        .addAllCDMethods(createEClassMethods(astcdDefinition))
        .addAllCDMethods(createEAttributeMethods(astcdDefinition))
        .build();

  }


  protected ASTCDAttribute createENameAttribute(String definitionName) {
    // e.g. String eNAME = "Automata";
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, String.class, E_NAME);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= \"" + definitionName + "\""));
    return attribute;
  }

  protected ASTCDAttribute createENSURIAttribute(String definitionName) {
    // e.g. String eNS_URI = "http://Automata/1.0";
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, String.class, ENS_URI);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= \"http://" + definitionName + "/1.0\""));
    return attribute;
  }

  protected ASTCDAttribute createENSPrefixAttribute(String definitionName) {
    // e.g. String eNS_PREFIX = "Automata";
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, String.class, ENS_PREFIX);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= \"" + definitionName + "\""));
    return attribute;
  }

  protected ASTCDAttribute createEInstanceAttribute(String interfaceName) {
    // e.g. AutomataPackage eINSTANCE = AutomataPackageImpl.init();
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, interfaceName, E_INSTANCE);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + interfaceName + "Impl.init()"));
    return attribute;
  }

  protected ASTCDAttribute createConstantsAttribute(String definitionName) {
    // e.g. int ConstantsAutomata = 0;
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, getCDTypeFacade().createIntType(),
        CONSTANTS_PREFIX + definitionName);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= 0"));
    return attribute;
  }


  protected List<ASTCDAttribute> createProdAttributes(ASTCDDefinition astcdDefinition) {
    // e.g. int ASTAutomaton = 1; int ASTState = 2;
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    int i;
    for (i = 0; i < astcdDefinition.getCDClassList().size(); i++) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, getCDTypeFacade().createIntType(),
          astcdDefinition.getCDClassList().get(i).getName());
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + (i + 1)));
      attributeList.add(attribute);
    }

    for (int j = 0; j < astcdDefinition.getCDInterfaceList().size(); j++) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, getCDTypeFacade().createIntType(),
          astcdDefinition.getCDInterfaceList().get(j).getName());
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + (j + i + 1)));
      attributeList.add(attribute);
    }
    return attributeList;
  }

  protected List<ASTCDAttribute> createNonTerminalAttributes(ASTCDDefinition astcdDefinition) {
    // e.g. int ASTAutomaton_Name = 0; int ASTAutomaton_States = 1;
    List<ASTCDAttribute> attributeList = new ArrayList<>();


    for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
      for (int i = 0; i < astcdClass.getCDAttributeList().size(); i++) {
        ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, getCDTypeFacade().createIntType(),
            astcdClass.getName() + "_" + StringTransformations.capitalize(astcdClass.getCDAttribute(i).getName()));
        this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + i));
        attributeList.add(attribute);
      }
    }

    for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfaceList()) {
      for (int j = 0; j < astcdInterface.getCDAttributeList().size(); j++) {
        ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, getCDTypeFacade().createIntType(),
            astcdInterface.getName() + "_" + StringTransformations.capitalize(astcdInterface.getCDAttribute(j).getName()));
        this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + (j)));
        attributeList.add(attribute);
      }
    }
    return attributeList;
  }

  protected List<ASTCDAttribute> createEDataTypeAttributes(ASTCDDefinition astcdDefinition, int startIndex) {
    //index has to start with the next index after prodAttributes
    //cannot start with 0
    Set<String> eDataTypes = emfService.getEDataTypes(astcdDefinition);
    List<ASTCDAttribute> astcdAttributes = new ArrayList<>();

    for (String eDataType : eDataTypes) {
      ASTCDAttribute eDataTypeAttribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, getCDTypeFacade().createIntType(),
          emfService.getSimpleNativeType(eDataType));
      this.replaceTemplate(VALUE, eDataTypeAttribute, new StringHookPoint("= " + startIndex));
      astcdAttributes.add(eDataTypeAttribute);
      startIndex++;
    }
    return astcdAttributes;
  }

  protected ASTCDMethod createNodeFactoryMethod(String definitionName) {
    // e.g. AutomataNodeFactory getAutomataFactory();
    ASTMCQualifiedType nodeFactoryType = getCDTypeFacade().createQualifiedType(definitionName + NODE_FACTORY_SUFFIX);
    String methodName = String.format(GET, definitionName + FACTORY_SUFFIX);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(nodeFactoryType).build();
    return getCDMethodFacade().createMethod(PACKAGE_PRIVATE_ABSTRACT, returnType, methodName);
  }

  protected ASTCDMethod createEEnumMethod(String definitionName) {
    // e.g. EEnum getConstantsAutomata();
    ASTMCQualifiedType eEnumType = getCDTypeFacade().createQualifiedType(E_ENUM_TYPE);
    String methodName = String.format(GET, CONSTANTS_PREFIX + definitionName);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(eEnumType).build();
    return getCDMethodFacade().createMethod(PACKAGE_PRIVATE_ABSTRACT, returnType, methodName);
  }

  protected List<ASTCDMethod> createEClassMethods(ASTCDDefinition astcdDefinition) {
    // e.g. EClass getAutomaton(); EClass getState();
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
      String methodName = String.format(GET, astcdClass.getName());
      ASTMCQualifiedType eClassType = getCDTypeFacade().createQualifiedType(E_CLASS_TYPE);
      ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(eClassType).build();
      methodList.add(getCDMethodFacade().createMethod(PACKAGE_PRIVATE_ABSTRACT, returnType, methodName));
    }

    for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfaceList()) {
      if (!emfService.isASTNodeInterface(astcdInterface, astcdDefinition)) {
        String methodName = String.format(GET, astcdInterface.getName());
        ASTMCQualifiedType eClassType = getCDTypeFacade().createQualifiedType(E_CLASS_TYPE);
        ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(eClassType).build();
        methodList.add(getCDMethodFacade().createMethod(PACKAGE_PRIVATE_ABSTRACT, returnType, methodName));
      }
    }
    return methodList;
  }

  protected List<ASTCDMethod> createEAttributeMethods(ASTCDDefinition astcdDefinition) {
    List<ASTCDMethod> methodList = new ArrayList<>();

    //remove attributes that are inherited
    List<ASTCDClass> noInheritedAttributesClasses = astcdDefinition.getCDClassList()
        .stream()
        .map(emfService::removeInheritedAttributes)
        .collect(Collectors.toList());
    // e.g. EAttribute getASTAutomaton_Name() ; EReference getASTAutomaton_States() ;
    for (ASTCDClass astcdClass : noInheritedAttributesClasses) {
      for (ASTCDAttribute astcdAttribute : astcdClass.getCDAttributeList()) {
        ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(emfService.getEmfAttributeType(astcdAttribute)).build();
        String methodName = String.format(GET, astcdClass.getName() + "_" + StringTransformations.capitalize(astcdAttribute.getName()));
        methodList.add(getCDMethodFacade().createMethod(PACKAGE_PRIVATE_ABSTRACT, returnType, methodName));
      }
    }

    //remove attributes that are inherited
    List<ASTCDInterface> noInheritedAttributesInterfaces = astcdDefinition.getCDInterfaceList()
        .stream()
        .map(emfService::removeInheritedAttributes)
        .collect(Collectors.toList());
    // e.g. EAttribute getASTAutomaton_Name() ; EReference getASTAutomaton_States() ;
    for (ASTCDInterface astcdInterface : noInheritedAttributesInterfaces) {
      for (ASTCDAttribute astcdAttribute : astcdInterface.getCDAttributeList()) {
        ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(emfService.getEmfAttributeType(astcdAttribute)).build();
        String methodName = String.format(GET, astcdInterface.getName() + "_" + StringTransformations.capitalize(astcdAttribute.getName()));
        methodList.add(getCDMethodFacade().createMethod(PACKAGE_PRIVATE_ABSTRACT, returnType, methodName));
      }
    }
    return methodList;
  }

  protected List<ASTCDMethod> createEDataTypeMethods(List<ASTCDAttribute> astcdAttributes) {
    // e.g. EAttribute getASTAutomaton_Name() ; EReference getASTAutomaton_States() ;
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : astcdAttributes) {
      String methodName = String.format(GET, astcdAttribute.getName());
      ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createQualifiedType(E_DATA_TYPE)).build();
      methodList.add(getCDMethodFacade().createMethod(PACKAGE_PRIVATE_ABSTRACT,
          returnType, methodName));
    }
    return methodList;
  }
}
