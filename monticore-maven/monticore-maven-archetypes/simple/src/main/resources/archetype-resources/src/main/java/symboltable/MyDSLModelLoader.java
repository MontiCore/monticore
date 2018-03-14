/* (c) https://github.com/MontiCore/monticore */

package ${package}.symboltable;

import ${package}.lang.MyDSLLanguage;
import ${package}.mydsl._ast.ASTMyModel;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class MyDSLModelLoader extends ModelingLanguageModelLoader<ASTMyModel> {
  
  public MyDSLModelLoader(MyDSLLanguage language) {
    super(language);
  }
  
  @Override
  protected void createSymbolTableFromAST(final ASTMyModel ast, final String modelName,
      final MutableScope enclosingScope, final ResolvingConfiguration resolvingConfiguration) {
    final MyDSLSymbolTableCreator symbolTableCreator = getModelingLanguage()
        .getSymbolTableCreator(resolvingConfiguration, enclosingScope).orElse(null);
    
    if (symbolTableCreator != null) {
      Log.info("Start creation of symbol table for model \"" + modelName + "\".",
          MyDSLModelLoader.class.getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);
      
      if (!(scope instanceof ArtifactScope)) {
        Log.warn("0xC0010 Top scope of model " + modelName + " is expected to be an artifact scope, but"
            + " is scope \"" + scope.getName() + "\"");
      }
      
      Log.info("Created symbol table for model \"" + modelName + "\".", MyDSLModelLoader.class
          .getSimpleName());
    }
    else {
      Log.warn("0xC0011 No symbol created, because '" + getModelingLanguage().getName()
          + "' does not define a symbol table creator.");
    }
  }
  
  @Override
  public MyDSLLanguage getModelingLanguage() {
    return (MyDSLLanguage) super.getModelingLanguage();
  }
}
