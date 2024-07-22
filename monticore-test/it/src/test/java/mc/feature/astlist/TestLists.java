/* (c) https://github.com/MontiCore/monticore */

package mc.feature.astlist;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.ASTB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestLists extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
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
    
    Assertions.assertEquals(6, list.indexOf(g));
    list.remove(g);
    Assertions.assertEquals(-1, list.indexOf(g));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
