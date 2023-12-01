// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.setexpressions.symboltable;

import de.monticore.expressions.setexpressions._ast.ASTGeneratorDeclaration;
import de.monticore.expressions.setexpressions._ast.ASTSetVariableDeclaration;
import de.monticore.expressions.setexpressions._symboltable.ISetExpressionsScope;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsHandler;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsTraverser;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsVisitor2;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.se_rwth.commons.logging.Log;

public class SetExpressionsSymbolTableCompleter implements
    SetExpressionsVisitor2, BasicSymbolsVisitor2, SetExpressionsHandler {

  IDerive deriver;

  ISynthesize synthesizer;

  protected SetExpressionsTraverser traverser;

  public void setDeriver(IDerive deriver) {
    if (deriver != null) {
      this.deriver = deriver;
    }
    else {
      Log.error("0xA3201 The deriver has to be set");
    }
  }

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

  public void initialize_SetVariableDeclaration(
      VariableSymbol symbol, ASTSetVariableDeclaration ast) {
    symbol.setIsReadOnly(false);
    if (ast.isPresentMCType()) {
      ast.getMCType().setEnclosingScope(symbol.getEnclosingScope());
      ast.getMCType().accept(getTraverser());
      final TypeCheckResult typeResult = synthesizer.synthesizeType(ast.getMCType());
      if (!typeResult.isPresentResult()) {
        Log.error("0xA3027 The type " + ast.getMCType().printType()
                + " of the object " + ast.getName()
                + " could not be calculated",
            ast.get_SourcePositionStart(),
            ast.get_SourcePositionEnd()
        );
      }
      else {
        symbol.setType(typeResult.getResult());
      }
    }
    else {
      if (ast.isPresentExpression()) {
        ast.getExpression().accept(getTraverser());
        final TypeCheckResult tcr_expr = deriver.deriveType(ast.getExpression());
        if (tcr_expr.isPresentResult()) {
          symbol.setType(tcr_expr.getResult());
        }
        else {
          Log.error("0xA3026 The type of the object "
                  + ast.getName() + " could not be calculated",
              ast.get_SourcePositionStart(),
              ast.get_SourcePositionEnd()
          );
        }
      }
      else {
        symbol.setType(
            SymTypeExpressionFactory.createTypeObject("Object", ast.getEnclosingScope()));
      }
    }
  }

  @Override
  public void endVisit(ASTGeneratorDeclaration node) {
    initialize_GeneratorDeclaration(node.getSymbol(), node);
  }

  public void initialize_GeneratorDeclaration(VariableSymbol symbol, ASTGeneratorDeclaration ast) {
    symbol.setIsReadOnly(false);
    if (ast.isPresentMCType()) {
      ast.getMCType().setEnclosingScope(symbol.getEnclosingScope());
      ast.getMCType().accept(getTraverser());
      final TypeCheckResult typeResult = synthesizer.synthesizeType(ast.getMCType());
      if (!typeResult.isPresentResult()) {
        Log.error("0xA3023 The type " + ast.getMCType().printType()
                + " of the object " + ast.getName()
                + " could not be calculated",
            ast.get_SourcePositionStart(),
            ast.get_SourcePositionEnd()
        );
      }
      else {
        symbol.setType(typeResult.getResult());
      }
    }
    else {
      final TypeCheckResult typeResult = deriver.deriveType(ast.getExpression());
      if (!typeResult.isPresentResult() || typeResult.getResult().isObscureType()) {
        Log.error("0xA3024 The type of the object "
                + ast.getName() + " could not be calculated",
            ast.get_SourcePositionStart(),
            ast.get_SourcePositionEnd()
        );
      }
      else if (typeResult.getResult().isPrimitive()) {
        Log.error("0xA3025 Expression of object " +
                ast.getName() + " has to be a collection",
            ast.get_SourcePositionStart(),
            ast.get_SourcePositionEnd()
        );
      }
      else {
        SymTypeExpression result = MCCollectionSymTypeRelations
            .getCollectionElementType(typeResult.getResult());
        symbol.setType(result);
      }
    }
  }
}
