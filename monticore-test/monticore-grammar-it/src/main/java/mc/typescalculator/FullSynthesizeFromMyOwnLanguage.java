/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import de.monticore.types.check.*;
import mc.typescalculator.myownlanguage.MyOwnLanguageMill;
import mc.typescalculator.myownlanguage._visitor.MyOwnLanguageTraverser;

public class FullSynthesizeFromMyOwnLanguage extends AbstractSynthesize {

  protected SynthesizeSymTypeFromMCBasicTypes symTypeFromMCBasicTypes;
  protected SynthesizeSymTypeFromMCCollectionTypes symTypeFromMCCollectionTypes;
  protected SynthesizeSymTypeFromUnitTypes symTypeFromUnitTypes;

  public FullSynthesizeFromMyOwnLanguage(){
    this(MyOwnLanguageMill.traverser());
  }

  public FullSynthesizeFromMyOwnLanguage(MyOwnLanguageTraverser traverser){
    super(traverser);
    init(traverser);
  }

  public void setTraverser(MyOwnLanguageTraverser traverser) {
    this.traverser = traverser;
  }

  public void init(MyOwnLanguageTraverser traverser) {
    symTypeFromUnitTypes = new SynthesizeSymTypeFromUnitTypes();
    symTypeFromUnitTypes.setTypeCheckResult(typeCheckResult);
    traverser.add4UnitTypes(symTypeFromUnitTypes);
    traverser.setUnitTypesHandler(symTypeFromUnitTypes);

    symTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    symTypeFromMCCollectionTypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCCollectionTypes(symTypeFromMCCollectionTypes);
    traverser.setMCCollectionTypesHandler(symTypeFromMCCollectionTypes);

    symTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    symTypeFromMCBasicTypes.setTypeCheckResult(typeCheckResult);
    traverser.setMCBasicTypesHandler(symTypeFromMCBasicTypes);
    traverser.add4MCBasicTypes(symTypeFromMCBasicTypes);
  }
}
