/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import mc.typescalculator.unittypes._ast.ASTMinuteType;
import mc.typescalculator.unittypes._visitor.UnitTypesVisitor;

public class SynthesizeSymTypeFromUnitTypes extends SynthesizeSymTypeFromMCBasicTypes implements UnitTypesVisitor, ISynthesize {

  private UnitTypesVisitor realThis;

  public SynthesizeSymTypeFromUnitTypes() {
    super();
  }

  @Override
  public UnitTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(UnitTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void endVisit(ASTMinuteType type){
    OOTypeSymbolSurrogate surrogate = new OOTypeSymbolSurrogate(type.getUnit());
    surrogate.setEnclosingScope(getScope(type.getEnclosingScope()));
    typeCheckResult.setCurrentResult(new SymTypeOfSIUnit(surrogate));
  }
}
