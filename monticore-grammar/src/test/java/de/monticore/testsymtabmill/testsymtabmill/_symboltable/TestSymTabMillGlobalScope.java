/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testsymtabmill.testsymtabmill._symboltable;

import de.monticore.io.paths.MCPath;

public class TestSymTabMillGlobalScope extends TestSymTabMillGlobalScopeTOP {

  public TestSymTabMillGlobalScope(MCPath symbolPath){
    super(symbolPath, "tsm");
  }

  public TestSymTabMillGlobalScope(MCPath symbolPath, String modelFileExtension){
    super(symbolPath, modelFileExtension);
  }

  public TestSymTabMillGlobalScope(){
    super();
  }

  @Override public TestSymTabMillGlobalScope getRealThis() {
    return this;
  }

}
