/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.codegen.cd2java.ast_emf;

import java.util.Optional;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
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
  
  private AstEmfGeneratorHelper astHelper;
  
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
  
  private String createEDataType() {
    if (isAstList || AstEmfGeneratorHelper.istJavaList(getCdAttribute())) {
      Optional<ASTSimpleReferenceType> typeArg = TypesHelper
          .getFirstTypeArgumentOfGenericType(getCdAttribute().getType(), GeneratorHelper.JAVA_LIST);
      if (typeArg.isPresent()) {
        return Names.getSimpleName(TypesHelper
            .printType(typeArg.get()));
      }
    }
    String nativeType = astHelper.getNativeTypeName(getCdAttribute());
    Optional<String> externalType = astHelper.getExternalType(nativeType);
    if (externalType.isPresent()) {
      return externalType.get();
    }
    return Names.getSimpleName(nativeType);
  }
  
  public EmfAttribute(
      ASTCDAttribute cdAttribute,
      ASTCDType type,
      String name,
      String definedGrammar,
      boolean isAstNode,
      boolean isAstList,
      boolean isOptional,
      boolean isInherited, 
      boolean isExternal,
      boolean isEnum,
      AstEmfGeneratorHelper astHelper) {
    this.cdAttribute = cdAttribute;
    this.cdType = type;
    this.fullName = name;
    this.definedGrammar = definedGrammar;
    this.isAstNode = isAstNode;
    this.isAstList = isAstList;
    this.isOptional = isOptional;
    this.astHelper = astHelper;
    this.isInherited = isInherited;
    this.isExternal = isExternal;
    this.isEnum = isEnum;
    this.eDataType = createEDataType();
    this.defaultValue = createDefaultValue();
  }
  
}
