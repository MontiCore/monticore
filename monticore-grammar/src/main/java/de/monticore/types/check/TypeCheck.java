/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types.check.SymTypeConstant.unbox;

/**
 * This class is intended to provide typeChecking functionality.
 * It is designed as functional class (no state), allowing to
 * plug-in the appropriate implementation through subclasses,
 * Those subclasses can deal with variants of Expression, Literal
 * and Type-classes that are used in the respective project.
 * (It is thus configure along three dimensions:
 *    Literals
 *    Expressions
 *    Types)
 * This class only knows about the thre top Level grammars:
 * MCBasicTypes, ExpressionsBasis and MCLiteralsBasis, because it includes their
 * main NonTerminals in the signature.
 */
public class TypeCheck {
  
  /**
   * Configuration: Visitor for Function 1:
   * Synthesizing the SymTypeExpression from an AST Type.
   * May also be of a subclass;
   */
  protected ISynthesize iSynthesize;
  
  /**
   * Configuration: Visitor for Function 2b:
   * Deriving the SymTypeExpression from an AST Value - Literal.
   * May also be of a subclass;
   */
  protected IDerive iDerive;
  

  /**
   * Configuration as state:
   * @param synthesizeSymType defines, which AST Types are mapped (and how)
   * @param  iDerive defines, which AST Literals are handled
   *                               through the Expression type recognition
   */
  public TypeCheck(ISynthesize synthesizeSymType,
                   IDerive iDerive) {
    this.iSynthesize = synthesizeSymType;
    this.iDerive = iDerive;
  }
  
  /*************************************************************************/
  

