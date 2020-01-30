// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolLoader;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.MCBasicTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.monticore.utils.Names;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.StringTransformations;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;

public class DecorationHelper extends MCBasicTypesHelper {

  private static DecorationHelper decorationHelper;

  private DecorationHelper() {
  }

  public static DecorationHelper getInstance() {
    if (decorationHelper == null) {
      decorationHelper = new DecorationHelper();
    }

    return decorationHelper;
  }

  public static final String GET_PREFIX_BOOLEAN = "is";

  public static final String GET_SUFFIX_LIST = "List";

  public static final String GET_PREFIX = "get";

  public static final String SET_PREFIX = "set";

  public String getGeneratedErrorCode(String name) {
    // Use the string representation
    int hashCode = Math.abs(name.hashCode());
    String errorCodeSuffix = String.valueOf(hashCode);
    return "x" + (hashCode < 1000 ? errorCodeSuffix : errorCodeSuffix
        .substring(errorCodeSuffix.length() - 3));
  }

  /**
   * methods which check if the Type is of a special kind
   * e.g. Optional, List, ASTNode, Map
   */
  public boolean isAstNode(ASTCDAttribute attr) {
    if (attr.getModifier().isPresentStereotype()) {
      return attr.getModifier().getStereotype().getValueList().stream().anyMatch(v -> v.getName().equals(MC2CDStereotypes.AST_TYPE.toString()));
    }
    return false;
  }

  public boolean isSimpleAstNode(ASTCDAttribute attr) {
    return !isOptional(attr.getMCType()) && !isListType(attr.printType()) && isAstNode(attr);
  }

  public boolean isOptionalAstNode(ASTCDAttribute attr) {
    return isOptional(attr.getMCType()) && isAstNode(attr);
  }

  public boolean isListAstNode(ASTCDAttribute attribute) {
    return isListType(attribute.printType()) && isAstNode(attribute);
  }

  public boolean isListType(String type) {
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "List".equals(type) || "java.util.List".equals(type)
        || "ArrayList".equals(type) || "java.util.ArrayList".equals(type);
  }

  public boolean isMapType(String type) {
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "Map".equals(type) || "java.util.Map".equals(type);
  }

  public boolean isOptional(String type) {
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "Optional".equals(type) || "java.lang.Optional".equals(type);
  }

  public boolean isOptional(ASTMCType type) {
    if (type instanceof ASTMCOptionalType) {
      return true;
    } else if (type instanceof ASTMCGenericType) {
      String simpleType = ((ASTMCGenericType) type).printWithoutTypeArguments();
      return "Optional".equals(Names.getSimpleName(simpleType));
    }
    return false;
  }

  public boolean isString(String type) {
    return "String".equals(type) || "java.lang.String".equals(type);
  }

  public boolean isPrimitive(ASTMCType type) {
    return type instanceof ASTMCPrimitiveType;
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

  /**
   * adds default declaration to an attribute, by replacing the VALUE template
   * important for Optional and List types
   */
  public void addAttributeDefaultValues(ASTCDAttribute attribute, GlobalExtensionManagement glex) {
    if (isListType(attribute.printType())) {
      glex.replaceTemplate(VALUE, attribute, new StringHookPoint("= new java.util.ArrayList<>()"));

    } else if (isOptional(attribute.getMCType())) {
      glex.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    }
  }

  /**
   * gets attribute Name without the 'r__' prefix
   * this prefix get all attribute names which are also keywords in java e.g. 'final', 'static'
   * remove this prefix to still get setters and getters without the 'r__' prefix
   */
  public String getNativeAttributeName(String attributeName) {
    if (!attributeName.startsWith(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED)) {
      return attributeName;
    }
    return attributeName.substring(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED.length());
  }

  // TODO Alternative für folgende Methoden finden
  public ASTMCTypeArgument getReferenceTypeFromOptional(ASTMCType type) {
    Preconditions.checkArgument(isOptional(type));
    return ((ASTMCGenericType) type).getMCTypeArgumentList().get(0);
  }

  /**
   * methods only used in templates
   */
  public boolean hasOnlyAstAttributes(ASTCDClass type) {
    for (ASTCDAttribute attr : type.getCDAttributeList()) {
      if (!isAstNode(attr)) {
        return false;
      }
    }
    return true;
  }

  public boolean isAttributeOfTypeEnum(ASTCDAttribute attr) {
    if (!attr.isPresentSymbol()) {
      return false;
    }
    CDTypeSymbolLoader attrType = attr.getSymbol().getType();

    List<CDTypeSymbolLoader> typeArgs = attrType.getActualTypeArguments();
    if (typeArgs.size() > 1) {
      return false;
    }

    String typeName = typeArgs.isEmpty()
        ? attrType.getName()
        : typeArgs.get(0).getName();
    if (!typeName.contains(".") && !typeName.startsWith(AST_PREFIX)) {
      return false;
    }

    List<String> listName = Arrays.asList(typeName.split("\\."));
    if (!listName.get(listName.size() - 1).startsWith(AST_PREFIX)) {
      return false;
    }

    if (typeArgs.isEmpty()) {
      return attrType.isSymbolLoaded() && attrType.getLoadedSymbol().isIsEnum();
    }

    CDTypeSymbolLoader typeArgument = typeArgs
        .get(0);
    return typeArgument.isSymbolLoaded() && typeArgument.getLoadedSymbol().isIsEnum();
  }

  /**
   * methods return correct getters or setters for a special attribut
   * needed in templates
   */
  public String getPlainGetter(ASTCDAttribute ast) {
    String astType = ast.getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
    StringBuilder sb = new StringBuilder();
    // Do not use CDTypes.isBoolean() because only primitive boolean uses GET_PREFIX_BOOLEAN
    if (astType.equals("boolean")) {
      sb.append(GET_PREFIX_BOOLEAN);
    } else {
      sb.append(GET_PREFIX);
    }
    sb.append(StringTransformations.capitalize(getNativeAttributeName(ast.getName())));
    if (isListType(astType)) {
      if (ast.getName().endsWith(TransformationHelper.LIST_SUFFIX)) {
        sb.replace(sb.length() - TransformationHelper.LIST_SUFFIX.length(),
            sb.length(), GET_SUFFIX_LIST);
      } else {
        sb.append(GET_SUFFIX_LIST);
      }
    }
    return sb.toString();
  }

  public String getPlainSetter(ASTCDAttribute ast) {
    StringBuilder sb = new StringBuilder(SET_PREFIX).append(
        StringTransformations.capitalize(getNativeAttributeName(ast.getName())));
    String astType = ast.getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
    if (isListType(astType)) {
      if (ast.getName().endsWith(TransformationHelper.LIST_SUFFIX)) {
        sb.replace(sb.length() - TransformationHelper.LIST_SUFFIX.length(),
            sb.length(), GET_SUFFIX_LIST);
      } else {
        sb.append(GET_SUFFIX_LIST);
      }
    }
    return sb.toString();
  }

  /**
   * only needed for templates, so that no instance of the PrettyPrinter has to be created in the template
   */
  public String printType(ASTMCType type) {
    return type.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
  }

}
