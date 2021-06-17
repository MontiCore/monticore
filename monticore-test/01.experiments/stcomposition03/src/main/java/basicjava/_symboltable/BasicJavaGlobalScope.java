/* (c) https://github.com/MontiCore/monticore */

package basicjava._symboltable;

import basicjava.BasicJavaMill;
import basicjava._ast.ASTCompilationUnit;
import basicjava._parser.BasicJavaParser;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Optional;

public class BasicJavaGlobalScope extends BasicJavaGlobalScopeTOP {

  public BasicJavaGlobalScope(MCPath symbolPath,
                              String modelFileExtension) {
    super(symbolPath, modelFileExtension);
  }

  public BasicJavaGlobalScope() {
  }

  @Override public BasicJavaGlobalScope getRealThis() {
    return this;
  }

  public  void loadFileForModelName (String modelName)  {
    // 1. call super implementation to start with employing the DeSer
    super.loadFileForModelName(modelName);


    // 2. calculate potential location of model file and try to find it in model path
    Optional<URL> location = getSymbolPath().find(modelName, "javamodel");

    // 3. if the file was found, parse the model and create its symtab
    if(location.isPresent()){
      ASTCompilationUnit ast = parse(location.get().getPath());
      BasicJavaMill.scopesGenitorDelegator().createFromAST(ast);
    }
  }

  private ASTCompilationUnit parse(String model){
    try {
      Reader reader = new FileReader(model);
      Optional<ASTCompilationUnit> optAST = new BasicJavaParser().parse(reader);
      if(optAST.isPresent()){
        return optAST.get();
      }
    }
    catch (IOException e) {
      Log.error("0x1A234 Error while parsing model", e);
    }
    return null;
  }

}
