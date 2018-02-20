/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.util;

import java.util.Observer;

/**
 * <p>
 * Interface for being observable.
 * </p>
 * <p>
 * Unfortunately, the {@link java.util.Observable} does not provide a interface,
 * for a class to be be observable which I could have used instead. So I just
 * used the method signatures of {@link java.util.Observable} as inspiration :).
 * </p>
 * 
 * @author Tim Enger
 */
public interface IObservable {
  
  /**
   * @see java.util.Observable#addObserver(Observer)
   * @param o
   */
  public void addObserver(Observer o);
  
  /**
   * @see java.util.Observable#deleteObserver(Observer)
   * @param o
   */
  public void deleteObserver(Observer o);
  
  /**
   * @see java.util.Observable#deleteObservers()
   */
  public void deleteObservers();
  
  /**
   * @see java.util.Observable#notifyObservers()
   */
  public void notifyObservers();
  
  /**
   * @see java.util.Observable#notifyObservers(Object)
   * @param arg
   */
  public void notifyObservers(Object arg);
}
