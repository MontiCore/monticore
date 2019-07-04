package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import net.sourceforge.plantuml.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants.FACTORY_SUFFIX;
import static de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants.NODE_FACTORY_SUFFIX;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PACKAGE_PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PACKAGE_PRIVATE_ABSTRACT;

public class PackageInterfaceDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDInterface> {

  private static final String GET = "get%s";

  private final DecorationHelper decorationHelper;

  public PackageInterfaceDecorator(GlobalExtensionManagement glex, DecorationHelper decorationHelper) {
    super(glex);
    this.decorationHelper = decorationHelper;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit compilationUnit) {
    String definitionName = compilationUnit.deepClone().getCDDefinition().getName();
    String interfaceName = definitionName + PACKAGE_SUFFIX;

    List<ASTCDClass> classList = compilationUnit.deepClone().getCDDefinition().getCDClassList();
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(interfaceName)
        .addInterface(getCDTypeFacade().createSimpleReferenceType(ASTE_PACKAGE))
        .addCDAttribute(createENameAttribute(definitionName))
        .addCDAttribute(createENSURIAttribute(definitionName))
        .addCDAttribute(createENSPrefixAttribute(definitionName))
        .addCDAttribute(createEInstanceAttribute(interfaceName))
        .addCDAttribute(createConstantsAttribute(definitionName))
        .addAllCDAttributes(createProdAttributes(classList))
        .addAllCDAttributes(createNonTerminalAttributes(classList))
        .addCDMethod(createNodeFactoryMethod(definitionName))
        .addCDMethod(createEEnumMethod(definitionName))
        .addAllCDMethods(createEClassMethods(classList))
        .addAllCDMethods(createEAttributeMethods(classList))
        .build();

  }

  protected ASTCDAttribute createENameAttribute(String definitionName) {
    // e.g. String eNAME = "Automata";
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, String.class, E_NAME);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + definitionName));
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
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + definitionName));
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


  protected List<ASTCDAttribute> createProdAttributes(List<ASTCDClass> astcdClassList) {
    // e.g. int ASTAutomaton = 1; int ASTState = 2;
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (int i = 0; i < astcdClassList.size(); i++) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, getCDTypeFacade().createIntType(),
          astcdClassList.get(i).getName());
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + (i + 1)));
      attributeList.add(attribute);
    }
    return attributeList;
  }

  protected List<ASTCDAttribute> createNonTerminalAttributes(List<ASTCDClass> astcdClassList) {
    // e.g. int ASTAutomaton_Name = 0; int ASTAutomaton_States = 1;
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      for (int i = 0; i < astcdClass.getCDAttributeList().size(); i++) {
        ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, getCDTypeFacade().createIntType(),
            astcdClass.getName() + "_" + StringUtils.capitalize(astcdClass.getCDAttribute(i).getName()));
        this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + i));
        attributeList.add(attribute);
      }
    }
    return attributeList;
  }

  protected ASTCDMethod createNodeFactoryMethod(String definitionName) {
    // e.g. AutomataNodeFactory getAutomataFactory();
    ASTSimpleReferenceType nodeFactoryType = getCDTypeFacade().createSimpleReferenceType(definitionName + NODE_FACTORY_SUFFIX);
    String methodName = String.format(GET, definitionName + FACTORY_SUFFIX);
    return getCDMethodFacade().createMethod(PACKAGE_PRIVATE_ABSTRACT, nodeFactoryType, methodName);
  }

  protected ASTCDMethod createEEnumMethod(String definitionName) {
    // e.g. EEnum getConstantsAutomata();
    ASTSimpleReferenceType eEnumType = getCDTypeFacade().createSimpleReferenceType(E_ENUM_TYPE);
    String methodName = String.format(GET, CONSTANTS_PREFIX + definitionName);
    return getCDMethodFacade().createMethod(PACKAGE_PRIVATE_ABSTRACT, eEnumType, methodName);
  }

  protected List<ASTCDMethod> createEClassMethods(List<ASTCDClass> astcdClassList) {
    // e.g. EClass getAutomaton(); EClass getState();
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      ASTSimpleReferenceType eEnumType = getCDTypeFacade().createSimpleReferenceType(E_CLASS_TYPE);
      String methodName = String.format(GET, astcdClass.getName());
      methodList.add(getCDMethodFacade().createMethod(PACKAGE_PRIVATE_ABSTRACT, eEnumType, methodName));
    }
    return methodList;
  }

  protected List<ASTCDMethod> createEAttributeMethods(List<ASTCDClass> astcdClassList) {
    // e.g. EAttribute getASTAutomaton_Name() ; EReference getASTAutomaton_States() ;
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      for (ASTCDAttribute astcdAttribute : astcdClass.getCDAttributeList()) {
        ASTSimpleReferenceType returnType;
        if (decorationHelper.isAstNode(astcdAttribute) || decorationHelper.isOptionalAstNode(astcdAttribute)
            || decorationHelper.isListAstNode(astcdAttribute)) {
          returnType = getCDTypeFacade().createSimpleReferenceType(E_REFERENCE_TYPE);
        } else {
          returnType = getCDTypeFacade().createSimpleReferenceType(E_ATTRIBUTE_TYPE);
        }
        String methodName = String.format(GET, astcdClass.getName() + "_" + StringUtils.capitalize(astcdAttribute.getName()));
        methodList.add(getCDMethodFacade().createMethod(PACKAGE_PRIVATE_ABSTRACT, returnType, methodName));
      }
    }
    return methodList;
  }
}
