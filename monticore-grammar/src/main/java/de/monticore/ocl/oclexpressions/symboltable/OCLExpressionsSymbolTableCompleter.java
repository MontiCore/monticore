// (c) https://github.com/MontiCore/monticore
package de.monticore.ocl.oclexpressions.symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclaration;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclarationVariable;
import de.monticore.ocl.oclexpressions._ast.ASTOCLVariableDeclaration;
import de.monticore.ocl.oclexpressions._ast.ASTTypeIfExpression;
import de.monticore.ocl.oclexpressions._symboltable.IOCLExpressionsScope;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsHandler;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsTraverser;
import de.monticore.ocl.oclexpressions._visitor.OCLExpressionsVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsVisitor2;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

public class OCLExpressionsSymbolTableCompleter
    implements BasicSymbolsVisitor2,
    OCLExpressionsVisitor2,
    OCLExpressionsHandler {

  protected OCLExpressionsTraverser traverser;

  @Deprecated(forRemoval = true)
  protected IDerive deriver;

  @Deprecated(forRemoval = true)
  protected ISynthesize synthesizer;

  /**
   * @deprecated initialize TypeCheck3
   */
  @Deprecated(forRemoval = true)
  public void setDeriver(IDerive deriver) {
    if (deriver != null) {
      this.deriver = deriver;
    }
    else {
      Log.error("0xA3201 The deriver has to be set");
    }
  }

  /**
   * @deprecated initialize TypeCheck3
   */
  @Deprecated(forRemoval = true)
  public void setSynthesizer(ISynthesize synthesizer) {
    if (synthesizer != null) {
      this.synthesizer = synthesizer;
    }
    else {
      Log.error("0xA3204 The synthesizer has to be set");
    }
  }

  @Override
  public OCLExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(OCLExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(IOCLExpressionsScope node) {
    OCLExpressionsHandler.super.traverse(node);
    for (IOCLExpressionsScope subscope : node.getSubScopes()) {
      subscope.accept(this.getTraverser());
    }
  }

  @Override
  public void visit(ASTTypeIfExpression node) {
    // get the shadowing variable that has been added in OCLExpressionsScopesGenitor
    VariableSymbol shadowedSymbol = node.getNameSymbol();
    VariableSymbol shadowingSymbol =
        node.getThenExpression().getSpannedScope().getLocalVariableSymbols().get(0);
    SymTypeExpression shadowedSymbolType = shadowedSymbol.getType();
    SymTypeExpression additionalType = synth(node.getMCType());

    shadowingSymbol.setType(createIntersection(shadowedSymbolType, additionalType));
    shadowingSymbol.setIsReadOnly(true);
  }

  @Override
  public void visit(ASTInDeclaration ast) {
    for (ASTInDeclarationVariable node : ast.getInDeclarationVariableList()) {
      VariableSymbol symbol = node.getSymbol();
      symbol.setIsReadOnly(false);
      if (ast.isPresentMCType()) {
        symbol.setType(synth(ast.getMCType()));
      }
      if (ast.isPresentExpression()) {
        SymTypeExpression exprType = derive(ast.getExpression());
        SymTypeExpression collInnerType = MCCollectionSymTypeRelations
            .getCollectionElementType(exprType);
        if (!ast.isPresentMCType()) {
          // if no MCType present: symbol has type of expression
          symbol.setType(collInnerType);
        }
        else if (!symbol.getType().isObscureType() &&
            !collInnerType.isObscureType()) {
          // if MCType present: check that type of expression
          // and MCType are compatible
          if (!SymTypeRelations.isCompatible(symbol.getType(), collInnerType)) {
            Log.error(
                String.format(
                    "The MCType (%s) and the expression type (%s) in Symbol (%s) are not compatible",
                    ast.getMCType().printType(),
                    collInnerType.printFullName(),
                    symbol.getName()));
          }
        }
      }
    }
  }

  @Override
  public void visit(ASTOCLVariableDeclaration ast) {
    VariableSymbol symbol = ast.getSymbol();
    symbol.setIsReadOnly(false);
    if (ast.isPresentMCType()) {
      ast.getMCType().setEnclosingScope(symbol.getEnclosingScope());
      ast.getMCType().accept(getTraverser());
      symbol.setType(synth(ast.getMCType()));
    }
    if (ast.isPresentExpression()) {
      SymTypeExpression exprType = derive(ast.getExpression());
      if (!ast.isPresentMCType()) {
        symbol.setType(exprType);
      }
      else if (!symbol.getType().isObscureType() &&
          !exprType.isObscureType()) {
        // if MCType present: check that type of expression
        // and MCType are compatible
        if (!SymTypeRelations.isCompatible(symbol.getType(), exprType)) {
          Log.error(
              String.format(
                  "The MCType (%s) and the expression type (%s) in Symbol (%s) are not compatible",
                  ast.getMCType().printType(),
                  exprType.printFullName(),
                  symbol.getName()));
        }
      }
    }
  }

  // allow deprecated code, will be removed with TC1

  private SymTypeExpression synth(ASTMCType mcType) {
    // allow deprecated code
    if (synthesizer != null) {
      TypeCheckResult tcr = synthesizer.synthesizeType(mcType);
      if (!tcr.isPresentResult()) {
        return createObscureType();
      }
      else {
        return tcr.getResult();
      }
    }
    else {
      return TypeCheck3.symTypeFromAST(mcType);
    }
  }

  private SymTypeExpression derive(ASTExpression expr) {
    // allow deprecated code
    if (deriver != null) {
      TypeCheckResult tcr = deriver.deriveType(expr);
      if (!tcr.isPresentResult()) {
        return createObscureType();
      }
      else {
        return tcr.getResult();
      }
    }
    else {
      return TypeCheck3.typeOf(expr);
    }
  }
}
