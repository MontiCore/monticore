/* (c) https://github.com/MontiCore/monticore */
package mc.testcd4analysis._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TestCD4AnalysisGlobalScope extends TestCD4AnalysisGlobalScopeTOP{

  public TestCD4AnalysisGlobalScope(ModelPath mp) {
    super(mp, "cd");
  }

  public TestCD4AnalysisGlobalScope(ModelPath modelPath, String modelFileExtension) {
    super(modelPath, modelFileExtension);
  }

  @Override
  public Set<String> calculateModelNamesForCDType(String name) {
    // e.g., if p.CD.CD.Clazz, return p.CD
    if (!Names.getQualifier(name).isEmpty()) {
      String cdSymbolFullName = Names.getQualifier(name);
      if(!Names.getQualifier(cdSymbolFullName).isEmpty()){
        return ImmutableSet.of(Names.getQualifier(cdSymbolFullName));
      }
    }
    return Collections.emptySet();
  }

  @Override
  public Set<String> calculateModelNamesForCDMethOrConstr(String name) {
    // e.g., if p.CD.CD.Clazz.Meth return p.CD
    List<String> nameParts = Splitters.DOT.splitToList(name);

    // at least 4, because of CD.CD.Clazz.meth
    if (nameParts.size() >= 4) {
      // cut the last three name parts (e.g., CD.Clazz.meth)
      return ImmutableSet.of(Joiners.DOT.join(nameParts.subList(0, nameParts.size()-3)));
    }
    return Collections.emptySet();
  }

  @Override
  public Set<String> calculateModelNamesForCDField(String name) {
    // e.g., if p.CD.CD.Clazz.Field return p.CD
    List<String> nameParts = Splitters.DOT.splitToList(name);

    // at least 3, because of CD.CD.Clazz.field
    if (nameParts.size() >= 4) {
      // cut the last three name parts (e.g., CD.Clazz.field)
      return ImmutableSet.of(Joiners.DOT.join(nameParts.subList(0, nameParts.size()-3)));
    }
    return Collections.emptySet();
  }

  @Override
  public TestCD4AnalysisGlobalScope getRealThis(){
    return this;
  }
}
