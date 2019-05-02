package de.monticore.codegen.cd2java.factories;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.types.TypesHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.CDTypes;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.List;

import static de.monticore.codegen.cd2java.ast_new.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java.ast_new.ASTConstants.AST_PREFIX;

public class DecorationHelper extends TypesHelper {

  public static final String OPTIONAL = "Optional";

  public static final String GET_PREFIX_BOOLEAN = "is";

  public static final String GET_SUFFIX_OPTINAL = "Opt";

  public static final String GET_SUFFIX_LIST = "List";

  public static final String GET_PREFIX = "get";

  public static String getGeneratedErrorCode(ASTNode node) {
    int hashCode;
    if (node.isPresentSymbol()) {
      String nodeName = node.getSymbol().getFullName();
      hashCode = Math.abs(node.getClass().getSimpleName().hashCode() + nodeName.hashCode());
    } else { // Else use the string representation
      hashCode = Math.abs(node.toString().hashCode());
    }
    String errorCodeSuffix = String.valueOf(hashCode);
    return "x" + (hashCode < 1000 ? errorCodeSuffix : errorCodeSuffix
        .substring(errorCodeSuffix.length() - 3));
  }

  public boolean isAstNode(ASTCDAttribute attr) {
    if (!attr.isPresentSymbol()) {
      return false;
    }
    if (!(attr.getSymbol() instanceof CDFieldSymbol)) {
      Log.error(String.format("0xA5013 Symbol of ASTCDAttribute %s is not CDFieldSymbol.",
          attr.getName()));
    }
    return isAstNode(((CDFieldSymbol) attr.getSymbol()).getType());
  }


  public boolean isAstNode(CDTypeSymbol type) {
    String typeName = type.getName();
    if (!typeName.contains(".") && !typeName.startsWith(AST_PREFIX)) {
      return false;
    } else {
      List<String> listName = TypesHelper.createListFromDotSeparatedString(typeName);
      if (!listName.get(listName.size() - 1).startsWith(AST_PREFIX)) {
        return false;
      }
    }
    if (!(type instanceof CDTypeSymbolReference)) {
      return type.isClass() || type.isInterface();
    }
    CDTypeSymbolReference attrType = (CDTypeSymbolReference) type;
    if (!attrType.getActualTypeArguments().isEmpty()) {
      return false;
    }
    //todo find if there is a way to check isASTNode before Types are changed
    //because now the types are changed before and 'existsReferencedSymbol' searches for the old Types
    //return attrType.existsReferencedSymbol() && !attrType.isEnum();
    return true;
  }

  public boolean isOptionalAstNode(ASTCDAttribute attr) {
    if (!attr.isPresentSymbol()) {
      return false;
    }
    if (!(attr.getSymbol() instanceof CDFieldSymbol)) {
      Log.error(String.format("0xA5014 Symbol of ASTCDAttribute %s is not CDFieldSymbol.",
          attr.getName()));
    }
    return isOptionalAstNode(((CDFieldSymbol) attr.getSymbol()).getType());
  }

  public boolean isOptionalAstNode(CDTypeSymbolReference type) {
    if (!type.getName().equals(OPTIONAL)) {
      return false;
    }
    List<ActualTypeArgument> typeArgs = type.getActualTypeArguments();
    if (typeArgs.size() != 1) {
      return false;
    }

    if (!(typeArgs.get(0).getType() instanceof CDTypeSymbolReference)) {
      return false;
    }
    return isAstNode((CDTypeSymbolReference) typeArgs.get(0).getType());
  }

  public boolean isListAstNode(ASTCDAttribute attribute) {
    if (!attribute.isPresentSymbol()) {
      return false;
    }
    if (!(attribute.getSymbol() instanceof CDFieldSymbol)) {
      Log.error(String.format("0xA5012 Symbol of ASTCDAttribute %s is not CDFieldSymbol.",
          attribute.getName()));
    }
    return isListAstNode(((CDFieldSymbol) attribute.getSymbol()).getType());
  }

