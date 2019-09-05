/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types2.SymTypeExpression;
import de.monticore.types2.SymTypeOfObject;

import java.util.List;

public class CD2EHelper {

  public static TypeSymbol transformCDType2ETypeSymbol(CDTypeSymbol typeSymbol){
    TypeSymbol res = ExpressionsBasisSymTabMill.typeSymbolBuilder().setAccessModifier(typeSymbol.getAccessModifier()).setName(typeSymbol.getName()).setFullName(typeSymbol.getFullName()).build();
    for(CDAssociationSymbol assoc : typeSymbol.getAllAssociations()){
      CDTypeSymbol targetType = assoc.getTargetType().getReferencedSymbol();
      List<FieldSymbol> variableSymbols = res.getFields();
      variableSymbols.add(ExpressionsBasisSymTabMill.fieldSymbolBuilder().setName(targetType.getName()).setFullName(targetType.getFullName()).setAccessModifier(targetType.getAccessModifier()).build());
      res.setFields(variableSymbols);
    }
    for(CDFieldSymbol fieldSymbol: typeSymbol.getFields()){
      List<FieldSymbol> variableSymbols = res.getFields();
      variableSymbols.add(transformCDField2FieldSymbol(fieldSymbol));
      res.setFields(variableSymbols);
    }
    return res;
  }

  public static FieldSymbol transformCDField2FieldSymbol(CDFieldSymbol fieldSymbol){
    FieldSymbol res = ExpressionsBasisSymTabMill.fieldSymbolBuilder().setName(fieldSymbol.getName()).setFullName(fieldSymbol.getFullName()).setAccessModifier(fieldSymbol.getAccessModifier()).build();
    res.setType(transformCDType2SymTypeExpression(fieldSymbol.getType()));
    return res;
  }

  public static MethodSymbol transformCDMethOrConstr2EMethodSymbol(CDMethOrConstrSymbol methOrConstrSymbol){
    MethodSymbol res = ExpressionsBasisSymTabMill.methodSymbolBuilder().setName(methOrConstrSymbol.getName()).setFullName(methOrConstrSymbol.getFullName()).setAccessModifier(methOrConstrSymbol.getAccessModifier()).build();
    res.setReturnType(transformCDType2SymTypeExpression(methOrConstrSymbol.getReturnType()));
    for(CDFieldSymbol param: methOrConstrSymbol.getParameters()){
      List<FieldSymbol> params = res.getParameter();
      params.add(transformCDField2FieldSymbol(param));
      res.setParameter(params);
    }
    return res;
  }

  public static SymTypeExpression transformCDType2SymTypeExpression(CDTypeSymbol typeSymbol) {
    SymTypeExpression res = new SymTypeOfObject();
    boolean success = false;
      List<CDTypeSymbolReference> superTypes = typeSymbol.getSuperTypes();
      for (CDTypeSymbolReference ref : superTypes) {
        res.addSuperType(transformCDType2SymTypeExpression(ref));
      }
      res.setName(typeSymbol.getFullName());
      success = true;
    if (!success){
      res.setName(typeSymbol.getName());
    }
    return res;
  }
}
