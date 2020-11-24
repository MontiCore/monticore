/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesHandler;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;


/**
 * Visitor for Derivation of SymType from MCBasicTypes
 * i.e. for
 *    types/MCBasicTypes.mc4
 */
public class SynthesizeSymTypeFromMCBasicTypes extends AbstractSynthesizeFromType implements MCBasicTypesVisitor2, MCBasicTypesHandler, ISynthesize {

  protected MCBasicTypesTraverser traverser;

  // ---------------------------------------------------------- Visting Methods

  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  public void endVisit(ASTMCPrimitiveType primitiveType) {
    SymTypeConstant typeConstant =
            SymTypeExpressionFactory.createTypeConstant(primitiveType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()));
    typeCheckResult.setCurrentResult(typeConstant);
  }
  
  public void endVisit(ASTMCVoidType voidType) {
    typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeVoid());
  }
  
  /**
   * Asks the SymTypeExpressionFactory to create the correct Type
   * Here: the Argument may be qualified Type object, but that allows only primitives, such as "int" or
   * boxed versions, such as "java.lang.Boolean"
   * This are the only qualified Types that may occur.
   * In particular: This method needs to be overriden when real qualified Types occur.
   * @param qType
   */
  public void endVisit(ASTMCQualifiedType qType) {
    // Otherwise the Visitor is applied to the wrong AST (and an internal error 0x893F62 is issued
    typeCheckResult.setCurrentResult(
        createTypeObject(qType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()), getScope(qType.getEnclosingScope())));
  }
  
  public void endVisit(ASTMCReturnType rType) {
    // result is pushed upward (no change)
  }

  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCBasicTypesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void endVisit(ASTMCQualifiedName qName) {
    TypeSymbol loader = new OOTypeSymbolSurrogate(qName.getQName());
    loader.setEnclosingScope(getScope(qName.getEnclosingScope()));
    SymTypeOfObject oType = createTypeObject(loader);
    typeCheckResult.setCurrentResult(oType);
  }


}
