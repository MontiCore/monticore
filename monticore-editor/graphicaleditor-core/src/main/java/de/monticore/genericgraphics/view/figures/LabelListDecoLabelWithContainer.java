/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

/**
 * A {@link IFigure} with: (in descending order)
 * <ul>
 * <li>{@link LabelList}</li>
 * <li>{@link DecoratableLabel}</li>
 * <li>{@link Figure Container}</li>
 * </ul>
 * Note: The {@link LabelList} is created on demand, so only if needed.
 * 
 * @author Tim Enger
 */
public class LabelListDecoLabelWithContainer extends DecoLabelWithContainer {
  
  private LabelList labelList;
  
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
  public LabelListDecoLabelWithContainer(String labelName, Font labelFont, boolean right, int labelAlignment) {
    super(labelName, labelFont, right, labelAlignment);
  }
  
  /**
   * @return The {@link LabelList} used
   */
  public LabelList getLabelList() {
    if (labelList == null) {
      labelList = new LabelList();
      add(labelList, 0);
    }
    return labelList;
  }
  
  /**
   * Creates and adds a new {@link Label} to the list/container. The
   * {@link Label} is added at the last position.
   * 
   * @param labelMessage The message displayed in the label.
   */
  public void addLabel(String labelMessage) {
    getLabelList().addLabel(labelMessage);
  }
  
  /**
   * Creates and adds a new {@link Label} to the list/container.
   * 
   * @param labelMessage The message displayed in the label.
   * @param index The index at which the new {@link Label} should be added. If
   *          <code>-1</code>, the new {@link Label} is added at the last
   *          position.
   */
  public void addLabel(String labelMessage, int index) {
    getLabelList().addLabel(labelMessage, index);
  }
  
  /**
   * Creates and adds a new {@link Label} to the list/container. The
   * {@link Label} is added at the last position.
   * 
   * @param labelMessage The message displayed in the label.
   * @param font The {@link Font} to use. If <tt>null</tt> no {@link Font} is
   *          set.
   */
  public void addLabel(String labelMessage, Font font) {
    getLabelList().addLabel(labelMessage, font);
  }
  
  /**
   * Creates and adds a new {@link Label} to the list/container.
   * 
   * @param labelMessage The message displayed in the label.
   * @param font The {@link Font} to use. If <tt>null</tt> no {@link Font} is
   *          set.
   * @param index The index at which the new {@link Label} should be added. If
   *          <code>-1</code>, the new {@link Label} is added at the last
   *          position.
   */
  public void addLabel(String labelMessage, Font font, int index) {
    getLabelList().addLabel(labelMessage, font, index);
  }
}
