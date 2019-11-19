/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;

public class EmfService extends AbstractService {

  protected static final String ABSTRACT = "IS_ABSTRACT";

  public EmfService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public EmfService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return ASTConstants.AST_PACKAGE;
  }

  @Override
  protected ASTService createService(CDDefinitionSymbol cdSymbol) {
    return createEmfService(cdSymbol);
  }


  public static ASTService createEmfService(CDDefinitionSymbol cdSymbol) {
    return new ASTService(cdSymbol);
  }

  public String getQualifiedPackageImplName() {
    return getQualifiedPackageImplName(getCDSymbol());
  }

  public String getQualifiedPackageImplName(CDDefinitionSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), getSimplePackageImplName(cdSymbol));
  }

  public String getSimplePackageImplName() {
    return getSimplePackageImplName(getCDSymbol());
  }

  public String getSimplePackageImplName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + PACKAGE_IMPL_SUFFIX;
  }

  public String getSimplePackageImplName(String cdSymbolQualifiedName) {
    return getSimplePackageImplName(resolveCD(cdSymbolQualifiedName));
  }

  public boolean isExternal(ASTCDAttribute attribute) {
    return getNativeTypeName(attribute.getMCType()).endsWith("Ext");
  }

  //for InitializePackageContents template
  public String getClassPackage(CDTypeSymbol cdTypeSymbol) {
    if (cdTypeSymbol.getModelName().equalsIgnoreCase(getQualifiedCDName())) {
      return "this";
    } else {
      return StringTransformations.uncapitalize(getSimplePackageImplName(cdTypeSymbol.getModelName()));
    }
  }

  //for InitializePackageContents template
  public String determineListInteger(ASTMCType astType) {
    if (DecorationHelper.isListType(astType.printType())) {
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
    } else if (isPrimitive(attribute.getMCType()) || isString(attribute.getMCType())) {
      return "ecorePackage.getE" + StringTransformations.capitalize(getSimpleNativeType(attribute.getMCType()));
    } else if (decorationHelper.isSimpleAstNode(attribute) || decorationHelper.isListAstNode(attribute) || decorationHelper.isOptionalAstNode(attribute)) {
      String grammarName = StringTransformations.uncapitalize(getGrammarFromClass(astcdDefinition, attribute));
      return grammarName + ".get" + StringTransformations.capitalize(getSimpleNativeType(attribute.getMCType()));
    } else {
      return "this.get" + StringTransformations.capitalize(getSimpleNativeType(attribute.getMCType()));
    }
  }

  public boolean isPrimitive(ASTMCType type) {
    return type instanceof ASTMCPrimitiveType;
  }

  public boolean isString(ASTMCType type) {
    return "String".equals(getSimpleNativeType(type));
  }

  public ASTMCQualifiedType getEmfAttributeType(ASTCDAttribute astcdAttribute) {
    DecorationHelper decorationHelper = new DecorationHelper();
    if (decorationHelper.isAstNode(astcdAttribute) || decorationHelper.isOptionalAstNode(astcdAttribute)
        || decorationHelper.isListAstNode(astcdAttribute)) {
      return MCTypeFacade.getInstance().createQualifiedType(E_REFERENCE_TYPE);
    } else {
      return MCTypeFacade.getInstance().createQualifiedType(E_ATTRIBUTE_TYPE);
    }
  }

  public Set<String> getEDataTypes(ASTCDDefinition astcdDefinition) {
    //map of <attributeType, attributeName>
    Set<String> eDataTypeMap = new HashSet<>();
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
      for (ASTCDAttribute astcdAttribute : astcdClass.getCDAttributeList()) {
        if (isEDataType(astcdAttribute)) {
          eDataTypeMap.add(getNativeTypeName(astcdAttribute.getMCType()));
        }
      }
    }
    return eDataTypeMap;
  }

  public boolean isEDataType(ASTCDAttribute astcdAttribute) {
    DecorationHelper decorationHelper = new DecorationHelper();
    return !decorationHelper.isSimpleAstNode(astcdAttribute) && !decorationHelper.isListAstNode(astcdAttribute) &&
        !decorationHelper.isOptionalAstNode(astcdAttribute) && !isPrimitive(astcdAttribute.getMCType())
        && !isString(astcdAttribute.getMCType());
  }

  public boolean isASTNodeInterface(ASTCDInterface astcdInterface, ASTCDDefinition astcdDefinition) {
    return astcdInterface.getName().equals("AST" + astcdDefinition.getName() + "Node");
  }

  public ASTCDDefinition prepareCDForEmfPackageDecoration(ASTCDDefinition astcdDefinition) {
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
    superTypes.put(getSimpleNativeType(astcdClass.printSuperClass()), getPackage(astcdClass.printSuperClass()));
    for (ASTMCObjectType astReferenceType : astcdClass.getInterfaceList()) {
      superTypes.put(getSimpleNativeType(astReferenceType), getPackage(astReferenceType.printType()));
    }
    return superTypes;
  }

  public String getPackage(String typeName) {
    return typeName.contains(".") ? typeName.substring(0, typeName.lastIndexOf(".")) : "this";
  }
}
