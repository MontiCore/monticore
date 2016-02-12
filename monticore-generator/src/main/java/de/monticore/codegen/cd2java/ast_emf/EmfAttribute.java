/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.cd2java.ast_emf;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class EmfAttribute {
  
  private ASTCDType cdType;
  
  private String eDataType;
  
  private String defaultValue;
  
  private String definedGrammar;
  
  /**
   * @return definedGrammar
   */
  public String getDefinedGrammar() {
    return this.definedGrammar;
  }
  
  /**
   * @return defaultValue
   */
  public String getDefaultValue() {
    return this.defaultValue;
  }
  
  /**
   * @return eDataType
   */
  public String getEDataType() {
    return this.eDataType;
  }
  
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
  
  private boolean hasExternalType;
  
  /**
   * @return hasExternalType
   */
  public boolean hasExternalType() {
    return this.hasExternalType;
  }
  
  /**
   * @param hasExternalType the hasExternalType to set
   */
  public void setHasExternalType(boolean hasExternalType) {
    this.hasExternalType = hasExternalType;
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
  
  private boolean isExternal;
  
  /**
   * @return isExternal
   */
  public boolean isExternal() {
    return this.isExternal;
  }
  
  /**
   * @return name of the attribute
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
  
  private boolean isOptional;
  
  private boolean isInherited;
  
  private boolean isEnum;
  
  /**
   * @return isEnum
   */
  public boolean isEnum() {
    return this.isEnum;
  }
  
  /**
   * @return isInherited
   */
  public boolean isInherited() {
    return this.isInherited;
  }
  
  /**
   * @return isOptionalAstNode
   */
  public boolean isOptional() {
    return this.isOptional;
  }
  
  /**
   * @param isOptionalAstNode the isOptionalAstNode to set
   */
  public void setOptional(boolean isOptional) {
    this.isOptional = isOptional;
  }
  
  /**
   * @param isASTList the isASTList to set
   */
  public void setAstList(boolean isASTList) {
    this.isAstList = isASTList;
  }
  
  public String getTypeName() {
    return getCdAttribute().printType();
  }
  
  private String createDefaultValue() {
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
    return (isAstNode() || isAstList()) ? "EReference" : "EAttribute";
  }
  
  public boolean isEAttribute() {
    return "EAttribute".equals(getEmfType());
  }
  
  public boolean isEReference() {
    return "EReference".equals(getEmfType());
  }
  
  public EmfAttribute(
      ASTCDAttribute cdAttribute,
      ASTCDType type,
      String name,
      String eDataType,
      String definedGrammar,
      boolean isAstNode,
      boolean isAstList,
      boolean isOptional,
      boolean isInherited,
      boolean isExternal,
      boolean isEnum,
      boolean hasExternalType) {
    this.cdAttribute = cdAttribute;
    this.cdType = type;
    this.fullName = name;
    this.definedGrammar = definedGrammar;
    this.isAstNode = isAstNode;
    this.isAstList = isAstList;
    this.isOptional = isOptional;
    this.isInherited = isInherited;
    this.isExternal = isExternal;
    this.isEnum = isEnum;
    this.hasExternalType = hasExternalType;
    this.eDataType = eDataType;
    this.defaultValue = createDefaultValue();
  }
  
  public String toString() {
    return "+ " + getCdAttribute().getName() + " of " + getCdAttribute().printType() + " EType "
        + getEmfType() + " + ";
  }
  
}
