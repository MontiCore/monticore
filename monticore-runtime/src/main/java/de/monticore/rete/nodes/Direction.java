/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete.nodes;

/**
 * Direction of a delta propagated through a Rete network.
 */
public enum Direction {

  INSERT, DELETE;

  public Direction opposite() {
    return this == INSERT ? DELETE : INSERT;
  }
}
