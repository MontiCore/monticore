/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;

import java.util.Optional;

public class FullSynthesizeCompKindFromMCSimpleGenericTypes implements ISynthesizeComponent {

  protected MCSimpleGenericTypesTraverser traverser;

  protected CompKindCheckResult resultWrapper;

  @Override
  public void init() {
    this.traverser = MCSimpleGenericTypesMill.traverser();
    this.resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synFromBasic = new SynthesizeCompKindFromMCBasicTypes(resultWrapper);
    SynthesizeCompKindFromMCSimpleGenericTypes synFromSimple = new SynthesizeCompKindFromMCSimpleGenericTypes(resultWrapper);

    traverser.setMCSimpleGenericTypesHandler(synFromSimple);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }

  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public Optional<CompKindExpression> getResult() {
    return resultWrapper.getResult();
  }
}
