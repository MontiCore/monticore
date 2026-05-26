/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine;

import java.util.ArrayList;
import java.util.List;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectFactoryTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testInstanciationWithDefaultConstructor() {
    Object obj = ObjectFactory.createObject("java.lang.String");
    assertNotNull(obj);
    assertEquals(String.class, obj.getClass());
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testInstanciationWithParams() {
    List<Object> params = new ArrayList<Object>();
    params.add("myContent");
    Object obj = ObjectFactory.createObject("java.lang.String", params);
    assertNotNull(obj);
    assertEquals(String.class, obj.getClass());
    assertEquals(obj, "myContent");
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testInstanciationWithTypesAndParams() {
    List<Class<?>> paramTypes = new ArrayList<Class<?>>();
    paramTypes.add(char[].class);
    paramTypes.add(Integer.TYPE);
    paramTypes.add(Integer.TYPE);
    
    List<Object> params = new ArrayList<Object>();
    params.add("Say yes!".toCharArray());
    params.add(4);
    params.add(3);
    
    Object obj = ObjectFactory.createObject("java.lang.String", paramTypes, params);
    assertNotNull(obj);
    assertEquals(obj.getClass(), (new String()).getClass());
    assertEquals(obj, "yes");
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
