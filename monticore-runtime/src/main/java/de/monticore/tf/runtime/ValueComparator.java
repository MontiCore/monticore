/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.runtime;

import java.util.Comparator;
import java.util.Map;

/**
 * A comparator that sorts strings based on their associated integer values in descending order.
 */
public class ValueComparator implements Comparator<String> {
  
  protected Map<String, Integer> data = null;

  /**
   * Constructs a {@link ValueComparator} with the specified data map.
   *
   * @param data a map containing string keys and their associated integer values
   */
  public ValueComparator(Map<String, Integer> data) {
    this.data = data;
  }
  
  /**
   * Compares two strings based on their associated values in the data map.
   * Strings with higher values are ordered before those with lower values (descending order).
   *
   * @param o1 the first string to compare
   * @param o2 the second string to compare
   * @return a negative integer if o1's value is greater than o2's value,
   *         a positive integer if o1's value is less than or equal to o2's value,
   *         or zero if they are equal
   */
  @Override
  public int compare(String o1, String o2) {
    int value1 = (Integer) data.get(o1);
    int value2 = (Integer) data.get(o2);
    return (value1 > value2) ? -1 : (value2 >= value1) ? 1 : 0;
  }
  
}
