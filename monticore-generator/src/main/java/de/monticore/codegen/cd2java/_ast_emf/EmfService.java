/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.*;

public class EmfService extends AbstractService<EmfService> {

  protected static final String ABSTRACT = "IS_ABSTRACT";

  public EmfService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public EmfService(DiagramSymbol cdSymbol) {
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
  protected EmfService createService(DiagramSymbol cdSymbol) {
    return createEmfService(cdSymbol);
  }

  public static EmfService createEmfService(DiagramSymbol cdSymbol) {
    return new EmfService(cdSymbol);
  }

  /**
   * methods which determine the PackageImplName for a ClassDiagram e.g. AutomataPackageImpl
   */

  public String getQualifiedPackageImplName() {
    return getQualifiedPackageImplName(getCDSymbol());
  }

  public String getQualifiedPackageImplName(DiagramSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), getSimplePackageImplName(cdSymbol));
  }

  public String getSimplePackageImplName() {
    return getSimplePackageImplName(getCDSymbol());
  }

  public String getSimplePackageImplName(DiagramSymbol cdSymbol) {
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
    return getDecorationHelper().getNativeTypeName(attribute.getMCType()).endsWith("Ext");
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
    Set<String> eDataTypeMap = new LinkedHashSet<>();
    for (ASTCDClass astcdClass : astcdDefinition.getCDClassesList()) {
      eDataTypeMap.addAll(getEDataTypes(astcdClass));
    }
    for (ASTCDInterface astcdInterface : astcdDefinition.getCDInterfacesList()) {
      eDataTypeMap.addAll(getEDataTypes(astcdInterface));
    }
    return eDataTypeMap;
  }

  public Set<String> getEDataTypes(ASTCDType astcdType) {
    Set<String> eDataTypeMap = new LinkedHashSet<>();
    for (ASTCDAttribute astcdAttribute : astcdType.getCDAttributeList()) {
      if (isEDataType(astcdAttribute) && !isInheritedAttribute(astcdAttribute)) {
        eDataTypeMap.add(getDecorationHelper().getNativeTypeName(astcdAttribute.getMCType()));
      }
    }
    return eDataTypeMap;
  }

  public boolean isEDataType(ASTCDAttribute astcdAttribute) {
    return !getDecorationHelper().isSimpleAstNode(astcdAttribute) && !getDecorationHelper().isListAstNode(astcdAttribute) &&
        !getDecorationHelper().isOptionalAstNode(astcdAttribute) && !getDecorationHelper().isPrimitive(astcdAttribute.getMCType())
        && !getDecorationHelper().isString(CD4CodeMill.prettyPrint(astcdAttribute.getMCType(), false)) && !isObjectType(astcdAttribute.getMCType())
        && !getDecorationHelper().isMapType(CD4CodeMill.prettyPrint(astcdAttribute.getMCType(), false));
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
    //remove inherited attributes
    List<ASTCDAttribute> astcdAttributes = removeInheritedAttributes(astCDClass.getCDAttributeList());
    astCDClass.setCDAttributeList(astcdAttributes);
    return astCDClass;
  }

  public ASTCDInterface removeInheritedAttributes(ASTCDInterface astCDInterface) {
    //remove inherited attributes
    List<ASTCDAttribute> astcdAttributes = removeInheritedAttributes(astCDInterface.getCDAttributeList());
    astCDInterface.setCDAttributeList(astcdAttributes);
    return astCDInterface;
  }

  protected List<ASTCDAttribute> removeInheritedAttributes(List<ASTCDAttribute> astcdAttributeList) {
    return astcdAttributeList
        .stream()
        .filter(x -> !isInheritedAttribute(x))
        .collect(Collectors.toList());
  }

  public String getGrammarFromClass(ASTCDDefinition astcdDefinition, ASTCDAttribute astcdAttribute) {
    String simpleNativeAttributeType = getDecorationHelper().getSimpleNativeType(astcdAttribute.getMCType());
    if (astcdDefinition.getCDClassesList().stream().anyMatch(x -> x.getName().equals(simpleNativeAttributeType))) {
      return "this";
    } else {
      List<DiagramSymbol> superCDs = getSuperCDsTransitive(astcdDefinition.getSymbol());
      for (DiagramSymbol superCD : superCDs) {
        if (getAllCDTypes(superCD
        ).stream().anyMatch(x -> x.getName().equals(simpleNativeAttributeType))) {
          return superCD.getName() + "PackageImpl";
        }
      }
    }
    return "this";
  }
  
  //for InitializePackageContents template
  public List<CDTypeSymbol> retrieveSuperTypes(ASTCDClass c) {
    List<CDTypeSymbol> superTypes = Lists.newArrayList();
    c.getSymbol().getSuperTypesList().stream()
            .map(s -> s.getTypeInfo())
            .forEach(t -> {if(t instanceof CDTypeSymbol && ((CDTypeSymbol)t).isIsInterface()) superTypes.add((CDTypeSymbol) t);});
    return superTypes;
  }

  /**
   * methods needed in templates
   */
  //for InitializePackageContents template
  public String getClassPackage(CDTypeSymbol cdTypeSymbol) {
    // in this version the scope carries all relevant naming information and we
    // know that it is an artifact scope
    ICD4CodeArtifactScope scope = ((ICD4CodeArtifactScope) cdTypeSymbol.getEnclosingScope().getEnclosingScope());
    String modelName = scope.getFullName();
    if (modelName.equalsIgnoreCase(getQualifiedCDName())) {
      return "this";
    } else {
      return StringTransformations.uncapitalize(getSimplePackageImplName(modelName));
    }
  }

  //for InitializePackageContents template
  public String determineListInteger(ASTMCType astType) {
    if (getDecorationHelper().isListType(CD4CodeMill.prettyPrint(astType, false))) {
      return "-1";
    } else {
      return "1";
    }
  }

  //for InitializePackageContents template
  public String determineAbstractString(ASTCDClass cdClass) {
    if (cdClass.getModifier().isAbstract()) {
      return ABSTRACT;
    } else {
      return "!" + ABSTRACT;
    }
  }

  //for InitializePackageContents template
  public String determineGetEmfMethod(ASTCDAttribute attribute, ASTCDDefinition astcdDefinition) {
    if (isExternal(attribute)) {
      return "theASTENodePackage.getENode";
    } else if (getDecorationHelper().isPrimitive(attribute.getMCType()) || getDecorationHelper().isString(CD4CodeMill.prettyPrint(attribute.getMCType(), false))) {
      return "ecorePackage.getE" + StringTransformations.capitalize(getDecorationHelper().getSimpleNativeType(attribute.getMCType()));
    } else if (isObjectType(attribute.getMCType())) {
      return "ecorePackage.getE" + StringTransformations.capitalize(getDecorationHelper().getSimpleNativeType(attribute.getMCType())) + "Object";
    } else if (getDecorationHelper().isSimpleAstNode(attribute) || getDecorationHelper().isListAstNode(attribute)
        || getDecorationHelper().isOptionalAstNode(attribute)) {
      String grammarName = StringTransformations.uncapitalize(getGrammarFromClass(astcdDefinition, attribute));
      return grammarName + ".get" + StringTransformations.capitalize(getDecorationHelper().getSimpleNativeType(attribute.getMCType()));
    } else if (getDecorationHelper().isMapType(CD4CodeMill.prettyPrint(attribute.getMCType(), false))) {
      return "ecorePackage.getEMap";
    } else {
      return "this.get" + StringTransformations.capitalize(getDecorationHelper().getSimpleNativeType(attribute.getMCType()));
    }
  }

  /**
   * Checks whether the given mc type is a java object type.
   *
   * @param type The input type
   * @return true if the input type is a java object type, false otherwise.
   */
  protected boolean isObjectType(ASTMCType type) {
    switch (getDecorationHelper().getSimpleNativeType(type)) {
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

  /**
   * default values for return types in templates
   */
  public String getDefaultValue(ASTCDAttribute attribute) {
    if (getDecorationHelper().isAstNode(attribute)) {
      return "null";
    }
    if (getDecorationHelper().isOptional(attribute.getMCType())) {
      return "Optional.empty()";
    }
    String typeName = CD4CodeMill.prettyPrint(attribute.getMCType(), false);
    switch (typeName) {
      case "boolean":
        return "false";
      case "int":
        return "0";
      case "short":
        return "(short) 0";
      case "long":
        return "0";
      case "float":
        return "0.0f";
      case "double":
        return "0.0";
      case "char":
        return "'\u0000'";
      default:
        return "null";
    }
  }
}
