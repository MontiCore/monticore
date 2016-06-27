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
package de.monticore.genericgraphics.controller.actions;

import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;

import de.monticore.genericgraphics.controller.util.ImageSaveUtil;
import de.monticore.genericgraphics.view.icons.IconProvider;


/**
 * @author Tim Enger
 */
public class ExportAsImageAction extends EditorPartAction {
  
  /**
   * Id of this action
   */
  public static final String EXPORT_AS_IMAGE_ID = "export as image";
  
  private static final String DESCRIPTION = "Export as image";
  
  /**
   * Constructor
   * 
   * @param editor
   */
  public ExportAsImageAction(IEditorPart editor) {
    super(editor);
    
    setDescription(DESCRIPTION);
    
    Image image = IconProvider.loadImage(IconProvider.EXPORT_ICON);
    if (image!=null) {
      setImageDescriptor(ImageDescriptor.createFromImage(image));
    }
    
    setText("Export");
  }
  
  @Override
  protected void init() {
    super.init();
    setId(EXPORT_AS_IMAGE_ID);
  }
  
  @Override
  public void run() {
    ImageSaveUtil.save(getEditorPart());
  }
  
  @Override
  protected boolean calculateEnabled() {
    return getEditorPart() != null;
  }
  
  @Override
  public void setEditorPart(IEditorPart part) {
    super.setEditorPart(part);
  }
}
