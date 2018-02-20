/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model;

import java.util.List;

import org.eclipse.swt.graphics.Font;

import de.monticore.genericgraphics.view.figures.connections.locators.ConnectionLocatorPosition;


/**
 * <p>
 * Interface for text connection labels.
 * </p>
 * <p>
 * A text connection label consists of a list of strings, a {@link Font} and a
 * {@link ConnectionLocatorPosition}.
 * </p>
 * 
 * @author Tim Enger
 */
public interface ITextConnectionLabel extends IConnectionLabel {
  
  /**
   * @return The list of strings
   */
  public List<String> getTexts();
  
  /**
   * @param texts The list of strings to set
   */
  public void setTexts(List<String> texts);
  
  /**
   * @return The {@link Font}
   */
  public Font getFont();
  
  /**
   * @param font The {@link Font} to set
   */
  public void setFont(Font font);
}
