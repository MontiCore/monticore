/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;


import de.monticore.types.mcbasictypes._ast.*;
import de.se_rwth.commons.Names;

import java.util.List;

/**
 * This class provides methods for printing types as Strings. The BasicTypesPrinter
 * is a singleton.
 *
 * Care: It is not extensible (as it does not fully implement the static delegator pattern)
 */
public class BasicTypesPrinter {

  private static BasicTypesPrinter instance;

  /**
   * We have a singleton.
   */
  protected BasicTypesPrinter() {
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
    if (type instanceof ASTMCObjectType) {
      return doPrintObjectType((ASTMCObjectType) type);
    }
    return "vxgcnfnhnhjggf";
  }
  
  /**
   * Converts an ASTMCObjectType to a String
   *
   * @param type ASTMCObjectType to be converted
   * @return String representation of "type"
   */
  public static String printObjectType(ASTMCObjectType type) {
    return getInstance().doPrintObjectType(type);
  }

  protected String doPrintObjectType(ASTMCObjectType type) {

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
   * Converts an ASTObjectTypeList to a String
   *
   * @param type ASTObjectTypeList to be converted
   * @return String representation of "type"
   */
  public static String printObjectTypeList(List<ASTMCObjectType> type) {
    return getInstance().doPrintObjectTypeList(type);
  }
  
  protected String doPrintObjectTypeList(List<ASTMCObjectType> type) {
    StringBuilder ret = new StringBuilder();
    if (type != null) {
      String sep = "";
      for (ASTMCObjectType refType : type) {
        ret.append(sep + doPrintObjectType(refType));
        sep = ", ";
      }
    }
    return ret.toString();
  }

}
