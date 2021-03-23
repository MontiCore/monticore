/* (c) https://github.com/MontiCore/monticore */
package mylang;

import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import mylang._visitor.MyLangTraverser;

import java.util.Optional;

public class SynthesizeFromMyLang implements ISynthesize {
  
  protected MyLangTraverser traverser;
  protected TypeCheckResult typeCheckResult;
  
  @Override 
  public Optional<SymTypeExpression> getResult() {
    if(typeCheckResult.isPresentCurrentResult()){
      return Optional.of(typeCheckResult.getCurrentResult());
    }
    return Optional.empty();
  }
  
  @Override 
  public void init() {
    // use new result wrapper and traverser
    traverser = MyLangMill.traverser();
    typeCheckResult = new TypeCheckResult();
    // add Synthesize for MCBasicTypes
    SynthesizeSymTypeFromMCBasicTypes basictypes =
                             new SynthesizeSymTypeFromMCBasicTypes();
    basictypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCBasicTypes(basictypes);
    traverser.setMCBasicTypesHandler(basictypes);
    // add Synthesize for MCArrayTypes
    SynthesizeSymTypeFromMCArrayTypes arraytypes = 
                             new SynthesizeSymTypeFromMCArrayTypes();
    arraytypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCArrayTypes(arraytypes);
    traverser.setMCArrayTypesHandler(arraytypes);
  }
  
  @Override 
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }
}
