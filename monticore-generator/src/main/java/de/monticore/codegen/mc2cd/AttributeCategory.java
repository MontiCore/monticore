/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;

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
    if (cdAttribute.getType() instanceof ASTPrimitiveType) {
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
    boolean hasGenerics = ((ASTSimpleReferenceType) cdAttribute.getType()).getTypeArgumentsOpt() != null;
    return "java.util.List".equals(TransformationHelper.typeToString(cdAttribute.getType()))
        && hasGenerics;
  }
  
  private static boolean isOptional(ASTCDAttribute cdAttribute) {
    return "Optional".equals(TransformationHelper.typeToString(cdAttribute.getType()));
  }
  
}
