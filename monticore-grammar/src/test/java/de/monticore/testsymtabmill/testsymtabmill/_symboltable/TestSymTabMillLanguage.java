/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testsymtabmill.testsymtabmill._symboltable;

public class TestSymTabMillLanguage extends TestSymTabMillLanguageTOP {


  public TestSymTabMillLanguage(){
    super("TestSymTabMillLanguage","ts");
  }

  @Override
  protected TestSymTabMillModelLoader provideModelLoader() {
    return null;
  }
}
