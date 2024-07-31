package de.monticore.types.typeparameters._symboltable;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.monticore.types.typeparameters._visitor.TypeParametersVisitor2;
import de.monticore.types3.ITypeCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Sets the superTypes of the type parameter symbols.
 * <p>
 * IMPORTANT: usually, the symbols are moved from the scope they are in
 * into their corresponding types scope.
 * In this case, they cannot be found from within the scope enclosing
 * of ASTNode referencing the superTypes.
 * As such, this must be run before any such transformation takes place!
 */
public class TypeParametersSTCompleteTypes implements TypeParametersVisitor2 {

  ITypeCalculator tc;

  public TypeParametersSTCompleteTypes(ITypeCalculator tc) {
    this.tc = tc;
  }

  @Override
  public void visit(ASTTypeParameter node) {
    if (node.isPresentTypeBounds()) {
      List<SymTypeExpression> bounds = new ArrayList<>();
      for (ASTMCType astTypeBound : node.getTypeBounds().getMCTypeList()) {
        bounds.add(tc.symTypeFromAST(astTypeBound));
      }
      // error logged if obscure
      node.getSymbol().setSuperTypesList(bounds);
    }
  }
}
