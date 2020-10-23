/* (c) https://github.com/MontiCore/monticore */

package basicjava._symboltable;

import basicjava.BasicJavaMill;
import basicjava._ast.ASTCompilationUnit;
import basicjava._parser.BasicJavaParser;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

public class BasicJavaGlobalScope extends BasicJavaGlobalScopeTOP {

  public BasicJavaGlobalScope(ModelPath modelPath,
      String modelFileExtension) {
    super(modelPath, modelFileExtension);
  }

  public BasicJavaGlobalScope() {
  }

  public  void loadFileForModelName (String modelName, String symbolName)  {
    super.loadFileForModelName(modelName, symbolName);
    ModelCoordinate model = ModelCoordinates.createQualifiedCoordinate(modelName, getModelFileExtension());
    model = getModelPath().resolveModel(model);
    if(model.hasLocation()){
      ASTCompilationUnit ast = parse(model);
      BasicJavaMill.basicJavaSymbolTableCreator().createFromAST(ast);
    }
  }

  @Override public BasicJavaGlobalScope getRealThis() {
    return this;
  }

  private ASTCompilationUnit parse(ModelCoordinate model){
    try {
      Reader reader = ModelCoordinates.getReader(model);
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
