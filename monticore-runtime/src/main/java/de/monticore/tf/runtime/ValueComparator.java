/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<String> {
  
  private Map<String, Integer> data = null;
  
  public ValueComparator(Map<String, Integer> data) {
    this.data = data;
  }
  
  @Override
  public int compare(String o1, String o2) {
    int value1 = (Integer) data.get(o1);
    int value2 = (Integer) data.get(o2);
    return (value1 > value2) ? -1 : (value2 >= value1) ? 1 : 0;
  }
  
}
