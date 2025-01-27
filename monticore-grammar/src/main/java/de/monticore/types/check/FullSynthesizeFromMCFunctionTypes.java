/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcfunctiontypes.MCFunctionTypesMill;
import de.monticore.types.mcfunctiontypes._visitor.MCFunctionTypesTraverser;

/**
 * @deprecated use {@link de.monticore.types3.TypeCheck3}
 */
@Deprecated(forRemoval = true)
public class FullSynthesizeFromMCFunctionTypes extends AbstractSynthesize {

  public FullSynthesizeFromMCFunctionTypes() {
    this(MCFunctionTypesMill.traverser());
  }

  public FullSynthesizeFromMCFunctionTypes(MCFunctionTypesTraverser traverser) {
    super(traverser);
    init(traverser);
  }

  public void init(MCFunctionTypesTraverser traverser) {
    SynthesizeSymTypeFromMCFunctionTypes synFromFunction = new SynthesizeSymTypeFromMCFunctionTypes();
    synFromFunction.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();
    synFromBasic.setTypeCheckResult(typeCheckResult);

    traverser.add4MCFunctionTypes(synFromFunction);
    traverser.setMCFunctionTypesHandler(synFromFunction);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }
}
