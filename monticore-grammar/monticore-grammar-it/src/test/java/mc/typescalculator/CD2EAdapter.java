/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CD2EAdapter implements ITypeSymbolResolvingDelegate, IMethodSymbolResolvingDelegate, IFieldSymbolResolvingDelegate {

  private CD4AnalysisGlobalScope cd4ascope;

  public CD2EAdapter(CD4AnalysisGlobalScope cd4ascope){
    this.cd4ascope=cd4ascope;

  }

  @Override
  public List<MethodSymbol> resolveAdaptedMethodSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<MethodSymbol> predicate) {
    List<MethodSymbol> result = Lists.newArrayList();
    Optional<CDMethOrConstrSymbol> methOrConstrSymbolOpt = cd4ascope.resolveCDMethOrConstr(symbolName,modifier);
    if(methOrConstrSymbolOpt.isPresent()){
      CDMethOrConstrSymbol methOrConstrSymbol = methOrConstrSymbolOpt.get();
      result.add(CD2EHelper.transformCDMethOrConstr2EMethodSymbol(methOrConstrSymbol));
    }
    return result;
  }

  @Override
  public List<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    List<TypeSymbol> result = Lists.newArrayList();
    Optional<CDTypeSymbol> typeSymbolOpt = cd4ascope.resolveCDType(symbolName,modifier);
    if(typeSymbolOpt.isPresent()){
      CDTypeSymbol typeSymbol = typeSymbolOpt.get();
      TypeSymbol res = ExpressionsBasisSymTabMill.typeSymbolBuilder().setName(typeSymbol.getName()).setFullName(typeSymbol.getFullName()).setAccessModifier(typeSymbol.getAccessModifier()).build();
      res.setSpannedScope(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build());
      for(CDAssociationSymbol assoc : typeSymbol.getAllAssociations()){
        CDTypeSymbol targetType = assoc.getTargetType().getReferencedSymbol();
        List<FieldSymbol> variableSymbols = res.getFieldList();
        FieldSymbol varsym = ExpressionsBasisSymTabMill.fieldSymbolBuilder().setName(targetType.getName()).setFullName(targetType.getFullName()).setAccessModifier(targetType.getAccessModifier()).build();
        variableSymbols.add(varsym);
        res.getSpannedScope().add(varsym);
        res.setFieldList(variableSymbols);
      }
      for(CDFieldSymbol fieldSymbol: typeSymbol.getFields()){
        List<FieldSymbol> variableSymbols = res.getFieldList();
        FieldSymbol varsym = ExpressionsBasisSymTabMill.fieldSymbolBuilder().setName(fieldSymbol.getName()).setFullName(fieldSymbol.getFullName()).setAccessModifier(fieldSymbol.getAccessModifier()).build();
        varsym.setType(CD2EHelper.transformCDTypeReference2SymTypeExpression(fieldSymbol.getType()));
        variableSymbols.add(varsym);
        res.getSpannedScope().add(varsym);
        res.setFieldList(variableSymbols);
      }
      for(CDMethOrConstrSymbol method : typeSymbol.getAllVisibleMethods()){
        List<MethodSymbol> methodSymbols = res.getMethodList();
        MethodSymbol metSym = ExpressionsBasisSymTabMill.methodSymbolBuilder().setName(method.getName()).setFullName(method.getFullName()).setAccessModifier(method.getAccessModifier()).build();
        metSym.setReturnType(CD2EHelper.transformCDTypeReference2SymTypeExpression(method.getReturnType()));
        for(CDFieldSymbol parameter: method.getParameters()){
          List<FieldSymbol> fieldSymbols = metSym.getParameterList();
          fieldSymbols.add(CD2EHelper.transformCDField2FieldSymbol(parameter));
          metSym.setParameterList(fieldSymbols);
        }
        methodSymbols.add(metSym);
        res.getSpannedScope().add(metSym);
        res.setMethodList(methodSymbols);
      }
      for(CDTypeSymbolReference ref : typeSymbol.getSuperTypes()){
        List<SymTypeExpression> superTypes = res.getSuperTypeList();
        superTypes.add(CD2EHelper.transformCDTypeReference2SymTypeExpression(ref));
        res.setSuperTypeList(superTypes);
      }
      result.add(res);
    }
    return result;
  }

  @Override
  public List<FieldSymbol> resolveAdaptedFieldSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    List<FieldSymbol> result = Lists.newArrayList();
    Optional<CDFieldSymbol> cdFieldSymbolopt = cd4ascope.resolveCDField(symbolName,modifier);
    if(cdFieldSymbolopt.isPresent()){
      CDFieldSymbol fieldSymbol = cdFieldSymbolopt.get();
      result.add(CD2EHelper.transformCDField2FieldSymbol(fieldSymbol));
    }
    return result;
  }
}
