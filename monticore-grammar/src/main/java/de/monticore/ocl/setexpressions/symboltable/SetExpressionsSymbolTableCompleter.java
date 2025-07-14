// (c) https://github.com/MontiCore/monticore
package de.monticore.ocl.setexpressions.symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.setexpressions._ast.ASTGeneratorDeclaration;
import de.monticore.ocl.setexpressions._ast.ASTSetVariableDeclaration;
import de.monticore.ocl.setexpressions._symboltable.ISetExpressionsScope;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsHandler;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsTraverser;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsVisitor2;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

public class SetExpressionsSymbolTableCompleter implements
    SetExpressionsVisitor2, BasicSymbolsVisitor2, SetExpressionsHandler {

  protected static final String LOG_NAME =
    SetExpressionsSymbolTableCompleter.class.getName();

  @Deprecated
  IDerive deriver;

  @Deprecated
  ISynthesize synthesizer;

  protected SetExpressionsTraverser traverser;

  @Deprecated
  public void setDeriver(IDerive deriver) {
    if (deriver != null) {
      this.deriver = deriver;
    }
    else {
      Log.error("0xA3201 The deriver has to be set");
    }
  }

  @Deprecated
  public void setSynthesizer(ISynthesize synthesizer) {
    if (synthesizer != null) {
      this.synthesizer = synthesizer;
    }
    else {
      Log.error("0xA3204 The synthesizer has to be set");
    }
  }

  @Override
  public SetExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(SetExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ISetExpressionsScope node) {
    SetExpressionsHandler.super.traverse(node);
    for (ISetExpressionsScope subscope : node.getSubScopes()) {
      subscope.accept(this.getTraverser());
    }
  }

  @Override
  public void visit(ASTSetVariableDeclaration node) {
  }

  @Override
  public void endVisit(ASTSetVariableDeclaration node) {
    initialize_SetVariableDeclaration(node.getSymbol(), node);
  }

  protected void initialize_SetVariableDeclaration(
      VariableSymbol symbol, ASTSetVariableDeclaration ast) {
    symbol.setIsReadOnly(false);
    if (ast.isPresentMCType()) {
      ast.getMCType().setEnclosingScope(symbol.getEnclosingScope());
      ast.getMCType().accept(getTraverser());
      SymTypeExpression mcType = synth(ast.getMCType());
      if (mcType.isObscureType()) {
        // TC already logged error, though
        Log.error("0xA3027 The type " + ast.getMCType().printType()
                + " of the object " + ast.getName()
                + " could not be calculated",
            ast.get_SourcePositionStart(),
            ast.get_SourcePositionEnd()
        );
      }
      symbol.setType(mcType);
    }
    else {
      if (ast.isPresentExpression()) {
        ast.getExpression().accept(getTraverser());
        SymTypeExpression tcr_expr = derive(ast.getExpression());
        if (tcr_expr.isObscureType()) {
          // TC already logged error, though
          Log.error("0xA3026 The type of the object "
                  + ast.getName() + " could not be calculated",
              ast.get_SourcePositionStart(),
              ast.get_SourcePositionEnd()
          );
        }
        symbol.setType(tcr_expr);
      }
      else {
        Log.debug("No type given for VariableDeclaration "
            + ast.getName() + ", thus selecting Object",
            ast.get_SourcePositionStart(),
            ast.get_SourcePositionEnd(),
            LOG_NAME
        );
        symbol.setType(
            SymTypeExpressionFactory.createTypeObject("Object", ast.getEnclosingScope()));
      }
    }
  }

  @Override
  public void endVisit(ASTGeneratorDeclaration node) {
    initialize_GeneratorDeclaration(node.getSymbol(), node);
  }

  protected void initialize_GeneratorDeclaration(VariableSymbol symbol, ASTGeneratorDeclaration ast) {
    symbol.setIsReadOnly(false);
    if (ast.isPresentMCType()) {
      ast.getMCType().setEnclosingScope(symbol.getEnclosingScope());
      ast.getMCType().accept(getTraverser());
      SymTypeExpression mcType = synth(ast.getMCType());
      if (mcType.isObscureType()) {
        // TC already logged error, though
        Log.error("0xA3023 The type " + ast.getMCType().printType()
                + " of the object " + ast.getName()
                + " could not be calculated",
            ast.get_SourcePositionStart(),
            ast.get_SourcePositionEnd()
        );
      }
      symbol.setType(mcType);
    }
    else {
      SymTypeExpression exprType = derive(ast.getExpression());
      if (exprType.isObscureType()) {
        Log.error("0xA3024 The type of the object "
                + ast.getName() + " could not be calculated",
            ast.get_SourcePositionStart(),
            ast.get_SourcePositionEnd()
        );
      }
      // warn: heuristic, not actually checking for a collection type,
      // as this concept does not exist in the general case
      else if (!exprType.isGenericType()) {
        Log.error("0xA3025 Expression of object " +
                ast.getName() + " has to be a collection",
            ast.get_SourcePositionStart(),
            ast.get_SourcePositionEnd()
        );
      }
      else {
        SymTypeExpression result = MCCollectionSymTypeRelations
            .getCollectionElementType(exprType);
        symbol.setType(result);
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
