/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mccomponenttypes.MCComponentTypesMill;
import de.monticore.types.mccomponenttypes._visitor.MCComponentTypesTraverser;

import java.util.Optional;

public class FullSynthesizeCompKindFromMCComponentTypes implements ISynthesizeComponent {

  protected MCComponentTypesTraverser traverser;

  protected CompKindCheckResult resultWrapper;

  @Override
  public void init() {
    this.traverser = MCComponentTypesMill.traverser();
    this.resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCComponentTypes synthesizer =
        new SynthesizeCompKindFromMCComponentTypes(resultWrapper);

    traverser.setMCComponentTypesHandler(synthesizer);
  }

  @Override
  public MCComponentTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public Optional<CompKindExpression> getResult() {
    return resultWrapper.getResult();
  }
}
