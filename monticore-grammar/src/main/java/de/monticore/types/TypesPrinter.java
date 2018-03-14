/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import java.util.List;

/**
 * @deprecated STATE DEL PN will be implemented with new symbol table concept
 */

import de.monticore.types.types._ast.ASTArrayType;
import de.monticore.types.types._ast.ASTComplexReferenceType;
import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTTypeArguments;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types.types._ast.ASTVoidType;
import de.monticore.types.types._ast.ASTWildcardType;
import de.se_rwth.commons.Names;

/**
 * This class provides methods for printing types as Strings. The TypesPrinter
 * is a singleton.
 * 
 * @author Martin Schindler
 */
public class TypesPrinter {
  
  private static TypesPrinter instance;
  
  /**
   * We have a singleton.
   */
  private TypesPrinter() {
  }
  
  /**
   * Returns the singleton instance.
   * 
   * @return The instance.
   */
  private static TypesPrinter getInstance() {
    if (instance == null) {
      instance = new TypesPrinter();
    }
    return instance;
  }
  
  /******************************************************************
   * INTERFACES
   ******************************************************************/
  
  /**
   * Converts an ASTType to a String
   * 
   * @param type ASTType to be converted
   * @return String representation of "type"
   */
  public static String printType(ASTType type) {
    return getInstance().doPrintType(type);
  }
  
  protected String doPrintType(ASTType type) {
    if (type instanceof ASTArrayType) {
      return doPrintArrayType((ASTArrayType) type);
    }
    if (type instanceof ASTPrimitiveType) {
      return doPrintPrimitiveType((ASTPrimitiveType) type);
    }
    if (type instanceof ASTReferenceType) {
      return doPrintReferenceType((ASTReferenceType) type);
    }
    return "";
  }
  
  /**
   * Converts an ASTReferenceType to a String
   *
   * @param type ASTReferenceType to be converted
   * @return String representation of "type"
   */
  public static String printReferenceType(ASTReferenceType type) {
    return getInstance().doPrintReferenceType(type);
  }

  protected String doPrintReferenceType(ASTReferenceType type) {
    if (type instanceof ASTSimpleReferenceType) {
      return doPrintSimpleReferenceType((ASTSimpleReferenceType) type);
    }
    if (type instanceof ASTComplexReferenceType) {
      return doPrintComplexReferenceType((ASTComplexReferenceType) type);
    }
    return "";
  }

  /**
   * Converts an ASTReturnType to a String
   * 
   * @param type ASTReturnType to be converted
   * @return String representation of "type"
   */
  public static String printReturnType(ASTReturnType type) {
    return getInstance().doPrintReturnType(type);
  }
  
  protected String doPrintReturnType(ASTReturnType type) {
    if (type instanceof ASTType) {
      return doPrintType((ASTType) type);
    }
    if (type instanceof ASTVoidType) {
      return doPrintVoidType((ASTVoidType) type);
    }
    return "";
  }
  
  /**
   * Converts an ASTTypeArgument to a String
   * 
   * @param type ASTTypeArgument to be converted
   * @return String representation of "type"
   */
  public static String printTypeArgument(ASTTypeArgument type) {
    return getInstance().doPrintTypeArgument(type);
  }
  
  protected String doPrintTypeArgument(ASTTypeArgument type) {
    if (type instanceof ASTWildcardType) {
      return doPrintWildcardType((ASTWildcardType) type);
    }
    if (type instanceof ASTType) {
      return doPrintType((ASTType) type);
    }
    return "";
  }

  /**
   * Converts an ASTType to a String, but omits type arguments
   *
   * @param type ASTType to be converted
   * @return String representation of "type" without type arguments
   */
  public static String printTypeWithoutTypeArguments(ASTType type) {
    return getInstance().doPrintTypeWithoutTypeArguments(type);
  }

