/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.types.check.*;
import mc.typescalculator.myownlanguage.MyOwnLanguageMill;
import mc.typescalculator.myownlanguage._visitor.MyOwnLanguageTraverser;

import java.util.Optional;

public class SynthesizeSymTypeFromMyOwnLanguage implements ISynthesize {

  protected SynthesizeSymTypeFromMCBasicTypes symTypeFromMCBasicTypes;
  protected SynthesizeSymTypeFromMCCollectionTypes symTypeFromMCCollectionTypes;
  protected SynthesizeSymTypeFromUnitTypes symTypeFromUnitTypes;
  protected TypeCheckResult result = new TypeCheckResult();
  private MyOwnLanguageTraverser traverser;

  public SynthesizeSymTypeFromMyOwnLanguage(){
    init();
  }

  @Override
  public MyOwnLanguageTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MyOwnLanguageTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    return Optional.ofNullable(result.getCurrentResult());
  }

  @Override
  public void init() {
    result = new TypeCheckResult();
    traverser = MyOwnLanguageMill.traverser();

    symTypeFromUnitTypes = new SynthesizeSymTypeFromUnitTypes();
    symTypeFromUnitTypes.setTypeCheckResult(result);
    traverser.addUnitTypesVisitor(symTypeFromUnitTypes);
    traverser.setUnitTypesHandler(symTypeFromUnitTypes);

    symTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    symTypeFromMCCollectionTypes.setTypeCheckResult(result);
    traverser.addMCCollectionTypesVisitor(symTypeFromMCCollectionTypes);
    traverser.setMCCollectionTypesHandler(symTypeFromMCCollectionTypes);

    symTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    symTypeFromMCBasicTypes.setTypeCheckResult(result);
    traverser.setMCBasicTypesHandler(symTypeFromMCBasicTypes);
    traverser.addMCBasicTypesVisitor(symTypeFromMCBasicTypes);
  }
}
