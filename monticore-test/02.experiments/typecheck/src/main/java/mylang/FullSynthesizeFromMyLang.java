/* (c) https://github.com/MontiCore/monticore */
package mylang;

import de.monticore.types.check.*;
import mylang._visitor.MyLangTraverser;

public class FullSynthesizeFromMyLang extends AbstractSynthesize {
  
  public FullSynthesizeFromMyLang(){
    this(MyLangMill.traverser());
  }

  public FullSynthesizeFromMyLang(MyLangTraverser traverser){
    super(traverser);
    init(traverser);
  }
  
  public void init(MyLangTraverser traverser) {
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
  
}
