package mc.typescalculator;

import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.typescalculator.TypeExpression;
import de.monticore.typescalculator.ObjectType;
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
    res.setType(transformCDType2TypeExpression(fieldSymbol.getType()));
    return res;
  }

  public static EMethodSymbol transformCDMethOrConstr2EMethodSymbol(CDMethOrConstrSymbol methOrConstrSymbol){
    EMethodSymbol res = ExpressionsBasisSymTabMill.eMethodSymbolBuilder().setName(methOrConstrSymbol.getName()).setFullName(methOrConstrSymbol.getFullName()).setAccessModifier(methOrConstrSymbol.getAccessModifier()).build();
    res.setReturnType(transformCDType2TypeExpression(methOrConstrSymbol.getReturnType()));
    for(CDFieldSymbol param: methOrConstrSymbol.getParameters()){
      List<EVariableSymbol> params = res.getParameterList();
      params.add(transformCDField2EVariableSymbol(param));
      res.setParameterList(params);
    }
    return res;
  }

  public static TypeExpression transformCDType2TypeExpression(CDTypeSymbol typeSymbol) {
    TypeExpression res = new ObjectType();
    boolean success = false;
      List<CDTypeSymbolReference> superTypes = typeSymbol.getSuperTypes();
      for (CDTypeSymbolReference ref : superTypes) {
        res.addSuperType(transformCDType2TypeExpression(ref));
      }
      res.setName(typeSymbol.getFullName());
      success = true;
    if (!success){
      res.setName(typeSymbol.getName());
    }
    return res;
  }
}