  protected String doPrintTypeWithoutTypeArguments(ASTType type) {
    if (type instanceof ASTArrayType) {
      return doPrintArrayType((ASTArrayType) type);
    }
    if (type instanceof ASTPrimitiveType) {
      return doPrintPrimitiveType((ASTPrimitiveType) type);
    }
    if (type instanceof ASTReferenceType) {
      return doPrintReferenceTypeWithoutTypeArguments((ASTReferenceType) type);
    }
    return "";
  }
  
  /**
   * Converts an ASTType to a String, but omits type arguments
   *
   * @param type ASTType to be converted
   * @return String representation of "type" without type arguments
   */
  public static String printTypeWithoutTypeArgumentsAndDimension(ASTType type) {
    return getInstance().doPrintTypeWithoutTypeArgumentsAndDimension(type);
  }

  protected String doPrintTypeWithoutTypeArgumentsAndDimension(ASTType type) {
    if (type instanceof ASTArrayType) {
      return doPrintTypeWithoutTypeArgumentsAndDimension(((ASTArrayType) type).getComponentType());
    }
    if (type instanceof ASTPrimitiveType) {
      return doPrintPrimitiveType((ASTPrimitiveType) type);
    }
    if (type instanceof ASTReferenceType) {
      return doPrintTypeWithoutTypeArguments((ASTReferenceType) type);
    }
    return "";
  }
  
  /******************************************************************
   * Rules
   ******************************************************************/
  
  /**
   * Converts ASTTypeParameters to a String
   * 
   * @param params ASTTypeParameters to be converted
   * @return String representation of "params"
   */
  public static String printTypeParameters(ASTTypeParameters params) {
    return getInstance().doPrintTypeParameters(params);
  }
  
  protected String doPrintTypeParameters(ASTTypeParameters params) {
    if (params != null && params.getTypeVariableDeclarationList() != null && !params.getTypeVariableDeclarationList().isEmpty()) {
      return "<" + doPrintTypeVariableDeclarationList(params.getTypeVariableDeclarationList()) + ">";
    }
    return "";
  }
  
  /**
   * Converts an ASTTypeVariableDeclarationList to a String
   * 
   * @param decl ASTTypeVariableDeclarationList to be converted
   * @return String representation of "decl"
   */
  public static String printTypeVariableDeclarationList(List<ASTTypeVariableDeclaration> decl) {
    return getInstance().doPrintTypeVariableDeclarationList(decl);
  }
  
  protected String doPrintTypeVariableDeclarationList(List<ASTTypeVariableDeclaration> decl) {
    StringBuilder ret = new StringBuilder();
    if (decl != null) {
      String sep = "";
      for (ASTTypeVariableDeclaration d : decl) {
        ret.append(sep + doPrintTypeVariableDeclaration(d));
        sep = ", ";
      }
    }
    return ret.toString();
  }
  
  /**
   * Converts an ASTTypeVariableDeclaration to a String
   * 
   * @param decl ASTTypeVariableDeclaration to be converted
   * @return String representation of "decl"
   */
  public static String printTypeVariableDeclaration(ASTTypeVariableDeclaration decl) {
    return getInstance().doPrintTypeVariableDeclaration(decl);
  }
  
  protected String doPrintTypeVariableDeclaration(ASTTypeVariableDeclaration decl) {
    StringBuilder ret = new StringBuilder();
    if (decl != null) {
      ret.append(decl.getName());
      if (decl.getUpperBoundList() != null && !decl.getUpperBoundList().isEmpty()) {
        String sep = " extends ";
        for (ASTType type : decl.getUpperBoundList()) {
          ret.append(sep + doPrintType(type));
          sep = " & ";
        }
      }
    }
    return ret.toString();
  }
  
  /**
   * Converts an ASTVoidType to a String
   * 
   * @param type ASTVoidType to be converted
   * @return String representation of "type"
   */
  public static String printVoidType(ASTVoidType type) {
    return getInstance().doPrintVoidType(type);
  }
  
  protected String doPrintVoidType(ASTVoidType type) {
    if (type != null) {
      return "void";
    }
    return "";
  }
  
