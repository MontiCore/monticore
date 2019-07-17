package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator.LITERALS_SUFFIX;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;

public class EmfService extends AbstractService {

  private static final String ABSTRACT = "IS_ABSTRACT";

  public EmfService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public EmfService(CDSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return ASTConstants.AST_PACKAGE;
  }

  @Override
  protected ASTService createService(CDSymbol cdSymbol) {
    return createEmfService(cdSymbol);
  }


  public static ASTService createEmfService(CDSymbol cdSymbol) {
    return new ASTService(cdSymbol);
  }

  public String getQualifiedPackageImplName() {
    return getQualifiedPackageImplName(getCDSymbol());
  }

  public String getQualifiedPackageImplName(CDSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), getSimplePackageImplName(cdSymbol));
  }

  public String getSimplePackageImplName() {
    return getSimplePackageImplName(getCDSymbol());
  }

  public String getSimplePackageImplName(CDSymbol cdSymbol) {
    return cdSymbol.getName() + PACKAGE_IMPL_SUFFIX;
  }

  public String getSimplePackageImplName(String cdSymbolQualifiedName) {
    return getSimplePackageImplName(resolveCD(cdSymbolQualifiedName));
  }

  public boolean isExternal(ASTCDAttribute attribute) {
    return getNativeAttributeType(attribute.getType()).endsWith("Ext");
  }

  //for InitializePackageContents template
  public String getClassPackage(CDTypeSymbol cdTypeSymbol) {
    if (cdTypeSymbol.getModelName().toLowerCase().equals(getQualifiedCDName().toLowerCase())) {
      return "this";
    } else {
      return StringTransformations.uncapitalize(getSimplePackageImplName(cdTypeSymbol.getModelName()));
    }
  }

  //for InitializePackageContents template
  public String determineListInteger(ASTType astType) {
    if (DecorationHelper.isListType(TypesPrinter.printType(astType))) {
      return "-1";
    } else {
      return "1";
    }
  }

  //for InitializePackageContents template
  public String determineAbstractString(ASTCDClass cdClass) {
    if (cdClass.isPresentModifier() && cdClass.getModifier().isAbstract()) {
      return ABSTRACT;
    } else {
      return "!" + ABSTRACT;
    }
  }

  //for InitializePackageContents template
  public String determineGetEmfMethod(ASTCDAttribute attribute, ASTCDDefinition astcdDefinition) {
    DecorationHelper decorationHelper = new DecorationHelper();
    if (isExternal(attribute)) {
      return "theASTENodePackage.getENode";
    } else if (isPrimitive(attribute.getType()) || isString(attribute.getType())) {
      return "ecorePackage.getE" + StringTransformations.capitalize(getSimpleNativeAttributeType(attribute.getType()));
    } else if (decorationHelper.isSimpleAstNode(attribute) || decorationHelper.isListAstNode(attribute) || decorationHelper.isOptionalAstNode(attribute)) {
      String grammarName = StringTransformations.uncapitalize(getGrammarFromClass(astcdDefinition, attribute));
      return grammarName + ".get" + StringTransformations.capitalize(getSimpleNativeAttributeType(attribute.getType()));
    } else {
      return "this.get" + StringTransformations.capitalize(getSimpleNativeAttributeType(attribute.getType()));
    }
  }

  public boolean isPrimitive(ASTType type) {
    return type instanceof ASTPrimitiveType;
  }

  public boolean isString(ASTType type) {
    return getSimpleNativeAttributeType(type).equals("String");
  }

  public boolean isLiteralsEnum(ASTCDEnum astcdEnum, String definitionName) {
    return astcdEnum.getName().equals(definitionName + LITERALS_SUFFIX);
  }

  public ASTSimpleReferenceType getEmfAttributeType(ASTCDAttribute astcdAttribute) {
    DecorationHelper decorationHelper = new DecorationHelper();
    if (decorationHelper.isAstNode(astcdAttribute) || decorationHelper.isOptionalAstNode(astcdAttribute)
        || decorationHelper.isListAstNode(astcdAttribute)) {
      return CDTypeFacade.getInstance().createSimpleReferenceType(E_REFERENCE_TYPE);
    } else {
      return CDTypeFacade.getInstance().createSimpleReferenceType(E_ATTRIBUTE_TYPE);
    }
  }

  public Set<String> getEDataTypes(ASTCDDefinition astcdDefinition) {
    //map of <attributeType, attributeName>
    Set<String> eDataTypeMap = new HashSet<>();
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
      for (ASTCDAttribute astcdAttribute : astcdClass.getCDAttributeList()) {
        if (isEDataType(astcdAttribute)) {
          eDataTypeMap.add(getNativeAttributeType(astcdAttribute.getType()));
        }
      }
    }
    return eDataTypeMap;
  }

  public boolean isEDataType(ASTCDAttribute astcdAttribute) {
    DecorationHelper decorationHelper = new DecorationHelper();
    return !decorationHelper.isSimpleAstNode(astcdAttribute) && !decorationHelper.isListAstNode(astcdAttribute) &&
        !decorationHelper.isOptionalAstNode(astcdAttribute) && !isPrimitive(astcdAttribute.getType())
        && !isString(astcdAttribute.getType());
  }

  public boolean isASTNodeInterface(ASTCDInterface astcdInterface, ASTCDDefinition astcdDefinition) {
    return astcdInterface.getName().equals("AST" + astcdDefinition.getName() + "Node");
  }

  public ASTCDDefinition prepareCD(ASTCDDefinition astcdDefinition) {
    ASTCDDefinition copiedDefinition = astcdDefinition.deepClone();
    //remove inherited attributes
    List<ASTCDClass> preparedClasses = copiedDefinition.getCDClassList()
        .stream()
        .map(this::removeInheritedAttributes)
        .collect(Collectors.toList());
    copiedDefinition.setCDClassList(preparedClasses);

    //remove ast node Interface e.g. ASTAutomataNode
    List<ASTCDInterface> astcdInterfaces = copiedDefinition.getCDInterfaceList()
        .stream()
        .filter(x -> !isASTNodeInterface(x, copiedDefinition))
        .collect(Collectors.toList());
    copiedDefinition.setCDInterfaceList(astcdInterfaces);

    //remove inherited attributes
    astcdInterfaces = astcdInterfaces
        .stream()
        .map(this::removeInheritedAttributes)
        .collect(Collectors.toList());
    copiedDefinition.setCDInterfaceList(astcdInterfaces);

    return copiedDefinition;
  }


  public ASTCDClass removeInheritedAttributes(ASTCDClass astCDClass) {
    ASTCDClass copiedAstClass = astCDClass.deepClone();
    //remove inherited attributes
    List<ASTCDAttribute> astcdAttributes = astCDClass.getCDAttributeList()
        .stream()
        .filter(x -> !isInherited(x))
        .collect(Collectors.toList());
    copiedAstClass.setCDAttributeList(astcdAttributes);
    return copiedAstClass;
  }


  public ASTCDInterface removeInheritedAttributes(ASTCDInterface astCDInterface) {
    ASTCDInterface copiedInterface = astCDInterface.deepClone();
    //remove inherited attributes
    List<ASTCDAttribute> astcdAttributes = astCDInterface.getCDAttributeList()
        .stream()
        .filter(x -> !isInherited(x))
        .collect(Collectors.toList());
    copiedInterface.setCDAttributeList(astcdAttributes);
    return copiedInterface;
  }

  public Map<String, String> getSuperTypesOfClass(ASTCDClass astcdClass) {
    //map of <simpleSuperTypeName, package>
    // simpleSuperType: e.g. ASTExpression
    // fitting package: own grammar -> this, from other grammar -> e.g.
    Map<String, String> superTypes = new HashMap<>();
    superTypes.put(getSimpleNativeAttributeType(astcdClass.printSuperClass()), getPackage(astcdClass.printSuperClass()));
    for (ASTReferenceType astReferenceType : astcdClass.getInterfaceList()) {
      superTypes.put(getSimpleNativeAttributeType(astReferenceType), getPackage(TypesPrinter.printType(astReferenceType)));
    }
    return superTypes;
  }

  public String getPackage(String typeName) {
    return typeName.contains(".") ? typeName.substring(0, typeName.lastIndexOf(".")) : "this";
  }
}
