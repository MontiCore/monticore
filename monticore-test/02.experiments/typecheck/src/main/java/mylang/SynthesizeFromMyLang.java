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
    SynthesizeSymTypeFromMCBasicTypes bt =
                           new SynthesizeSymTypeFromMCBasicTypes();
    bt.setTypeCheckResult(typeCheckResult);
    traverser.add4MCBasicTypes(bt);
    traverser.setMCBasicTypesHandler(bt);
    
    // add Synthesize for MCArrayTypes
    SynthesizeSymTypeFromMCArrayTypes at = 
                           new SynthesizeSymTypeFromMCArrayTypes();
    at.setTypeCheckResult(typeCheckResult);
    traverser.add4MCArrayTypes(at);
    traverser.setMCArrayTypesHandler(at);
  }
  
  @Override 
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }
}
