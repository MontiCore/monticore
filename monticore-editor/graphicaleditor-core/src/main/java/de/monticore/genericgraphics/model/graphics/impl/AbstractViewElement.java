/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model.graphics.impl;

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
