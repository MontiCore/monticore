/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;


import de.monticore.types.mcbasictypes._ast.*;
import de.se_rwth.commons.Names;

import java.util.List;

/**
 * This class provides methods for printing types as Strings. The TypesPrinter
 * is a singleton.
 */
public class BasicTypesPrinter {

  private static BasicTypesPrinter instance;

  /**
   * We have a singleton.
   */
  private BasicTypesPrinter() {
  }
  
  /**
   * Returns the singleton instance.
   * 
   * @return The instance.
   */
  private static BasicTypesPrinter getInstance() {
    if (instance == null) {
      instance = new BasicTypesPrinter();
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
    if (type.isPresentType()) {
      return doPrintType(type.getType());
    }
    if (type.isPresentVoidType()) {
      return doPrintVoidType(type.getVoidType());
    }
    return "";
  }
  
  
  /******************************************************************
   * Rules
   ******************************************************************/
 


  
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
    if (type.getPrimitive() == ASTConstantsMCBasicTypes.BOOLEAN) {
      return "boolean";
    }
    if (type.getPrimitive() == ASTConstantsMCBasicTypes.BYTE) {
      return "byte";
    }
    if (type.getPrimitive() == ASTConstantsMCBasicTypes.CHAR) {
      return "char";
    }
    if (type.getPrimitive() == ASTConstantsMCBasicTypes.SHORT) {
      return "short";
    }
    if (type.getPrimitive() == ASTConstantsMCBasicTypes.INT) {
      return "int";
    }
    if (type.getPrimitive() == ASTConstantsMCBasicTypes.FLOAT) {
      return "float";
    }
    if (type.getPrimitive() == ASTConstantsMCBasicTypes.LONG) {
      return "long";
    }
    if (type.getPrimitive() == ASTConstantsMCBasicTypes.DOUBLE) {
      return "double";
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
      return Names.getQualifiedName(type.getNameList());
    }

    return "";
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

  
}
