/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
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
package de.monticore.genericgraphics.view.figures;

import java.util.List;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Font;

/**
 * <p>
 * An extension of {@link ListContainer} that provides some functionality for
 * easily adding
 * <ul>
 * <li>{@link Label Labels}</li>
 * <li>{@link String Strings} (which will be used for a {@link Label} which is
 * then added)</li>
 * </ul>
 * </p>
 * 
 * @author Tim Enger
 */
public class LabelList extends ListContainer {
  
  private Font font;
  
  /**
   * Constructor
   */
  public LabelList() {
    // make a sandwich
  }
  
  /**
   * Constructor
   * 
   * @param font The {@link Font} to be used for all labels, when no other is
   *          given.
   */
  public LabelList(Font font) {
    this.font = font;
  }
  
  /**
   * Constructor
   * 
   * @param texts The list of String to display with each string in a separate
   *          line.
   */
  public LabelList(List<String> texts) {
    this(texts, null);
  }
  
  /**
   * Constructor
   * 
   * @param texts The list of String to display with each string in a separate
   *          line.
   * @param font The {@link Font} the font to use for the strings.
   */
  public LabelList(List<String> texts, Font font) {
    this.font = font;
    addLabels(texts, font);
  }
  
  /**
   * Creates and adds a new {@link Label Labels} to the list/container. The
   * {@link Label Labels} are added at the last positions.
   * 
   * @param labelMessages The list of strings used for the {@link Label Labels}.
   */
  public void addLabels(List<String> labelMessages) {
    for (String s : labelMessages) {
      addLabel(s, font);
    }
  }
  
  /**
   * Creates and adds a new {@link Label} to the list/container.
   * 
   * @param labelMessage The message displayed in the label.
   */
  public void addLabel(String labelMessage) {
    addLabel(labelMessage, font, -1);
  }
  
  /**
   * Creates and adds a new {@link Label Labels} to the list/container. The
   * {@link Label Labels} are added at the last positions.
   * 
   * @param labelMessages The list of strings used for the {@link Label Labels}.
   * @param font The {@link Font} to be used for the {@link Label labels}.
   */
  public void addLabels(List<String> labelMessages, Font font) {
    for (String s : labelMessages) {
      addLabel(s, font);
    }
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
    addLabel(labelMessage, font, index);
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
    addLabel(labelMessage, font, -1);
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
    Label label = new Label(labelMessage);
    if (font != null) {
      label.setFont(font);
    }
    else {
      if (this.font != null) {
        label.setFont(this.font);
      }
    }
    if (index < 0) {
      index = getChildren().size();
    }
    add(label, index);
  }
  
  @Override
  public Insets getInsets() {
    return new Insets(2, 10, 2, 10);
  }
}
