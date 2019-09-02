package de.monticore.types2;

import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.Optional;

/**
 * Visitor for Derivation of SymType from MCBasicTypes
 * i.e. for
 *    types/MCBasicTypes.mc4
 */
public class SynthesizeSymTypeFromMCCollectionTypes extends  SynthesizeSymTypeFromMCBasicTypes
                                                    implements MCCollectionTypesVisitor {
  
  public SynthesizeSymTypeFromMCCollectionTypes() { }
  
  /**
   * Using the visitor functionality to calculate the SymType Expression
   */

  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCCollectionTypesVisitor realThis = this;
  
  @Override
  public void setRealThis(MCCollectionTypesVisitor realThis) {
    this.realThis = realThis;
    super.realThis = realThis;  // not necessarily needed, but to be safe ...
  }
  
  @Override
  public MCCollectionTypesVisitor getRealThis() {
    return realThis;
  }
  // ---------------------------------------------------------- realThis end
  
  /**
   * Storage in the Visitor: result of the last endVisit
   * is inherited
   * This attribute is synthesized upward.
   */

  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  // TODO Bug: Eigentlich sollte die EndVisit-Methode reichen,
  // aber der Visitor hat mit astrule_Extensions ein Problem
  // (in der Grammatik steht:)
  //   MCListType implements MCGenericType <200> =
  //       {next("List")}? Name "<" mCTypeArgument:MCTypeArgument ">";
  //  astrule MCListType =
  //    mCTypeArgument:de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument*
  //    name:String*
  //  ;
  // und deshalb schreiben wir hie rvon Hand auch die Traversal
  // (um die Childs (in dem Fall nur eines) auch zu erreichen):
  
  public void traverse(ASTMCListType node) {
    if (null != node.getMCTypeArgumentList()) {
      // darf eigentlich nur 1 Argument sein
      // (deshalb speichern wir auch das result nicht zwischen)
      for(ASTMCTypeArgument a : node.getMCTypeArgumentList() ) {
        a.accept(getRealThis());
      }
    }
  }
  
  public void endVisit(ASTMCListType t) {
    System.out.println("\nSynC XXX ev List 1:" +result);
// argument Type has been processed and stored in result:
    SymTypeExpression tex =
            SymTypeExpressionFactory.createGenericTypeExpression(
                      "List", Arrays.asList(result.get()));
    if(!result.isPresent()) {
      Log.error("0xE9FD6 Internal Error: No SymType argument for List type. "
              + " Probably TypeCheck mis-configured.");
    }
    result = Optional.of(tex);
    System.out.println("\nSynC XXX ev List End:" +result);
  }
  
  public void endVisit(ASTMCSetType t) {
    System.out.println("\nXXX ev Set 1:" +result);
    // argument Type has been processed and stored in result:
    SymTypeExpression tex =
            SymTypeExpressionFactory.createGenericTypeExpression(
                    "Set", Arrays.asList(result.get()));
    if(!result.isPresent()) {
      Log.error("0xE9FD7 Internal Error: No SymType argument for Set type. "
              + " Probably TypeCheck mis-configured.");
    }
    result = Optional.of(tex);
    System.out.println("\nXXX ev Set End:" +result);
  }
  
  public void endVisit(ASTMCOptionalType t) {
    System.out.println("\nXXX ev Optional 1:" +result);
    // argument Type has been processed and stored in result:
    SymTypeExpression tex =
            SymTypeExpressionFactory.createGenericTypeExpression(
                    "Optional", Arrays.asList(result.get()));
    if(!result.isPresent()) {
      Log.error("0xE9FD8 Internal Error: No SymType argument for Optional type. "
              + " Probably TypeCheck mis-configured.");
    }
    result = Optional.of(tex);
    System.out.println("\nXXX ev Optional End:" +result);
  }
  
  /**
   * Map has two arguments: to retrieve them properly, the traverse method
   * is adapted (looking deeper into the visitor), instead of the endVisit method:
   *
   */
/*
  public void traverse(ASTMCMapType node) {
    System.out.println("\nXXX TraverseStart:" );
    // Argument 1:
    if (null != node.getKey()) {
      node.getKey().accept(getRealThis());
    }
    System.out.println("\nXXX Traverse 1:");
    if(!result.isPresent()) {
      Log.error("0xE9FDA Internal Error: Missing SymType argument 1 for Map type. "
              + " Probably TypeCheck mis-configured.");
    }
    SymTypeExpression argument1 = result.get();
  
    System.out.println("\nXXX Traverse 2:");
    // Argument 2:
    if (null != node.getValue()) {
      node.getValue().accept(getRealThis());
    }
    if(!result.isPresent()) {
      Log.error("0xE9FDA Internal Error: Missing SymType argument 1 for Map type. "
              + " Probably TypeCheck mis-configured.");
    }
    SymTypeExpression argument2 = result.get();
    // Construct new TypeExpression:
    System.out.println("\nXXX Traverse 3:");
    SymTypeExpression tex =
            SymTypeExpressionFactory.createGenericTypeExpression(
                    "Map", Arrays.asList(argument1,argument2));
    result = Optional.of(tex);
    System.out.println("\nXXX TraverseEnd:" );
  }
*/
  
    // ASTMCTypeArgument, ASTMCBasicTypeArgument and  MCPrimitiveTypeArgument:
    // Do nothing, because result already contains the argument
    // (because: MCG contains:
    // interface MCTypeArgument;
    // MCBasicTypeArgument implements MCTypeArgument <200> =
    //       MCQualifiedType;
    //
    // MCPrimitiveTypeArgument implements MCTypeArgument <190> =
    //       MCPrimitiveType;
  
}
