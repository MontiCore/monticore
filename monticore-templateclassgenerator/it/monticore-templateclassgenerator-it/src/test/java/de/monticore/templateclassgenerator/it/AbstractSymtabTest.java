/* (c) https://github.com/MontiCore/monticore */
package de.monticore.templateclassgenerator.it;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.java.lang.JavaDSLLanguage;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Common methods for symboltable tests
 *
 */
public class AbstractSymtabTest {
  protected static Scope createSymTab(Path modelPath) {
    ModelingLanguageFamily fam = new ModelingLanguageFamily();
    fam.addModelingLanguage(new JavaDSLLanguage());
    
    final ModelPath mp = new ModelPath(modelPath, Paths.get("src/main/resources/defaultTypes"));
    GlobalScope scope = new GlobalScope(mp, fam);
    return scope;
  }
  
  protected static Scope createJavaSymTab(Path... modelPath) {
    ModelingLanguageFamily fam = new ModelingLanguageFamily();
    fam.addModelingLanguage(new JavaDSLLanguage());
    final ModelPath mp = new ModelPath(modelPath);
    GlobalScope scope = new GlobalScope(mp, fam);
    return scope;
  }
}
