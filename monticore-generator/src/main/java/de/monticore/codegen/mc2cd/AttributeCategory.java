/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;


import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;

/**
 * An enumeration of the different categories that ASTCDAttributes in a CD AST can fall into.
 */
public enum AttributeCategory {
  
  /**
   * a primitive attribute such as boolean, int or char
   */
  PRIMITIVE,
  /**
   * an attribute wrapped by an Optional such as 'Optional<String>'
   */
  OPTIONAL,
  /**
   * a standard attribute such as 'String s'
   */
  STANDARD,
  /**
   * a reference to a generic List such as 'List<String>'
   */
  GENERICLIST;
  
  public static AttributeCategory determineCategory(ASTCDAttribute cdAttribute) {
    if (cdAttribute.getMCType() instanceof ASTMCPrimitiveType) {
      return PRIMITIVE;
    }
    if (isGenericList(cdAttribute)) {
      return GENERICLIST;
    }
    if (isOptional(cdAttribute)) {
      return OPTIONAL;
    }
    return STANDARD;
  }
  
  private static boolean isGenericList(ASTCDAttribute cdAttribute) {
    return cdAttribute.getMCType() instanceof ASTMCListType;
  }
  
  private static boolean isOptional(ASTCDAttribute cdAttribute) {
    return cdAttribute.getMCType() instanceof ASTMCOptionalType;
  }
  
}
