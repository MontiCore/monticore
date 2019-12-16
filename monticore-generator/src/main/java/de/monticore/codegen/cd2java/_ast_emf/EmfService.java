/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.se_rwth.commons.StringTransformations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  /**
   * overwrite methods of AbstractService to add the correct '_ast' package for emf generation
   */
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

  /**
   * methods which determine the PackageImplName for a ClassDiagram e.g. AutomataPackageImpl
   */

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

  /**
   * checks if an attribute is an external
   * (only by checking if the attribute name ends with 'Ext')
   */
  public boolean isExternal(ASTCDAttribute attribute) {
    return getNativeTypeName(attribute.getMCType()).endsWith("Ext");
  }

  /**
   * if the attribute type is a ASTType -> returns type of 'EReference'
   * if the attribute type NOT is a ASTType -> returns type of 'EAttribute'
   */
  public ASTMCQualifiedType getEmfAttributeType(ASTCDAttribute astcdAttribute) {
    if (getDecorationHelper().isAstNode(astcdAttribute) || getDecorationHelper().isOptionalAstNode(astcdAttribute)
        || getDecorationHelper().isListAstNode(astcdAttribute)) {
      return getMCTypeFacade().createQualifiedType(E_REFERENCE_TYPE);
    } else {
      return getMCTypeFacade().createQualifiedType(E_ATTRIBUTE_TYPE);
    }
  }

  /**
   * finds all attributes in all classes that are valid Emf attributes
   */
  public Set<String> getEDataTypes(ASTCDDefinition astcdDefinition) {
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
    return !getDecorationHelper().isSimpleAstNode(astcdAttribute) && !getDecorationHelper().isListAstNode(astcdAttribute) &&
        !getDecorationHelper().isOptionalAstNode(astcdAttribute) && !getDecorationHelper().isPrimitive(astcdAttribute.getMCType())
        && !getDecorationHelper().isString(astcdAttribute.printType()) && !isObjectType(astcdAttribute.getMCType())
        && !getDecorationHelper().isMapType(astcdAttribute.printType());
  }

  /**
   * checks if an interface is the LanguageInterface e.g. ASTAutomataNode
   */
  public boolean isASTNodeInterface(ASTCDInterface astcdInterface, ASTCDDefinition astcdDefinition) {
    return astcdInterface.getName().equals("AST" + astcdDefinition.getName() + "Node");
  }

  /**
   * methods for removing inherited attributes
   */
  public ASTCDClass removeInheritedAttributes(ASTCDClass astCDClass) {
    ASTCDClass copiedAstClass = astCDClass.deepClone();
    //remove inherited attributes
    List<ASTCDAttribute> astcdAttributes = removeInheritedAttributes(copiedAstClass.getCDAttributeList());
    copiedAstClass.setCDAttributeList(astcdAttributes);
    return copiedAstClass;
  }

  public ASTCDInterface removeInheritedAttributes(ASTCDInterface astCDInterface) {
    ASTCDInterface copiedInterface = astCDInterface.deepClone();
    //remove inherited attributes
    List<ASTCDAttribute> astcdAttributes = removeInheritedAttributes(copiedInterface.getCDAttributeList());
    copiedInterface.setCDAttributeList(astcdAttributes);
    return copiedInterface;
  }

  private List<ASTCDAttribute> removeInheritedAttributes(List<ASTCDAttribute> astcdAttributeList) {
    return astcdAttributeList
        .stream()
        .filter(x -> !isInheritedAttribute(x))
        .collect(Collectors.toList());
  }

  /**
   * if mcType is not generic -> returns simply printed type
   * if mcType is generic -> returns only printed type argument
   */
  public String getNativeTypeName(ASTMCType astType) {
    // check if type is Generic type like 'List<automaton._ast.ASTState>' -> returns automaton._ast.ASTState
    // if not generic returns simple Type like 'int'
    if (astType instanceof ASTMCGenericType && ((ASTMCGenericType) astType).getMCTypeArgumentList().size() == 1) {
      return ((ASTMCGenericType) astType).getMCTypeArgumentList().get(0).getMCTypeOpt().get()
          .printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
    }
    return astType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
  }

  public String getSimpleNativeType(ASTMCType astType) {
    // check if type is Generic type like 'List<automaton._ast.ASTState>' -> returns ASTState
    // if not generic returns simple Type like 'int'
    String nativeAttributeType = getNativeTypeName(astType);
    return getSimpleNativeType(nativeAttributeType);
  }

  public String getSimpleNativeType(String nativeAttributeType) {
    // check if type is Generic type like 'List<automaton._ast.ASTState>' -> returns ASTState
    // if not generic returns simple Type like 'int'
    if (nativeAttributeType.contains(".")) {
      nativeAttributeType = nativeAttributeType.substring(nativeAttributeType.lastIndexOf(".") + 1);
    }
    if (nativeAttributeType.contains(">")) {
      nativeAttributeType = nativeAttributeType.replaceAll(">", "");
    }
    return nativeAttributeType;
  }

  public String getGrammarFromClass(ASTCDDefinition astcdDefinition, ASTCDAttribute astcdAttribute) {
    String simpleNativeAttributeType = getSimpleNativeType(astcdAttribute.getMCType());
    if (astcdDefinition.getCDClassList().stream().anyMatch(x -> x.getName().equals(simpleNativeAttributeType))) {
      return "this";
    } else {
      List<CDDefinitionSymbol> superCDs = getSuperCDsTransitive(resolveCD(astcdDefinition.getName()));
      for (CDDefinitionSymbol superCD : superCDs) {
        if (superCD.getTypes().stream().anyMatch(x -> x.getName().equals(simpleNativeAttributeType))) {
          return superCD.getName() + "PackageImpl";
        }
      }
    }
    return "this";
  }

  /**
   * methods needed in templates
   */
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
    if (getDecorationHelper().isListType(astType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()))) {
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
    if (isExternal(attribute)) {
      return "theASTENodePackage.getENode";
    } else if (getDecorationHelper().isPrimitive(attribute.getMCType()) || getDecorationHelper().isString(attribute.printType())) {
      return "ecorePackage.getE" + StringTransformations.capitalize(getSimpleNativeType(attribute.getMCType()));
    } else if (isObjectType(attribute.getMCType())) {
      return "ecorePackage.getE" + StringTransformations.capitalize(getSimpleNativeType(attribute.getMCType())) + "Object";
    } else if (getDecorationHelper().isSimpleAstNode(attribute) || getDecorationHelper().isListAstNode(attribute)
        || getDecorationHelper().isOptionalAstNode(attribute)) {
      String grammarName = StringTransformations.uncapitalize(getGrammarFromClass(astcdDefinition, attribute));
      return grammarName + ".get" + StringTransformations.capitalize(getSimpleNativeType(attribute.getMCType()));
    } else if (getDecorationHelper().isMapType(attribute.printType())) {
      return "ecorePackage.getEMap";
    } else {
      return "this.get" + StringTransformations.capitalize(getSimpleNativeType(attribute.getMCType()));
    }
  }

  /**
   * Checks whether the given mc type is a java object type.
   *
   * @param type The input type
   * @return true if the input type is a java object type, false otherwise.
   */
  private boolean isObjectType(ASTMCType type) {
    switch (getSimpleNativeType(type)) {
      case "Boolean":
      case "Short":
      case "Integer":
      case "Long":
      case "Character":
      case "Float":
      case "Double":
      case "java.lang.Boolean":
      case "java.lang.Short":
      case "java.lang.Integer":
      case "java.lang.Long":
      case "java.lang.Character":
      case "java.lang.Float":
      case "java.lang.Double":
        return true;
      default:
        return false;
    }
  }

  public String getPackage(String typeName) {
    return typeName.contains(".") ? typeName.substring(0, typeName.lastIndexOf(".")) : "this";
  }
}
