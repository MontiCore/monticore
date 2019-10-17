/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymTabMill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CD2EHelper {

  protected static Map<CDTypeSymbol,TypeSymbol> convertedTypes = new HashMap<>();

  public static TypeSymbol transformCDType2TypeSymbol(CDTypeSymbol typeSymbol){
    if(convertedTypes.containsKey(typeSymbol)){
      return convertedTypes.get(typeSymbol);
    }

    TypeSymbol res = ExpressionsBasisSymTabMill.typeSymbolBuilder().setAccessModifier(typeSymbol.getAccessModifier()).setName(typeSymbol.getName()).setFullName(typeSymbol.getFullName()).build();
    res.setSpannedScope(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build());
    convertedTypes.put(typeSymbol,res);
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
    for(CDTypeSymbolReference superType: typeSymbol.getSuperTypes()){
      List<SymTypeExpression> superTypesList = res.getSuperTypeList();
      superTypesList.add(transformCDTypeReference2SymTypeExpression(superType));
      res.setSuperTypeList(superTypesList);
    }
    return res;
  }

  public static FieldSymbol transformCDField2FieldSymbol(CDFieldSymbol fieldSymbol){
    FieldSymbol res = ExpressionsBasisSymTabMill.fieldSymbolBuilder().setName(fieldSymbol.getName()).setFullName(fieldSymbol.getFullName()).setAccessModifier(fieldSymbol.getAccessModifier()).build();
    res.setType(transformCDTypeReference2SymTypeExpression(fieldSymbol.getType()));
    return res;
  }

  public static MethodSymbol transformCDMethOrConstr2EMethodSymbol(CDMethOrConstrSymbol methOrConstrSymbol){
    MethodSymbol res = ExpressionsBasisSymTabMill.methodSymbolBuilder().setName(methOrConstrSymbol.getName()).setFullName(methOrConstrSymbol.getFullName()).setAccessModifier(methOrConstrSymbol.getAccessModifier()).build();
    res.setReturnType(transformCDTypeReference2SymTypeExpression(methOrConstrSymbol.getReturnType()));
    for(CDFieldSymbol param: methOrConstrSymbol.getParameters()){
      List<FieldSymbol> params = res.getParameterList();
      params.add(transformCDField2FieldSymbol(param));
      res.setParameterList(params);
    }
    return res;
  }

  public static SymTypeExpression transformCDTypeReference2SymTypeExpression(CDTypeSymbolReference typeSymbol) {
//    List<SymTypeExpression> superTypes = new ArrayList<>();
//    for(CDTypeSymbolReference ref: typeSymbol.getSuperTypes()){
//      superTypes.add(transformCDType2SymTypeExpression(ref));
//    }
//    List<MethodSymbol> methods = new ArrayList<>();
//    for(CDMethOrConstrSymbol method: typeSymbol.getMethods()){
//      methods.add(transformCDMethOrConstr2EMethodSymbol(method));
//    }
//    List<FieldSymbol> fields = new ArrayList<>();
//    for(CDFieldSymbol field: typeSymbol.getFields()){
//      fields.add(transformCDField2FieldSymbol(field));
//    }

    SymTypeExpression res = new SymTypeOfObject(typeSymbol.getName(),transformCDType2TypeSymbol(typeSymbol.getReferencedSymbol()));
    return res;
  }
}
