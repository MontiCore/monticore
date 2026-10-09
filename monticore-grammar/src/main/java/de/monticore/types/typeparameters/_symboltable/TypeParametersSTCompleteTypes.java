/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.typeparameters._symboltable;

import de.monticore.symboltable.ClearingMemorizer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.monticore.types.typeparameters._visitor.TypeParametersVisitor2;
import de.monticore.types3.ITypeCalculator;
import de.monticore.types3.TypeCheck3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
    List<Supplier<SymTypeExpression>> bounds = new ArrayList<>();
    if (tc != null){
        //deprecated behavior:
        for (ASTMCType astTypeBound : node.getMCTypeList()) {
            bounds.add(new ClearingMemorizer<>(() -> tc.symTypeFromAST(astTypeBound)));
        }
    } else {
        for (ASTMCType astTypeBound : node.getMCTypeList()) {
            bounds.add(new ClearingMemorizer<>(() -> TypeCheck3.symTypeFromAST(astTypeBound)));
        }
    }
    node.getSymbol().setSuperTypesSupplierList(bounds);
  }

}
