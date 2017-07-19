/*******************************************************************************
 * MontiCore Language Workbench
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
package de.monticore.genericgraphics.model.graphics.impl;

import java.util.Observable;

import de.monticore.genericgraphics.controller.util.IObservable;
import de.monticore.genericgraphics.model.graphics.IViewElement;


/**
 * <p>
 * The abstract base class for all {@link IViewElement IViewElements}.
 * </p>
 * This class extends the {@link IObservable} class and thus is part of the
 * Observer pattern.
 * 
 * @author Tim Enger
 */
public abstract class AbstractViewElement extends Observable implements IObservable, IViewElement {
  
  /**
   * generated Serial UID
   */
  private static final long serialVersionUID = -5476295308717102943L;
  
  private String identifier;
  
  /**
   * Constructor
   * 
   * @param identifier The identifier.
   */
  public AbstractViewElement(String identifier) {
    this.identifier = identifier;
  }
  
  @Override
  public String getIdentifier() {
    return identifier;
  }
  
  /**
   * @param identifier The identifier to set
   */
  public void setUniqueIdentifier(String identifier) {
    this.identifier = identifier;
  }
  
  @Override
  public abstract Object clone();
}
