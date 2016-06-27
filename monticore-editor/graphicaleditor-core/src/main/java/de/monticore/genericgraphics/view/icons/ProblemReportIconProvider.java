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
package de.monticore.genericgraphics.view.icons;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.se_rwth.commons.logging.Finding.Type;

/**
 * A utility class for loading ProblemReport images/icons.
 * 
 * @author Tim Enger
 */
public class ProblemReportIconProvider {
  
  /**
   * Get an {@link Image icon} for a {@link Type}
   * 
   * @param type The {@link Type} for the {@link ProblemReport}.
   * @return The corresponding {@link Image icon}.
   */
  public static Image getIconForProblemReportType(Type type) {
    Image image = null;
    switch (type) {
      case ERROR:
        image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
        break;
      case WARNING:
        image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_WARNING);
        break;
      default:
    }
    
    
    return image;
  }
}
