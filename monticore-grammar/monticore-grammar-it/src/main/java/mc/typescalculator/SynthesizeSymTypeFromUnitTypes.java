package mc.typescalculator;

import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import mc.typescalculator.unittypes._ast.ASTMinuteType;
import mc.typescalculator.unittypes._visitor.UnitTypesVisitor;

public class SynthesizeSymTypeFromUnitTypes extends SynthesizeSymTypeFromMCBasicTypes implements UnitTypesVisitor {

  private UnitTypesVisitor realThis;

  public SynthesizeSymTypeFromUnitTypes(IExpressionsBasisScope scope) {
    super(scope);
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
    lastResult.setLast(new SymTypeOfSIUnit(new TypeSymbolLoader(type.getUnit(),scope)));
  }
}
