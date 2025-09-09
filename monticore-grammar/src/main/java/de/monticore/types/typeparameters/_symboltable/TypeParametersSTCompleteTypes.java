package de.monticore.types.typeparameters._symboltable;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.monticore.types.typeparameters._visitor.TypeParametersVisitor2;
import de.monticore.types3.ITypeCalculator;
import de.monticore.types3.TypeCheck3;

import java.util.ArrayList;
import java.util.List;

/**
 * Sets the superTypes of the type parameter symbols.
 */
public class TypeParametersSTCompleteTypes implements TypeParametersVisitor2 {

  @Deprecated
  ITypeCalculator tc;
  
  public TypeParametersSTCompleteTypes() {
  }
  
  @Deprecated
  public TypeParametersSTCompleteTypes(ITypeCalculator tc) {
    this.tc = tc;
  }

  @Override
  public void visit(ASTTypeParameter node) {
    List<SymTypeExpression> bounds = new ArrayList<>();
    for (ASTMCType astTypeBound : node.getMCTypeList()) {
      // deprecated behavior:
      if (tc != null) {
        bounds.add(tc.symTypeFromAST(astTypeBound));
      } else {
        bounds.add(TypeCheck3.symTypeFromAST(astTypeBound));
      }
    }
    // error logged if obscure
    node.getSymbol().setSuperTypesList(bounds);
  }

}
