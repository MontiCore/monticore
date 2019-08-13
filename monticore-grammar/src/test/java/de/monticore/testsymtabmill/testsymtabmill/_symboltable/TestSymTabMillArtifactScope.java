/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testsymtabmill.testsymtabmill._symboltable;

import de.monticore.symboltable.ImportStatement;

import java.util.List;
import java.util.Optional;

public class TestSymTabMillArtifactScope extends TestSymTabMillArtifactScopeTOP {

  public TestSymTabMillArtifactScope(String packageName, List<ImportStatement> imports){
    super(packageName,imports);
  }

  public TestSymTabMillArtifactScope(Optional<ITestSymTabMillScope> enclosingScope, String packageName, List<ImportStatement> imports){
    super(enclosingScope,packageName,imports);
  }

}
