/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.cd2java.ast_emf;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.se_rwth.commons.Names;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class EmfAttribute {
  
  private ASTCDType cdType;
  
  /**
   * @return cdtype
   */
  public ASTCDType getCdType() {
    return this.cdType;
  }
  
  /**
   * @param cdtype the cdtype to set
   */
  public void setCdType(ASTCDType cdtype) {
    this.cdType = cdtype;
  }
  
  private ASTCDAttribute cdAttribute;
  
  /**
   * @return cdAttribute
   */
  public ASTCDAttribute getCdAttribute() {
    return this.cdAttribute;
  }
  
  /**
   * @param cdAttribute the cdAttribute to set
   */
  public void setCdAttribute(ASTCDAttribute cdAttribute) {
    this.cdAttribute = cdAttribute;
  }
  
  private String fullName;
  
  /**
   * @return fullName
   */
  public String getFullName() {
    return this.fullName;
  }
  
  /**
   * @param fullName the fullName to set
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
  
  /**
   * @return fullName
   */
  public String getAttributeName() {
    return getCdAttribute().getName();
  }
  
  private boolean isAstNode;
  
  /**
   * @return istAstNode
   */
  public boolean isAstNode() {
    return this.isAstNode;
  }
  
  /**
   * @param istAstNode the istAstNode to set
   */
  public void setAstNode(boolean isAstNode) {
    this.isAstNode = isAstNode;
  }
  
  private boolean isAstList;
  
  /**
   * @return isASTList
   */
  public boolean isAstList() {
    return this.isAstList;
  }
  
  /**
   * @param isASTList the isASTList to set
   */
  public void setAstList(boolean isASTList) {
    this.isAstList = isASTList;
  }
  
  public String getTypeName() {
    return TypesPrinter.printType(getCdAttribute().getType());
  }
  
  public String getDefaultValue() {
    if (isAstNode()) {
      return "null";
    }
    String typeName = getTypeName();
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
  
  public String getEmfType() {
    return isAstNode() ? "EReference" : "EAttribute";
  }
  
  public String getEDataType() {
    String type = Names.getSimpleName(TypesPrinter
        .printTypeWithoutTypeArgumentsAndDimension(getCdAttribute().getType()));
    if (!isAstList()) {
      return type;
    }
    return type.substring(0,
        type.lastIndexOf(GeneratorHelper.LIST_SUFFIX));
  }
  
  public EmfAttribute(
      ASTCDAttribute cdAttribute,
      ASTCDType type,
      String name,
      boolean isAstNode,
      boolean isAstList) {
    this.cdAttribute = cdAttribute;
    this.cdType = type;
    this.fullName = name;
    this.isAstNode = isAstNode;
    this.isAstList = isAstList;
  }
  
}
