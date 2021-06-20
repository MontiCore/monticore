/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator.combineexpressionswithliterals._symboltable;

import de.monticore.io.paths.MCPath;

public class CombineExpressionsWithLiteralsGlobalScope extends CombineExpressionsWithLiteralsGlobalScopeTOP {

  public CombineExpressionsWithLiteralsGlobalScope(MCPath symbolPath){
    super(symbolPath,"cex");
  }

  public CombineExpressionsWithLiteralsGlobalScope(MCPath symbolPath,
                                                   String modelFileExtension) {
    super(symbolPath, modelFileExtension);
  }

  public CombineExpressionsWithLiteralsGlobalScope(){
    super();
  }

  @Override
  public CombineExpressionsWithLiteralsGlobalScope getRealThis() {
    return this;
  }

}
