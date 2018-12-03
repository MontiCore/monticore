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
  public static String printType(ASTMCType type) {
    return getInstance().doPrintType(type);
  }
  
  protected String doPrintType(ASTMCType type) {

    if (type instanceof ASTMCPrimitiveType) {
      return doPrintPrimitiveType((ASTMCPrimitiveType) type);
    }
    if (type instanceof ASTMCReferenceType) {
      return doPrintReferenceType((ASTMCReferenceType) type);
    }
    return "";
  }
  
  /**
   * Converts an ASTReferenceType to a String
   *
   * @param type ASTReferenceType to be converted
   * @return String representation of "type"
   */
  public static String printReferenceType(ASTMCReferenceType type) {
    return getInstance().doPrintReferenceType(type);
  }

  protected String doPrintReferenceType(ASTMCReferenceType type) {

    return Names.getQualifiedName(type.getNameList());
  }

  /**
   * Converts an ASTReturnType to a String
   *
   * @param type ASTReturnType to be converted
   * @return String representation of "type"
   */
  public static String printReturnType(ASTMCReturnType type) {
    return getInstance().doPrintReturnType(type);
  }
  
  protected String doPrintReturnType(ASTMCReturnType type) {
    if (type.isPresentMCType()) {
      return doPrintType(type.getMCType());
    }
    if (type.isPresentMCVoidType()) {
      return doPrintVoidType(type.getMCVoidType());
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
  public static String printVoidType(ASTMCVoidType type) {
    return getInstance().doPrintVoidType(type);
  }
  
  protected String doPrintVoidType(ASTMCVoidType type) {
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
  public static String printPrimitiveType(ASTMCPrimitiveType type) {
    return getInstance().doPrintPrimitiveType(type);
  }
  
  protected String doPrintPrimitiveType(ASTMCPrimitiveType type) {
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
  public static String printReferenceTypeList(List<ASTMCReferenceType> type) {
    return getInstance().doPrintReferenceTypeList(type);
  }
  
  protected String doPrintReferenceTypeList(List<ASTMCReferenceType> type) {
    StringBuilder ret = new StringBuilder();
    if (type != null) {
      String sep = "";
      for (ASTMCReferenceType refType : type) {
        ret.append(sep + doPrintReferenceType(refType));
        sep = ", ";
      }
    }
    return ret.toString();
  }

}
