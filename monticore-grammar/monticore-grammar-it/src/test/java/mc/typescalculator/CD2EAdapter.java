/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.expressions.expressionsbasis._symboltable.*;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types2.SymTypeExpression;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static mc.typescalculator.CD2EHelper.*;

public class CD2EAdapter implements IETypeSymbolResolvingDelegate, IEMethodSymbolResolvingDelegate, IEVariableSymbolResolvingDelegate {

  private CD4AnalysisGlobalScope cd4ascope;

  public CD2EAdapter(CD4AnalysisGlobalScope cd4ascope){
    this.cd4ascope=cd4ascope;
  }

  @Override
  public Collection<EMethodSymbol> resolveAdaptedEMethodSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<EMethodSymbol> predicate) {
    Collection<EMethodSymbol> result = Lists.newArrayList();
    Optional<CDMethOrConstrSymbol> methOrConstrSymbolOpt = cd4ascope.resolveCDMethOrConstr(symbolName,modifier);
    if(methOrConstrSymbolOpt.isPresent()){
      CDMethOrConstrSymbol methOrConstrSymbol = methOrConstrSymbolOpt.get();
      result.add(transformCDMethOrConstr2EMethodSymbol(methOrConstrSymbol));
    }
    return result;
  }

  @Override
  public Collection<ETypeSymbol> resolveAdaptedETypeSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<ETypeSymbol> predicate) {
    Collection<ETypeSymbol> result = Lists.newArrayList();
    Optional<CDTypeSymbol> typeSymbolOpt = cd4ascope.resolveCDType(symbolName,modifier);
    if(typeSymbolOpt.isPresent()){
      CDTypeSymbol typeSymbol = typeSymbolOpt.get();
      ETypeSymbol res = ExpressionsBasisSymTabMill.eTypeSymbolBuilder().setName(typeSymbol.getName()).setFullName(typeSymbol.getFullName()).setAccessModifier(typeSymbol.getAccessModifier()).build();
      for(CDAssociationSymbol assoc : typeSymbol.getAllAssociations()){
        CDTypeSymbol targetType = assoc.getTargetType().getReferencedSymbol();
        List<EVariableSymbol> variableSymbols = res.getVariableSymbols();
        variableSymbols.add(ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName(targetType.getName()).setFullName(targetType.getFullName()).setAccessModifier(targetType.getAccessModifier()).build());
        res.setVariableSymbols(variableSymbols);
      }
      for(CDFieldSymbol fieldSymbol: typeSymbol.getFields()){
        List<EVariableSymbol> variableSymbols = res.getVariableSymbols();
        EVariableSymbol varsym = ExpressionsBasisSymTabMill.eVariableSymbolBuilder().setName(fieldSymbol.getName()).setFullName(fieldSymbol.getFullName()).setAccessModifier(fieldSymbol.getAccessModifier()).build();
        varsym.setType(transformCDType2SymTypeExpression(fieldSymbol.getType()));
        variableSymbols.add(varsym);
        res.setVariableSymbols(variableSymbols);
      }
      for(CDTypeSymbolReference ref : typeSymbol.getSuperTypes()){
        List<SymTypeExpression> superTypes = res.getSuperTypes();
        superTypes.add(transformCDType2SymTypeExpression(ref));
        res.setSuperTypes(superTypes);
      }
      result.add(res);
    }
    return result;
  }

  @Override
  public Collection<EVariableSymbol> resolveAdaptedEVariableSymbol(boolean foundSymbols, String symbolName, AccessModifier modifier, Predicate<EVariableSymbol> predicate) {
    Collection<EVariableSymbol> result = Lists.newArrayList();
    Optional<CDFieldSymbol> fieldSymbolopt = cd4ascope.resolveCDField(symbolName,modifier);
    if(fieldSymbolopt.isPresent()){
      CDFieldSymbol fieldSymbol = fieldSymbolopt.get();
      result.add(transformCDField2EVariableSymbol(fieldSymbol));
    }
    return result;
  }
}
