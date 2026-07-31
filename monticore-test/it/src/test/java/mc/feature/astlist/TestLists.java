/* (c) https://github.com/MontiCore/monticore */

package mc.feature.astlist;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.ASTB;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestLists extends GeneratorIntegrationsTest {
  
  /** Tests if remove function works correctly with the equals method */
  @Test
  public void testLists() {
    
    ASTB a = FeatureDSLMill.bBuilder().uncheckedBuild();
    ASTB b = FeatureDSLMill.bBuilder().uncheckedBuild();
    ASTB c = FeatureDSLMill.bBuilder().uncheckedBuild();
    ASTB d = FeatureDSLMill.bBuilder().uncheckedBuild();
    ASTB e = FeatureDSLMill.bBuilder().uncheckedBuild();
    ASTB f = FeatureDSLMill.bBuilder().uncheckedBuild();
    ASTB g = FeatureDSLMill.bBuilder().uncheckedBuild();
    
    ArrayList<ASTB> list = new ArrayList<>();
    list.add(a);
    list.add(b);
    list.add(c);
    list.add(d);
    list.add(e);
    list.add(f);
    list.add(g);
    
    assertEquals(6, list.indexOf(g));
    list.remove(g);
    assertEquals(-1, list.indexOf(g));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
