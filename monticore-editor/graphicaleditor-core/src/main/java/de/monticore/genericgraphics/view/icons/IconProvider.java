/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.icons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Class for providing icons. <br>
 * Icon downloaded from http://eclipse-icons.i24.cc/<br>
 * under EPL.
 * 
 * @author Tim Enger
 */
public class IconProvider {
  
  /**
   * Path for all icons
   */
  public final static String PATH = "icons/";
  
  private static Map<String, Image> imageSet = new java.util.HashMap<>();
  
  /**
   * export icon
   */
  public final static String EXPORT_ICON = PATH + "export.gif";
  
  /**
   * Load an image file.
   * 
   * @param display The {@link Display}
   * @param clazz The {@link Class}
   * @param path The path to the image
   * @return The loaded {@link Image}
   */
  public static Image loadImage(Display display, Class<?> clazz, String path) {
    Image image = imageSet.get(path);
    if (image == null) {
      InputStream stream = clazz.getClassLoader().getResourceAsStream(path);
      if (stream == null) {
        return null;
      }
      
      try {
        image = new Image(display, stream);
        imageSet.put(path, image);
      }
      catch (SWTException ex) {
      }
      finally {
        try {
          stream.close();
        }
        catch (IOException ex) {
        }
      }
    }
    return image;
  }
  
  /**
   * Load an image file using the {@link #loadImage(Display, Class, String)} method, with the
   * following parameters:
   * 
   * <pre>
   * <code>loadImage(null, IconProvider.class, path)</code>
   * </pre>
   * 
   * @param path The path to the image
   * @return The loaded {@link Image}
   */
  public static Image loadImage(String path) {
    Image image = IconProvider.loadImage(null, IconProvider.class, path);
    return image;
  }
}
