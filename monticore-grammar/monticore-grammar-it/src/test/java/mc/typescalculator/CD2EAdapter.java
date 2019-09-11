/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.*;
import de.monticore.types2.SymTypeExpression;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CD2EAdapter implements ITypeSymbolResolvingDelegate, IMethodSymbolResolvingDelegate, IFieldSymbolResolvingDelegate {

  private CD4AnalysisGlobalScope cd4ascope;

  public CD2EAdapter(CD4AnalysisGlobalScope cd4ascope){
    this.cd4ascope=cd4ascope;

  }

  @Override
  public Collection<MethodSymbol> resolveAdaptedMethodSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<MethodSymbol> predicate) {
    Collection<MethodSymbol> result = Lists.newArrayList();
    Optional<CDMethOrConstrSymbol> methOrConstrSymbolOpt = cd4ascope.resolveCDMethOrConstr(symbolName,modifier);
    if(methOrConstrSymbolOpt.isPresent()){
      CDMethOrConstrSymbol methOrConstrSymbol = methOrConstrSymbolOpt.get();
      result.add(CD2EHelper.transformCDMethOrConstr2EMethodSymbol(methOrConstrSymbol));
    }
    return result;
  }

  @Override
  public Collection<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    Collection<TypeSymbol> result = Lists.newArrayList();
    Optional<CDTypeSymbol> typeSymbolOpt = cd4ascope.resolveCDType(symbolName,modifier);
    if(typeSymbolOpt.isPresent()){
      CDTypeSymbol typeSymbol = typeSymbolOpt.get();
      TypeSymbol res = ExpressionsBasisSymTabMill.typeSymbolBuilder().setName(typeSymbol.getName()).setFullName(typeSymbol.getFullName()).setAccessModifier(typeSymbol.getAccessModifier()).build();
      for(CDAssociationSymbol assoc : typeSymbol.getAllAssociations()){
        CDTypeSymbol targetType = assoc.getTargetType().getReferencedSymbol();
        List<FieldSymbol> variableSymbols = res.getFields();
        variableSymbols.add(ExpressionsBasisSymTabMill.fieldSymbolBuilder().setName(targetType.getName()).setFullName(targetType.getFullName()).setAccessModifier(targetType.getAccessModifier()).build());
        res.setFields(variableSymbols);
      }
      for(CDFieldSymbol fieldSymbol: typeSymbol.getFields()){
        List<FieldSymbol> variableSymbols = res.getFields();
        FieldSymbol varsym = ExpressionsBasisSymTabMill.fieldSymbolBuilder().setName(fieldSymbol.getName()).setFullName(fieldSymbol.getFullName()).setAccessModifier(fieldSymbol.getAccessModifier()).build();
        varsym.setType(CD2EHelper.transformCDType2SymTypeExpression(fieldSymbol.getType()));
        variableSymbols.add(varsym);
        res.setFields(variableSymbols);
      }
      for(CDTypeSymbolReference ref : typeSymbol.getSuperTypes()){
        List<SymTypeExpression> superTypes = res.getSuperTypes();
        superTypes.add(CD2EHelper.transformCDType2SymTypeExpression(ref));
        res.setSuperTypes(superTypes);
      }
      result.add(res);
    }
    return result;
  }

  @Override
  public Collection<FieldSymbol> resolveAdaptedFieldSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    Collection<FieldSymbol> result = Lists.newArrayList();
    Optional<CDFieldSymbol> cdFieldSymbolopt = cd4ascope.resolveCDField(symbolName,modifier);
    if(cdFieldSymbolopt.isPresent()){
      CDFieldSymbol fieldSymbol = cdFieldSymbolopt.get();
      result.add(CD2EHelper.transformCDField2FieldSymbol(fieldSymbol));
    }
    return result;
  }
}
