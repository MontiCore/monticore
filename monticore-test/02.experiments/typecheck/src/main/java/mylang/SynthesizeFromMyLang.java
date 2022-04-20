/* (c) https://github.com/MontiCore/monticore */
package mylang;

import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import mylang._visitor.MyLangTraverser;

public class SynthesizeFromMyLang implements ISynthesize {
  
  protected MyLangTraverser traverser;
  protected TypeCheckResult typeCheckResult;

  public SynthesizeFromMyLang(){
    init();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCQualifiedName qName) {
    init();
    qName.accept(traverser);
    return typeCheckResult.copy();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCReturnType type) {
    init();
    type.accept(traverser);
    return typeCheckResult.copy();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCType type) {
    init();
    type.accept(traverser);
    return typeCheckResult.copy();
  }
  
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
  
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }
}
