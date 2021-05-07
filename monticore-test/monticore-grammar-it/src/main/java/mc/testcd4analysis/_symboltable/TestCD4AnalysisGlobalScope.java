/* (c) https://github.com/MontiCore/monticore */
package mc.testcd4analysis._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;
import mc.testcd4analysis.TestCD4AnalysisMill;
import mc.testcd4analysis._ast.ASTCDCompilationUnit;
import mc.testcd4analysis._parser.TestCD4AnalysisParser;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
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


  @Override
  public  void loadFileForModelName (String modelName)  {
    Optional<URL> location = getSymbolPath().find(modelName, getFileExt());
    if(location.isPresent() && !isFileLoaded(location.get().toString())){
      addLoadedFile(location.get().toString());
      ITestCD4AnalysisArtifactScope as = getSymbols2Json().load(location.get());
      addSubScope(as);
    }
    else if(!location.isPresent()){
      // load models instead of symbols
      location = getSymbolPath().find(modelName, "cd");
      if(location.isPresent() && !isFileLoaded(location.get().toString())){
        addLoadedFile(location.get().toString());
        ASTCDCompilationUnit ast = parse(location.get().getFile());
        ITestCD4AnalysisArtifactScope as = TestCD4AnalysisMill.scopesGenitorDelegator()
            .createFromAST(ast);
        addSubScope(as);
      }
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
