/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesHandler;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Visitor for Derivation of SymType from MCBasicTypes
 * i.e. for
 *    types/MCBasicTypes.mc4
 */
public class SynthesizeSymTypeFromMCBasicTypes extends AbstractSynthesizeFromType implements MCBasicTypesVisitor2, MCBasicTypesHandler {

  protected MCBasicTypesTraverser traverser;

  // ---------------------------------------------------------- Visting Methods

  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */
  @Override
  public void endVisit(ASTMCPrimitiveType primitiveType) {
    String primName = primitiveType.printType();
    Optional<TypeSymbol> prim = getScope(primitiveType.getEnclosingScope()).resolveType(primName);
    if(prim.isPresent()){
      SymTypePrimitive typeConstant =
        SymTypeExpressionFactory.createPrimitive(prim.get());
      getTypeCheckResult().setResult(typeConstant);
      primitiveType.setDefiningSymbol(typeConstant.getTypeInfo());
    }else{
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0111 Cannot find symbol " + primName, primitiveType.get_SourcePositionStart());
    }
  }

  @Override
  public void endVisit(ASTMCVoidType voidType) {
    getTypeCheckResult().setResult(SymTypeExpressionFactory.createTypeVoid());
  }

  @Override
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
    // Search for matching type variables
    Optional<? extends SymTypeExpression> symType = createTypeVariable(qName);

    // If no matching type variables, search for matching types
    if (symType.isEmpty()) {
      symType = createTypeObject(qName);
    }

    // Update result, if still no matching symbol then create sym-type obscure
    this.getTypeCheckResult().setResult(
      symType.isPresent() ? symType.get() : createObscure(qName)
    );
  }

  @Override
  public void endVisit(ASTMCQualifiedType node) {
    if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()){
      node.setDefiningSymbol(getTypeCheckResult().getResult().getTypeInfo());
    }
  }

  /**
   * This method creates a sym-type expression for the first resolved type
   * variable symbol matching the provided qualified name. It logs an error if
   * multiple matching symbols are found. The resulting sym-type expression is
   * encapsulated in an optional.
   * @param qName the qualified name
   * @return an optional of the created sym-type expression, empty if no match
   * type variable symbol is found
   */
  protected Optional<SymTypeVariable> createTypeVariable(ASTMCQualifiedName qName) {
    Preconditions.checkNotNull(qName);

    List<TypeVarSymbol> matches = getScope(qName.getEnclosingScope())
      .resolveTypeVarMany(qName.getQName());
    if (matches.isEmpty()) {
      return Optional.empty();
    } else {
      /* TODO: Enable once CD4A resolving is fixed
      if (matches.size() > 1) {
        Log.error("0xA0325 Reference " + qName.getQName()
            + " matches multiple type variables: "
            + StringUtils.join(matches, ", "),
          qName.get_SourcePositionStart()
        );
      }
      */

      SymTypeVariable symType = SymTypeExpressionFactory.createTypeVariable(matches.get(0));
      return Optional.of(symType);
    }
  }

  /**
   * This method creates a sym-type expression for the first resolved type
   * symbol matching the provided qualified name. It logs an error if
   * multiple matching symbols are found. The resulting sym-type expression is
   * encapsulated in an optional.
   * @param qName the qualified name
   * @return an optional of the created sym-type expression, empty if no match
   * type symbol is found
   */
  protected Optional<SymTypeOfObject> createTypeObject(ASTMCQualifiedName qName) {
    Preconditions.checkNotNull(qName);

    List<TypeSymbol> matches = getScope(qName.getEnclosingScope())
      .resolveTypeMany(qName.getQName());
    if (matches.isEmpty()) {
      return Optional.empty();
    } else {
        if (matches.size() > 1) {
        Log.error("0xA0326 Reference " + qName.getQName()
            + " matches multiple types: "
            + StringUtils.join(matches, ", "),
          qName.get_SourcePositionStart()
        );
      }
      SymTypeOfObject symType = SymTypeExpressionFactory.createTypeObject(matches.get(0));
      return Optional.of(symType);
    }
  }

  /**
   * This method creates a sym-type obscure and logs an error that no symbol
   * matching the provided qualified name can be found.
   * @param qName the qualified name
   * @return a sym-type obscure
   */
  protected SymTypeObscure createObscure(ASTMCQualifiedName qName) {
    Preconditions.checkNotNull(qName);
    Log.error("0xA0324 Cannot find symbol " + qName.getQName(), qName.get_SourcePositionStart());
    return SymTypeExpressionFactory.createObscureType();
  }
}