  /**
   * Function 1: extracting the SymTypeExpression from an AST Type
   * The SymTypeExpression is independent of the AST and can be stored in the SymTab etc.
   *
   * Tests for this Function are combined in the Visitor tests
   * (SynthesizeSymType.*Types.*Test)
   */
  public SymTypeExpression symTypeFromAST(ASTMCType ast)
  {
    iSynthesize.init();
    ast.accept(iSynthesize.getTraverser());
    Optional<SymTypeExpression> result = iSynthesize.getResult();
    if(!result.isPresent()) {
      Log.error("0xE9FD4 Internal Error: No SymType for: "
              + ast.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()) + ". Probably TypeCheck mis-configured.");
    }
    return result.get();
  }
  

  /**
   * Function 1b: extracting the SymTypeExpression from the AST Type "void"
   * ("void" is not in the ASTMCType hierarchy, while it is included in the SymTypeExpressions)
   */
  public SymTypeExpression symTypeFromAST(ASTMCVoidType ast) 
  {
    return SymTypeExpressionFactory.createTypeVoid();
  }
  

  /**
   * Function 1c: extracting the SymTypeExpression from the AST MCReturnType
   * (MCReturnType is not in the ASTMCType hierarchy, while it is included in the SymTypeExpressions)
   *
   * Tests for this Function are combined in the Visitor tests
   * (SynthesizeSymType.*Types.*Test)
   */
  public SymTypeExpression symTypeFromAST(ASTMCReturnType ast)
  {
    iSynthesize.init();
    ast.accept(iSynthesize.getTraverser());
    Optional<SymTypeExpression> result = iSynthesize.getResult();
    if(!result.isPresent()) {
      Log.error("0xE9FD5 Internal Error: No SymType for return type: "
              + ast.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter())
              + ". Probably TypeCheck mis-configured.");
    }
    return result.get();
  }


  /**
   * Function 1d: extracting the SymTypeExpression from the AST MCQualifiedName
   *
   * Tests for this Function are combined in the Visitor tests
   * (SynthesizeSymType.*Types.*Test)
   */
  public SymTypeExpression symTypeFromAST(ASTMCQualifiedName ast)
  {
    iSynthesize.init();
    ast.accept(iSynthesize.getTraverser());
    Optional<SymTypeExpression> result = iSynthesize.getResult();
    if(!result.isPresent()) {
      Log.error("0xE9FD5 Internal Error: No SymType for MCQualifiedName: "
              + ast.getBaseName()
              + ". Probably TypeCheck mis-configured.");
    }
    return result.get();
  }


  /*************************************************************************/
  

  /**
   * Function 2: Derive the SymTypeExpression from an Expression AST
   * This defines the Type that an Expression has.
   * Precondition:
   * Free Variables in the AST are being looked u through the Symbol Table that
   * needs to be in place; same for method calls etc.
   */
  public SymTypeExpression typeOf(ASTExpression expr)
  {
    iDerive.init();
    expr.accept(iDerive.getTraverser());
    Optional<SymTypeExpression> result = iDerive.getResult();
    if(!result.isPresent()) {
      Log.error("0xED680 Internal Error: No Type for Expression " + expr
              + " Probably TypeCheck mis-configured.");
    }
    return result.get();
  }
  

  /**
   * Function 2b: Derive the SymTypeExpression of a Literal
   * This defines the Type that a Literal has and will be used to
   * determine the Type of Expressions.
   *
   * Tests for this Function are combined in the Visitor tests
   * (DeriveSymType.*Literals.*Test)
   */
  public SymTypeExpression typeOf(ASTLiteral lit)
  {
    iDerive.init();
    lit.accept(iDerive.getTraverser());
    Optional<SymTypeExpression> result = iDerive.getResult();
    if(!result.isPresent()) {
      Log.error("0xED670 Internal Error: No Type for Literal " + lit
              + " Probably TypeCheck mis-configured.");
    }
    return result.get();
  }



  /*************************************************************************/
  
  /**
   * Function 3:
   * Given two SymTypeExpressions super, sub:
   * This function answers, whether the right type is a subtype of the left type in an assignment.
   * (This allows to store/use values of type "sub" at all positions of type "super".
   * Compatibility examples:
   *      compatible("int", "long")       (in all directions)
   *      compatible("long", "int")       (in all directions)
   *      compatible("double", "float")   (in all directions)
   *      compatible("Person", "Student") (uni-directional)
   *
   * Incompatible:
   *     !compatible("double", "int")   (in all directions)
   *
   * The concrete Typechecker has to decide on further issues, like
   *     !compatible("List<double>", "List<int>") 
   *     where e.g. Java and OCL/P differ in their answers
   *
   * @param left  Super-Type
   * @param right  Sub-Type (assignment-compatible to supertype?)
   *
   * TODO: Probably needs to be extended for free type-variable assignments
   * (because it may be that they get unified over time: e.g. Map<a,List<c>> and Map<long,b>
   * are compatible, by refining the assignments a-> long, b->List<c>
   *
   * TODO: remove "static" keyword
   */
  public static boolean compatible(SymTypeExpression left,
                                   SymTypeExpression right)
  {
    if(left.isTypeConstant()&&right.isTypeConstant()){
      SymTypeConstant leftType = (SymTypeConstant) left;
      SymTypeConstant rightType = (SymTypeConstant) right;
      if(isBoolean(leftType)&&isBoolean(rightType)){
        return true;
      }
      if(isDouble(leftType)&&rightType.isNumericType()){
        return true;
      }
      if(isFloat(leftType)&&((rightType.isIntegralType())||isFloat(right))){
        return true;
      }
      if(isLong(leftType)&&rightType.isIntegralType()){
        return true;
      }
      if(isInt(leftType)&&rightType.isIntegralType()&&!isLong(right)){
        return true;
      }
      if(isChar(leftType)&&isChar(right)){
        return true;
      }
      if(isShort(leftType)&&isShort(right)){
        return true;
      }
      if(isByte(leftType)&&isByte(right)){
        return true;
      }
      return false;
    } else if (!left.isTypeConstant() && right.isNullType()){
      return true;
    } else if(unbox(left.printFullName()).equals(unbox(right.printFullName()))) {
      return true;
    } else if(isSubtypeOf(right,left)) {
      return true;
    } else if (right.printFullName().equals(left.printFullName())) {
      return true;
    } else if (left.deepEquals(right) || right.deepEquals(left)) {
      return true;
    }else if (left.getTypeInfo().getName().equals(right.getTypeInfo().getName())){
      return true;
    }
    return false;
  }
  
  /*************************************************************************/
  

  /**
   * Function 4:
   * Checks whether the ASTExpression exp will result in a value that is of type, and
   * thus can be e.g. stored, sent, etc. Essentially exp needs to be of a subtype to
   * be assignment compatible.
   * (as it is combined from other functions, it need not be overwritten)
   * @param exp  the Expression that shall be checked for a given type
   * @param type the Type it needs to have (e.g. the Type of a variable used for assignment, or the
   *             type of a channel where to send a value)
   */
  public boolean isOfTypeForAssign(SymTypeExpression type,
                                   ASTExpression exp)
  {
    return compatible(  type, typeOf(exp));
    // that is all what is needed
  }



  /**
   * determines if one SymTypeExpression is a subtype of another SymTypeExpression
   * @param subType the SymTypeExpression that could be a subtype of the other SymTypeExpression
   * @param superType the SymTypeExpression that could be a supertype of the other SymTypeExpression
   */
  public static boolean isSubtypeOf(SymTypeExpression subType, SymTypeExpression superType){
    if(subType.isTypeConstant()&&superType.isTypeConstant()) {
      SymTypeConstant sub = (SymTypeConstant) subType;
      SymTypeConstant supert = (SymTypeConstant) superType;
      if (isDouble(supert) && sub.isNumericType() &&!isDouble(sub)) {
        return true;
      }
      if (isFloat(supert) && sub.isIntegralType()) {
        return true;
      }
      if (isLong(supert) && sub.isIntegralType() && !isLong(subType)) {
        return true;
      }
      if (isInt(supert) && sub.isIntegralType() && !isLong(subType) && !isInt(subType)) {
        return true;
      }
      return false;
    }else if((subType.isTypeConstant() && !superType.isTypeConstant()) ||
        (superType.isTypeConstant() && !subType.isTypeConstant())){
      return false;
    }
    return isSubtypeOfRec(subType,superType);
  }


  /**
   * private recursive helper method for the method isSubTypeOf
   * @param subType the SymTypeExpression that could be a subtype of the other SymTypeExpression
   * @param superType the SymTypeExpression that could be a supertype of the other SymTypeExpression
   */
  private static boolean isSubtypeOfRec(SymTypeExpression subType, SymTypeExpression superType){
    if (!subType.getTypeInfo().getSuperTypesList().isEmpty()) {
      for (SymTypeExpression type : subType.getTypeInfo().getSuperTypesList()) {
        if(type.print().equals(superType.print())){
          return true;
        }
      }
    }
    boolean subtype = false;
    for (int i = 0; i < subType.getTypeInfo().getSuperTypesList().size(); i++) {
      if (isSubtypeOf(subType.getTypeInfo().getSuperTypesList().get(i), superType)) {
        subtype=true;
        break;
      }
    }
    return subtype;
  }

  public static boolean isBoolean(SymTypeExpression type)
  {
    return "boolean".equals(unbox(type.print()));
  }

  public static boolean isInt(SymTypeExpression type)
  {
    return "int".equals(unbox(type.print()));
  }

  public static boolean isDouble(SymTypeExpression type)
  {
    return "double".equals(unbox(type.print()));
  }

  public static boolean isFloat(SymTypeExpression type)
  {
    return "float".equals(unbox(type.print()));
  }

  public static boolean isLong(SymTypeExpression type)
  {
    return "long".equals(unbox(type.print()));
  }

  public static boolean isChar(SymTypeExpression type)
  {
    return "char".equals(unbox(type.print()));
  }

  public static boolean isShort(SymTypeExpression type)
  {
    return "short".equals(unbox(type.print()));
  }

  public static boolean isByte(SymTypeExpression type)
  {
    return "byte".equals(unbox(type.print()));
  }

  public static boolean isVoid(SymTypeExpression type)
  {
    return "void".equals(unbox(type.print()));
  }

  public static boolean isString(SymTypeExpression type)
  {
    return "String".equals(type.print());
  }
  
  
}

