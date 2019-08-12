/* (c) https://github.com/MontiCore/monticore */
package sm2._symboltable;

import com.google.common.collect.ImmutableSet;
import de.se_rwth.commons.Names;

import java.util.Collections;
import java.util.Set;

public class SM2Language extends SM2LanguageTOP {
  public static final String FILE_ENDING = "aut";
  
  public SM2Language() {
    super("SM2 Language", FILE_ENDING);

  }
  
  @Override
  protected SM2ModelLoader provideModelLoader() {
  return new SM2ModelLoader(this);
 }
  @Override
  protected Set<String> calculateModelNamesForState(String name) {
    // e.g., if p.SM2.State, return p.SM2
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }
    
    return Collections.emptySet();
  }
 
}
