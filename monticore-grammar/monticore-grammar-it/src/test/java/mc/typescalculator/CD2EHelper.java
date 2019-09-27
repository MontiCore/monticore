/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.cd.cd4analysis._symboltable.CDAssociationSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDMethOrConstrSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;

import java.util.List;

public class CD2EHelper {

  public static TypeSymbol transformCDType2TypeSymbol(CDTypeSymbol typeSymbol){
    TypeSymbol res = ExpressionsBasisSymTabMill.typeSymbolBuilder().setAccessModifier(typeSymbol.getAccessModifier()).setName(typeSymbol.getName()).setFullName(typeSymbol.getFullName()).build();
    for(CDAssociationSymbol assoc : typeSymbol.getAllAssociations()){
      CDTypeSymbol targetType = assoc.getTargetType().getReferencedSymbol();
      List<FieldSymbol> variableSymbols = res.getFieldList();
      variableSymbols.add(ExpressionsBasisSymTabMill.fieldSymbolBuilder().setName(targetType.getName()).setFullName(targetType.getFullName()).setAccessModifier(targetType.getAccessModifier()).build());
      res.setFieldList(variableSymbols);
    }
    for(CDFieldSymbol fieldSymbol: typeSymbol.getFields()){
      List<FieldSymbol> variableSymbols = res.getFieldList();
      variableSymbols.add(transformCDField2FieldSymbol(fieldSymbol));
      res.setFieldList(variableSymbols);
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
      List<FieldSymbol> params = res.getParameterList();
      params.add(transformCDField2FieldSymbol(param));
      res.setParameterList(params);
    }
    return res;
  }

  public static SymTypeExpression transformCDType2SymTypeExpression(CDTypeSymbol typeSymbol) {
    SymTypeExpression res = new SymTypeOfObject(typeSymbol.getName());
    return res;
  }
}
