/* (c) https://github.com/MontiCore/monticore */

package mc.feature.astlist;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTB;
import mc.feature.featuredsl._ast.FeatureDSLNodeFactory;

public class TestLists extends GeneratorIntegrationsTest {
  
  /** Tests if remove function works correctly with the equals method */
  @Test
  public void testLists() {
    
    ASTB a = FeatureDSLNodeFactory.createASTB();
    ASTB b = FeatureDSLNodeFactory.createASTB();
    ASTB c = FeatureDSLNodeFactory.createASTB();
    ASTB d = FeatureDSLNodeFactory.createASTB();
    ASTB e = FeatureDSLNodeFactory.createASTB();
    ASTB f = FeatureDSLNodeFactory.createASTB();
    ASTB g = FeatureDSLNodeFactory.createASTB();
    
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
    
  }
}