  /**
   * Converts an ASTPrimitiveType to a String
   * 
   * @param type ASTPrimitiveType to be converted
   * @return String representation of "type"
   */
  public static String printPrimitiveType(ASTPrimitiveType type) {
    return getInstance().doPrintPrimitiveType(type);
  }
  
  protected String doPrintPrimitiveType(ASTPrimitiveType type) {
    if (type == null) {
      return "";
    }
    if (type.getPrimitive() == ASTConstantsTypes.BOOLEAN) {
      return "boolean";
    }
    if (type.getPrimitive() == ASTConstantsTypes.BYTE) {
      return "byte";
    }
    if (type.getPrimitive() == ASTConstantsTypes.CHAR) {
      return "char";
    }
    if (type.getPrimitive() == ASTConstantsTypes.SHORT) {
      return "short";
    }
    if (type.getPrimitive() == ASTConstantsTypes.INT) {
      return "int";
    }
    if (type.getPrimitive() == ASTConstantsTypes.FLOAT) {
      return "float";
    }
    if (type.getPrimitive() == ASTConstantsTypes.LONG) {
      return "long";
    }
    if (type.getPrimitive() == ASTConstantsTypes.DOUBLE) {
      return "double";
    }
    return "";
  }
  
  /**
   * Converts an ASTArrayType to a String
   * 
   * @param type ASTArrayType to be converted
   * @return String representation of "type"
   */
  public static String printArrayType(ASTArrayType type) {
    return getInstance().doPrintArrayType(type);
  }
  
  protected String doPrintArrayType(ASTArrayType type) {
    if (type != null) {
      StringBuilder dimension = new StringBuilder();
      dimension.append(doPrintType(type.getComponentType()));
      for (int i = 0; i < type.getDimensions(); i++) {
        dimension.append("[]");
      }
      return dimension.toString();
    }
    return "";
  }
  
  /**
   * Converts an ASTReferenceTypeList to a String
   * 
   * @param type ASTReferenceTypeList to be converted
   * @return String representation of "type"
   */
  public static String printReferenceTypeList(List<ASTReferenceType> type) {
    return getInstance().doPrintReferenceTypeList(type);
  }
  
  protected String doPrintReferenceTypeList(List<ASTReferenceType> type) {
    StringBuilder ret = new StringBuilder();
    if (type != null) {
      String sep = "";
      for (ASTReferenceType refType : type) {
        ret.append(sep + doPrintReferenceType(refType));
        sep = ", ";
      }
    }
    return ret.toString();
  }
  
  /**
   * Converts an ASTSimpleReferenceType to a String
   * 
   * @param type ASTSimpleReferenceType to be converted
   * @return String representation of "type"
   */
  public static String printSimpleReferenceType(ASTSimpleReferenceType type) {
    return getInstance().doPrintSimpleReferenceType(type);
  }
  
  protected String doPrintSimpleReferenceType(ASTSimpleReferenceType type) {
    if (type != null) {
      if(type.isPresentTypeArguments()) {
        return Names.getQualifiedName(type.getNameList()) + doPrintTypeArguments(type.getTypeArguments());
      }
      else {
        return Names.getQualifiedName(type.getNameList());
      }
    }

    return "";
  }
  
  /**
   * Converts an ComplexReferenceType to a String
   * 
   * @param type ComplexReferenceType to be converted
   * @return String representation of "type"
   */
  public static String printComplexReferenceType(ASTComplexReferenceType type) {
    return getInstance().doPrintComplexReferenceType(type);
  }
  
  protected String doPrintComplexReferenceType(ASTComplexReferenceType type) {
    String ret = "";
    if (type != null && type.getSimpleReferenceTypeList() != null) {
      return doPrintSimpleReferenceTypeList(type.getSimpleReferenceTypeList());
    }
    return ret;
  }
  
  /**
   * Converts an ASTSimpleReferenceTypeList to a String
   * @param type ComplexReferenceType to be converted
   * @return String representation of "type"
   */
  public static String printSimpleReferenceTypeList(List<ASTSimpleReferenceType> type) {
    return getInstance().doPrintSimpleReferenceTypeList(type);
  }

