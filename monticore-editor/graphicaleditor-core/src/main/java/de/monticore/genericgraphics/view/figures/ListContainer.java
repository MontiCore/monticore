/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;

/**
 * <p>
 * A simple container figure, that arranges its elements in a list.
 * </p>
 * <p>
 * To arrange its children this figure uses a {@link ToolbarLayout}.
 * </p>
 * 
 * @author Tim Enger
 */
public class ListContainer extends Figure {
  
  /**
   * <p>
   * Constructor
   * </p>
   * uses {@link ToolbarLayout#ALIGN_TOPLEFT} as default label alignment.
   */
  public ListContainer() {
    this(ToolbarLayout.ALIGN_TOPLEFT);
  }
  
  /**
   * @param labelAlignment Choose between {@link ToolbarLayout#ALIGN_TOPLEFT},
   *          {@link ToolbarLayout#ALIGN_BOTTOMRIGHT} and
   *          {@link ToolbarLayout#ALIGN_CENTER}.
   */
  public ListContainer(int labelAlignment) {
    // layout
    ToolbarLayout layout = new ToolbarLayout();
    layout.setMinorAlignment(labelAlignment);
    layout.setStretchMinorAxis((true));
    setLayoutManager(layout);
  }
  
  @Override
  public Insets getInsets() {
    // top, left, bottom, right
    return new Insets(0, 0, 0, 0);
  }
}
