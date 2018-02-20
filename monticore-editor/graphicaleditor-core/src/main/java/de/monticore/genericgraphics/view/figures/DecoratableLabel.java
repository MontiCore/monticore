/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import de.monticore.genericgraphics.view.decorations.IDecoratorDisplay;


/**
 * <p>
 * A decoratable label implementation.
 * </p>
 * <p>
 * This figure displays a {@link Label} in a {@link RoundedRectangle}, that is
 * decoratable, and thus implements {@link IDecoratorDisplay}.<br>
 * <br>
 * Per default this figure has no border.
 * </p>
 * 
 * @author Tim Enger
 */
public class DecoratableLabel extends Figure implements IDecoratorDisplay {
  
  private Label label;
  private boolean right;
  private IFigure decorator;
  private GridLayout layout;
  private boolean reserveSpace;
  
  /**
   * <p>
   * Constructor
   * </p>
   * <p>
   * Uses {@link #DecoratableLabel(String, Font, boolean, int, boolean)
   * DecoratableLabel(String, Font, boolean, int, true)}, so reserves space for
   * decoraters per default.
   * </p>
   * 
   * @param name Name to display in the {@link Label}
   * @param labelFont {@link Font} of the {@link Label}
   * @param right If <tt>true</tt> the decorator will be placed on the right
   *          side, otherwise left.
   * @param labelAlignment Determine alignment of label. Use {@link SWT}
   *          constants.
   */
  public DecoratableLabel(String name, Font labelFont, boolean right, int labelAlignment) {
    this(name, labelFont, right, labelAlignment, true);
  }
  
  /**
   * Constructor
   * 
   * @param name Name to display in the {@link Label}
   * @param labelFont {@link Font} of the {@link Label}
   * @param right If <tt>true</tt> the decorator will be placed on the right
   *          side, otherwise left.
   * @param labelAlignment Determine alignment of label. Use {@link SWT}
   *          constants.
   * @param reserveSpace If <tt>True</tt>, the preferred size of the label
   *          includes reserved space for possible decorators.
   */
  public DecoratableLabel(String name, Font labelFont, boolean right, int labelAlignment, boolean reserveSpace) {
    this.right = right;
    this.reserveSpace = reserveSpace;
    decorator = null;
    
    layout = new GridLayout(2, false);
    layout.verticalSpacing = 0;
    layout.horizontalSpacing = 0;
    layout.marginHeight = 0;
    layout.marginWidth = 2;
    setLayoutManager(layout);
    
    label = new Label(name);
    label.setFont(labelFont);
    label.setOpaque(false);
    
    setOpaque(false);
    
    add(label);
    layout.setConstraint(label, new GridData(labelAlignment, SWT.CENTER, true, true));
  }
  
  /**
   * Sets the text
   * 
   * @param text The text to set
   */
  public void setText(String text) {
    label.setText(text);
  }
  
  @Override
  public void setDecorator(Image img, List<String> messages) {
    if (decorator != null) {
      deleteDecorator();
    }
    
    decorator = new ImageFigure(img);
    
    LabelList toolTip = new LabelList();
    for (String m : messages) {
      toolTip.addLabel(m);
    }
    decorator.setToolTip(toolTip);
    
    if (right) {
      add(decorator, 1);
      layout.setConstraint(decorator, new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
    }
    else {
      add(decorator, 0);
      layout.setConstraint(decorator, new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
    }
    
    repaint();
    revalidate();
  }
  
  @Override
  public void deleteDecorator() {
    if (decorator != null) {
      remove(decorator);
      decorator = null;
    }
  }
  
  @Override
  public Insets getInsets() {
    return new Insets(0, 0, 0, 0);
  }
  
  @Override
  public final Dimension getPreferredSize(int w, int h) {
    Dimension labelSize = super.getPreferredSize(w, h);
    
    int width = labelSize.width;
    int height = labelSize.height;
    
    if (reserveSpace) {
      // reserve some space for future decorator
      width += 15;
    }
    
    return new Dimension(width, height);
  }
  
  /**
   * @return The label
   */
  public Label getLabel() {
    return label;
  }
  
  /**
   * @param label The label to set
   */
  public void setLabel(Label label) {
    this.label = label;
  }
  
}