  protected String doPrintSimpleReferenceTypeList(List<ASTSimpleReferenceType> argList) {
    StringBuilder ret = new StringBuilder();
    if (argList != null) {
      String sep = "";
      for (ASTSimpleReferenceType arg : argList) {
        ret.append(sep + doPrintSimpleReferenceType(arg));
        sep = ". ";
      }
    }
    return ret.toString();
  }
  
  /**
   * Converts ASTTypeArguments to a String
   * 
   * @param args ASTTypeArguments to be converted
   * @return String representation of "args"
   */
  public static String printTypeArguments(ASTTypeArguments args) {
    return getInstance().doPrintTypeArguments(args);
  }
  
  protected String doPrintTypeArguments(ASTTypeArguments args) {
    if (args != null && args.getTypeArgumentList() != null && !args.getTypeArgumentList().isEmpty()) {
      return "<" + doPrintTypeArgumentList(args.getTypeArgumentList()) + ">";
    }
    return "";
  }
  
  /**
   * Converts an ASTTypeArgumentList to a String
   * 
   * @param argList ASTTypeArgumentList to be converted
   * @return String representation of "argList"
   */
  public static String printTypeArgumentList(List<ASTTypeArgument> argList) {
    return getInstance().doPrintTypeArgumentList(argList);
  }
  
  protected String doPrintTypeArgumentList(List<ASTTypeArgument> argList) {
    StringBuilder ret = new StringBuilder();
    if (argList != null) {
      String sep = "";
      for (ASTTypeArgument arg : argList) {
        ret.append(sep + doPrintTypeArgument(arg));
        sep = ", ";
      }
    }
    return ret.toString();
  }
  
  /**
   * Converts an ASTWildcardType to a String
   * 
   * @param type ASTWildcardType to be converted
   * @return String representation of "type"
   */
  public static String printWildcardType(ASTWildcardType type) {
    return getInstance().doPrintWildcardType(type);
  }
  
  protected String doPrintWildcardType(ASTWildcardType type) {
    StringBuilder ret = new StringBuilder();
    if (type != null) {
      ret.append("?");
      if (type.isPresentUpperBound()) {
        ret.append(" extends " + doPrintType(type.getUpperBound()));
      }
      else if (type.isPresentLowerBound()) {
        ret.append(" super " + doPrintType(type.getLowerBound()));
      }
    }
    return ret.toString();
  }



  protected String doPrintReferenceTypeWithoutTypeArguments(ASTReferenceType type) {
    if (type instanceof ASTSimpleReferenceType) {
      return doPrintSimpleReferenceTypeWithoutTypeArguments((ASTSimpleReferenceType) type);
    }
    if (type instanceof ASTComplexReferenceType) {
      return doPrintComplexReferenceTypeWithoutTypeArguments((ASTComplexReferenceType) type);
    }
    return "";
  }

  protected String doPrintSimpleReferenceTypeWithoutTypeArguments(ASTSimpleReferenceType type) {
    if (type != null) {
      return Names.getQualifiedName(type.getNameList());
    }

    return "";
  }

  protected String doPrintComplexReferenceTypeWithoutTypeArguments(ASTComplexReferenceType type) {
    if (type != null && type.getSimpleReferenceTypeList() != null) {
      return doPrintSimpleReferenceTypeListWithoutTypeArguments(type.getSimpleReferenceTypeList());
    }
    return "";
  }

  protected String doPrintSimpleReferenceTypeListWithoutTypeArguments(List<ASTSimpleReferenceType> argList) {
    StringBuilder ret = new StringBuilder();
    if (argList != null) {
      String sep = "";
      for (ASTSimpleReferenceType arg : argList) {
        ret.append(sep + doPrintSimpleReferenceTypeWithoutTypeArguments(arg));
        sep = ". ";
      }
    }
    return ret.toString();
  }
  
}
