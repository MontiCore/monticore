/* (c) https://github.com/MontiCore/monticore */
package mc.testcd4analysis._symboltable;

import com.google.common.collect.ImmutableSet;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TestCD4AnalysisLanguage extends TestCD4AnalysisLanguageTOP{

  public static final String FILE_ENDING = "cd";

  public TestCD4AnalysisLanguage() {
    super("CD 4 Analysis Language", FILE_ENDING);
  }

  @Override
  protected TestCD4AnalysisModelLoader provideModelLoader() {
    return new TestCD4AnalysisModelLoader(this);
  }

  @Override
  protected Set<String> calculateModelNamesForCDType(String name) {
    // e.g., if p.CD.Clazz, return p.CD
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }
    return Collections.emptySet();
  }

  @Override
  protected Set<String> calculateModelNamesForCDMethOrConstr(String name) {
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
  protected Set<String> calculateModelNamesForCDField(String name) {
    // e.g., if p.CD.Clazz.Field return p.CD
    List<String> nameParts = Splitters.DOT.splitToList(name);

    // at least 3, because of CD.Clazz.field
    if (nameParts.size() >= 3) {
      // cut the last two name parts (e.g., Clazz.field)
      return ImmutableSet.of(Joiners.DOT.join(nameParts.subList(0, nameParts.size()-2)));
    }
    return Collections.emptySet();
  }
}
