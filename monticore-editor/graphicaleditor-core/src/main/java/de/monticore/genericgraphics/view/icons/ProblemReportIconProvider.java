/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.icons;

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
