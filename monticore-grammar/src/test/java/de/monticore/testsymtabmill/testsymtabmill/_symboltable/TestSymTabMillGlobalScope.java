/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testsymtabmill.testsymtabmill._symboltable;

import de.monticore.io.paths.ModelPath;

public class TestSymTabMillGlobalScope extends TestSymTabMillGlobalScopeTOP {

  public TestSymTabMillGlobalScope(ModelPath modelPath){
    super(modelPath);
  }
  public TestSymTabMillGlobalScope(ModelPath modelPath, TestSymTabMillModelLoader modelLoader){
    super(modelPath, modelLoader);
  }

  @Override public TestSymTabMillGlobalScope getRealThis() {
    return this;
  }

}
