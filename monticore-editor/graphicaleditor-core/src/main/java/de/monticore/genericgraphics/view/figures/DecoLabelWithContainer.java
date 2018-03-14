/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import de.monticore.genericgraphics.view.decorations.IDecoratorDisplay;
import de.monticore.genericgraphics.view.figures.borders.BottomSeparatorBorder;
import de.monticore.genericgraphics.view.figures.layout.ToolbarLayoutWithMinimumSize;


/**
 * A container figure, that has a {@link DecoratableLabel} at the top and a
 * {@link BottomSeparatorBorder} below the {@link DecoratableLabel}.
 * 
 * @author Tim Enger
 */
public class DecoLabelWithContainer extends RectangleFigure implements IDecoratorDisplay {
  
  /* attributes */
  boolean right;
  private Figure container;
  private DecoratableLabel label;
  
  /**
   * Constructor
   * 
   * @param labelName Name to display in the {@link DecoratableLabel}
   * @param labelFont {@link Font} of the {@link Label}
   * @param right right If <tt>true</tt> the decorator will be placed on the
   *          right side, otherwise left.
   * @param labelAlignment Determine alignment of label. Use {@link SWT}
   *          constants.
   */
  public DecoLabelWithContainer(String labelName, Font labelFont, boolean right, int labelAlignment) {
    // label
    label = new DecoratableLabel(labelName, labelFont, right, labelAlignment);
    label.setBorder(new BottomSeparatorBorder(0, 0, 0, 0, 1, ColorConstants.black));
    
    // container
    container = new Figure();
    ToolbarLayout layout = new ToolbarLayoutWithMinimumSize();
    layout.setStretchMinorAxis(true);
    container.setLayoutManager(layout);
    
    // figure itself
    setLayoutManager(new ToolbarLayout());
    add(label);
    add(container);
  }
  
  @Override
  public void setDecorator(Image img, List<String> messages) {
    getLabel().setDecorator(img, messages);
  }
  
  @Override
  public void deleteDecorator() {
    getLabel().deleteDecorator();
    
  }
  
  /**
   * @return The {@link Figure} used as container.
   */
  public Figure getContainer() {
    return container;
  }
  
  /**
   * @return The {@link DecoratableLabel} used as label.
   */
  public DecoratableLabel getLabel() {
    return label;
  }
  
  // use this paint method if you like to have a background with gradient colors
  
  // @Override
  // public void paint(Graphics graphics) {
  // Rectangle r = getBounds();
  // int alpha1 = 1000;
  // int alpha2 = 100;
  // Pattern pattern = RenderingUtils.createScaledPattern(graphics,
  // Display.getCurrent(), r.x, r.y, r.x + r.width, r.y + r.height,
  // ColorConstants.white, alpha1, ColorConstants.orange, alpha2);
  // graphics.setBackgroundPattern(pattern);
  // super.paint(graphics);
  // }
}
