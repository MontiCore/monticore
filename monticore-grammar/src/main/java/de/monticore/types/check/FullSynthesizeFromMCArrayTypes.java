/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcarraytypes.MCArrayTypesMill;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesTraverser;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class FullSynthesizeFromMCArrayTypes extends AbstractSynthesize {

  public FullSynthesizeFromMCArrayTypes(){
    this(MCArrayTypesMill.traverser());
  }

  public FullSynthesizeFromMCArrayTypes(MCArrayTypesTraverser traverser){
    super(traverser);
    init(traverser);
  }

  public void init(MCArrayTypesTraverser traverser) {
    SynthesizeSymTypeFromMCArrayTypes synFromArray = new SynthesizeSymTypeFromMCArrayTypes();
    synFromArray.setTypeCheckResult(typeCheckResult);
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();
    synFromBasic.setTypeCheckResult(typeCheckResult);

    traverser.add4MCArrayTypes(synFromArray);
    traverser.setMCArrayTypesHandler(synFromArray);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
  }
}
