/*******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.monticore.genericgraphics.controller.util;

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
