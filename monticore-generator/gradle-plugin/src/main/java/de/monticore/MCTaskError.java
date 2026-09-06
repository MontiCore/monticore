/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

/**
 * This is only needed to prevent elongated stacktraces
 */
public class MCTaskError extends RuntimeException {
  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
