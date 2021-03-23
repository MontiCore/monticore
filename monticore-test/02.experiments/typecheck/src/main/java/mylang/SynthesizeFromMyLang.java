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
    traverser = MyLangMill.traverser();
    typeCheckResult = new TypeCheckResult();
    SynthesizeSymTypeFromMCBasicTypes basictypes = new SynthesizeSymTypeFromMCBasicTypes();
    basictypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCBasicTypes(basictypes);
    traverser.setMCBasicTypesHandler(basictypes);
    SynthesizeSymTypeFromMCArrayTypes arraytypes = new SynthesizeSymTypeFromMCArrayTypes();
    arraytypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCArrayTypes(arraytypes);
    traverser.setMCArrayTypesHandler(arraytypes);
  }
  
  @Override 
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }
}
