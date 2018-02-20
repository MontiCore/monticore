/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model.graphics;

import java.io.Serializable;

import de.monticore.genericgraphics.controller.persistence.IIdentifiable;
import de.monticore.genericgraphics.controller.util.IObservable;


/**
 * Common interface for all View elements storing view information.
 * 
 * @author Tim Enger
 */
public interface IViewElement extends IIdentifiable, IObservable, Serializable, Cloneable {
  
  /**
   * @return A clone of this object.
   */
  public Object clone();
  
}