  public boolean isListAstNode(CDTypeSymbolReference type) {
    if (!isListType(type.getName())) {
      return false;
    }
    List<ActualTypeArgument> typeArgs = type.getActualTypeArguments();
    if (typeArgs.size() != 1) {
      return false;
    }

    if (!(typeArgs.get(0).getType() instanceof CDTypeSymbolReference)) {
      return false;
    }
    return isAstNode((CDTypeSymbolReference) typeArgs.get(0).getType());
  }

  public boolean hasOnlyAstAttributes(ASTCDClass type) {
    for (ASTCDAttribute attr : type.getCDAttributeList()) {
      if (!isAstNode(attr)) {
        return false;
      }
    }
    return true;
  }

  public static String getAstClassNameForASTLists(CDTypeSymbolReference field) {
    List<ActualTypeArgument> typeArgs = field.getActualTypeArguments();
    if (typeArgs.size() != 1) {
      return AST_INTERFACE;
    }

    if (!(typeArgs.get(0).getType() instanceof CDTypeSymbolReference)) {
      return AST_INTERFACE;
    }
    return ((CDTypeSymbolReference) typeArgs.get(0).getType()).getStringRepresentation();
  }

  public static String getAstClassNameForASTLists(ASTCDAttribute attr) {
    if (!attr.isPresentSymbol()) {
      return "";
    }
    if (!(attr.getSymbol() instanceof CDFieldSymbol)) {
      Log.error(String.format("0xA04125 Symbol of ASTCDAttribute %s is not CDFieldSymbol.",
          attr.getName()));
    }
    return getAstClassNameForASTLists(((CDFieldSymbol) attr.getSymbol()).getType());
  }

  public static String getNativeAttributeName(String attributeName) {
    if (!attributeName.startsWith(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED)) {
      return attributeName;
    }
    return attributeName.substring(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED.length());
  }

  public static boolean isListType(String type) {
    // TODO : use symbol table
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "List".equals(type) || "java.util.List".equals(type)
        || "ArrayList".equals(type) || "java.util.ArrayList".equals(type);
  }


  public static boolean isString(String type) {
    return "String".equals(type) || "java.lang.String".equals(type);
  }

  public boolean isAttributeOfTypeEnum(ASTCDAttribute attr) {
    if (!attr.isPresentSymbol() || !(attr.getSymbol() instanceof CDFieldSymbol)) {
      return false;
    }
    CDTypeSymbolReference attrType = ((CDFieldSymbol) attr.getSymbol()
    ).getType();

    List<ActualTypeArgument> typeArgs = attrType.getActualTypeArguments();
    if (typeArgs.size() > 1) {
      return false;
    }

    String typeName = typeArgs.isEmpty()
        ? attrType.getName()
        : typeArgs.get(0).getType().getName();
    if (!typeName.contains(".") && !typeName.startsWith(AST_PREFIX)) {
      return false;
    }

    List<String> listName = TypesHelper.createListFromDotSeparatedString(typeName);
    if (!listName.get(listName.size() - 1).startsWith(AST_PREFIX)) {
      return false;
    }

    if (typeArgs.isEmpty()) {
      return attrType.existsReferencedSymbol() && attrType.isEnum();
    }

    CDTypeSymbolReference typeArgument = (CDTypeSymbolReference) typeArgs
        .get(0).getType();
    return typeArgument.existsReferencedSymbol() && typeArgument.isEnum();
  }

  public static String getPlainGetter(ASTCDAttribute ast) {
    String astType = printType(ast.getType());
    StringBuilder sb = new StringBuilder();
    if (CDTypes.isBoolean(astType)) {
      sb.append(GET_PREFIX_BOOLEAN);
    } else {
      sb.append(GET_PREFIX);
    }
    sb.append(StringTransformations.capitalize(getNativeAttributeName(ast.getName())));
    if (isOptional(ast.getType())) {
      sb.append(GET_SUFFIX_OPTINAL);
    } else if (isListType(astType)) {
      if (ast.getName().endsWith(TransformationHelper.LIST_SUFFIX)) {
        sb.replace(sb.length() - TransformationHelper.LIST_SUFFIX.length(),
            sb.length(), GET_SUFFIX_LIST);
      } else {
        sb.append(GET_SUFFIX_LIST);
      }
    }
    return sb.toString();
  }

}
