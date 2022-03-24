/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.check.AbstractSynthesizeFromType;
import mc.typescalculator.unittypes._ast.ASTMinuteType;
import mc.typescalculator.unittypes._visitor.UnitTypesHandler;
import mc.typescalculator.unittypes._visitor.UnitTypesTraverser;
import mc.typescalculator.unittypes._visitor.UnitTypesVisitor2;

public class SynthesizeSymTypeFromUnitTypes extends AbstractSynthesizeFromType implements UnitTypesVisitor2, UnitTypesHandler {

  protected UnitTypesTraverser traverser;

  public SynthesizeSymTypeFromUnitTypes() {
    super();
  }

  @Override
  public UnitTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(UnitTypesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void endVisit(ASTMinuteType type){
    TypeSymbolSurrogate surrogate = new TypeSymbolSurrogate(type.getUnit());
    surrogate.setEnclosingScope(getScope(type.getEnclosingScope()));
    typeCheckResult.setResult(new SymTypeOfSIUnit(surrogate));
  }
}
