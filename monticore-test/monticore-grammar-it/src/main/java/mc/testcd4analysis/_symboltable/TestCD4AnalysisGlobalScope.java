/* (c) https://github.com/MontiCore/monticore */
package mc.testcd4analysis._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.io.paths.MCPath;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;
import mc.testcd4analysis.TestCD4AnalysisMill;
import mc.testcd4analysis._ast.ASTCDCompilationUnit;
import mc.testcd4analysis._parser.TestCD4AnalysisParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TestCD4AnalysisGlobalScope extends TestCD4AnalysisGlobalScopeTOP{

  public TestCD4AnalysisGlobalScope(MCPath symbolPath) {
    super(symbolPath, "cd");
  }

  public TestCD4AnalysisGlobalScope(MCPath symbolPath, String modelFileExtension) {
    super(symbolPath, modelFileExtension);
  }

  public TestCD4AnalysisGlobalScope(){
    super();
  }

  @Override
  public Set<String> calculateModelNamesForCDType(String name) {
    // e.g., if p.CD.Clazz, return p.CD
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }
    return Collections.emptySet();
  }

  @Override
  public Set<String> calculateModelNamesForCDMethOrConstr(String name) {
    // e.g., if p.CD.Clazz.Meth return p.CD
    List<String> nameParts = Splitters.DOT.splitToList(name);

    // at least 3, because of CD.Clazz.meth
    if (nameParts.size() >= 3) {
      // cut the last two name parts (e.g., Clazz.meth)
      return ImmutableSet.of(Joiners.DOT.join(nameParts.subList(0, nameParts.size()-2)));
    }
    return Collections.emptySet();
  }

  @Override
  public Set<String> calculateModelNamesForCDField(String name) {
    // e.g., if p.CD.Clazz.Field return p.CD
    List<String> nameParts = Splitters.DOT.splitToList(name);

    // at least 3, because of CD.Clazz.field
    if (nameParts.size() >= 3) {
      // cut the last two name parts (e.g., Clazz.field)
      return ImmutableSet.of(Joiners.DOT.join(nameParts.subList(0, nameParts.size()-2)));
    }
    return Collections.emptySet();
  }

  @Override
  public TestCD4AnalysisGlobalScope getRealThis(){
    return this;
  }

  public  void loadFileForModelName (String modelName)  {
    String symbolFileExtension = getFileExt() + "sym";
    Optional<URL> location = getSymbolPath().find(modelName, symbolFileExtension);
    String filePath = Paths.get(Names.getPathFromPackage(modelName) + "." + symbolFileExtension).toString();
    if(!isFileLoaded(filePath)) {
      addLoadedFile(filePath);

      //Load symbol table into enclosing global scope if a file has been found
      if (location.isPresent()) {
        URL url = location.get();
        this.addSubScope(symbols2Json.load(url));
      }

      // else, use try to load model (instead of symbol table)
      Optional<URL> modelLocation = getSymbolPath().find(modelName, getFileExt());

      // 3. if the file was found, parse the model and create its symtab
      if(location.isPresent()){
        ASTCDCompilationUnit ast = parse(location.get().getPath());
        ITestCD4AnalysisArtifactScope artScope = new TestCD4AnalysisPhasedSymbolTableCreatorDelegator().createFromAST(ast);
        addSubScope(artScope);
        addLoadedFile(filePath);
      }
    } else {
      Log.debug("Already tried to load model for '" + modelName + "'. If model exists, continue with cached version.",
          "TestCD4AnalysisGlobalScope");
    }
  }

  private ASTCDCompilationUnit parse(String model){
    try {
      Optional<ASTCDCompilationUnit> optAST = new TestCD4AnalysisParser().parse(new FileReader(model));
      if(optAST.isPresent()){
        return optAST.get();
      }
    }
    catch (IOException e) {
      Log.error("Error while parsing model", e);
    }
    return null;
  }


}
