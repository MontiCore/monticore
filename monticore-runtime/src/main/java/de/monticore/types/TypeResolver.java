/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import java.util.Optional;
/**
 *  on 06.11.2016.
 */
public class TypeResolver<T> {
  Optional<T> result;

  public Optional<T> getResult() {
    return result;
  }

  public void setResult(T result) {
    this.result = Optional.ofNullable(result);
  }
}
