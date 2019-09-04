/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ETypeSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.types2.SymObjectType;
import de.monticore.types2.SymTypeExpression;

import java.util.List;

public class CD2EHelper {

  public static ETypeSymbol transformCDType2ETypeSymbol(CDTypeSymbol typeSymbol){
    ETypeSymbol res = ExpressionsBasisSymTabMill.eTypeSymbolBuilder().setAccessModifier(typeSymbol.getAccessModifier()).setName(typeSymbol.getName()).setFullName(typeSymbol.getFullName()).build();
    for(CDAssociationSymbol assoc : typeSymbol.getAllAssociations()){
      CDTypeSymbol targetType = assoc.getTargetType().getReferencedSymbol();
      List<EVariableSymbol> variableSymbols = res.getVariableSymbols();
      variableSymbols.add(ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName(targetType.getName()).setFullName(targetType.getFullName()).setAccessModifier(targetType.getAccessModifier()).build());
      res.setVariableSymbols(variableSymbols);
    }
    for(CDFieldSymbol fieldSymbol: typeSymbol.getFields()){
      List<EVariableSymbol> variableSymbols = res.getVariableSymbols();
      variableSymbols.add(transformCDField2EVariableSymbol(fieldSymbol));
      res.setVariableSymbols(variableSymbols);
    }
    return res;
  }

  public static EVariableSymbol transformCDField2EVariableSymbol(CDFieldSymbol fieldSymbol){
    EVariableSymbol res = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName(fieldSymbol.getName()).setFullName(fieldSymbol.getFullName()).setAccessModifier(fieldSymbol.getAccessModifier()).build();
    res.setType(transformCDType2SymTypeExpression(fieldSymbol.getType()));
    return res;
  }

  public static EMethodSymbol transformCDMethOrConstr2EMethodSymbol(CDMethOrConstrSymbol methOrConstrSymbol){
    EMethodSymbol res = ExpressionsBasisSymTabMill.eMethodSymbolBuilder().setName(methOrConstrSymbol.getName()).setFullName(methOrConstrSymbol.getFullName()).setAccessModifier(methOrConstrSymbol.getAccessModifier()).build();
    res.setReturnType(transformCDType2SymTypeExpression(methOrConstrSymbol.getReturnType()));
    for(CDFieldSymbol param: methOrConstrSymbol.getParameters()){
      List<EVariableSymbol> params = res.getParameterList();
      params.add(transformCDField2EVariableSymbol(param));
      res.setParameterList(params);
    }
    return res;
  }

  public static SymTypeExpression transformCDType2SymTypeExpression(CDTypeSymbol typeSymbol) {
    SymTypeExpression res = new SymObjectType();
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
