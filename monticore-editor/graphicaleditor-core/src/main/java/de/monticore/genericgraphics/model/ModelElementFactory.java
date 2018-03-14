/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.model;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.swt.graphics.Font;

import de.monticore.genericgraphics.model.impl.FigureConnectionLabel;
import de.monticore.genericgraphics.model.impl.TextConnectionLabel;
import de.monticore.genericgraphics.view.figures.connections.locators.ConnectionLocatorPosition;


/**
 * Factory for a model elements.
 * 
 * @author Tim Enger
 */
public class ModelElementFactory {
  
  private static ModelElementFactory factory = null;
  
  private ModelElementFactory() {
  }
  
  private static void check() {
    if (factory == null) {
      factory = new ModelElementFactory();
    }
  }
  
  /**
   * Factory method for {@link ITextConnectionLabel}
   * 
   * @param position The {@link Locator}
   * @param texts The list of strings
   * @param font The {@link Font} to be used for displaying the text
   * @return The {@link ITextConnectionLabel}
   */
  public static ITextConnectionLabel createTextConnectionLabel(ConnectionLocatorPosition position, List<String> texts, Font font) {
    check();
    return factory.doCreateTextConnectionLabel(position, texts, font);
  }
  
  protected ITextConnectionLabel doCreateTextConnectionLabel(ConnectionLocatorPosition position, List<String> texts, Font font) {
    return new TextConnectionLabel(position, texts, font);
  }
  
  /**
   * Factory method for {@link ITextConnectionLabel}
   * 
   * @param position The {@link Locator}
   * @param text A single string
   * @param font The {@link Font} to be used for displaying the text
   * @return The {@link ITextConnectionLabel}
   */
  public static ITextConnectionLabel createTextConnectionLabel(ConnectionLocatorPosition position, String text, Font font) {
    check();
    return factory.doCreateTextConnectionLabel(position, text, font);
  }
  
  protected ITextConnectionLabel doCreateTextConnectionLabel(ConnectionLocatorPosition position, String text, Font font) {
    return new TextConnectionLabel(position, text, font);
  }
  
  /**
   * Factory method for {@link IFigureConnectionLabel}
   * 
   * @param position The {@link Locator}
   * @param figure The {@link IFigure} the label represents
   * @param children The list of children objects
   * @return The {@link IFigureConnectionLabel}
   */
  public static IFigureConnectionLabel createFigureConnectionLabel(ConnectionLocatorPosition position, IFigure figure, List<Object> children) {
    check();
    return factory.doCreateFigureConnectionLabel(position, figure, children);
  }
  
  protected IFigureConnectionLabel doCreateFigureConnectionLabel(ConnectionLocatorPosition position, IFigure figure, List<Object> children) {
    return new FigureConnectionLabel(position, figure, children);
  }
}
