/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.util;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;

import de.se_rwth.commons.logging.Log;

/**
 * <p>
 * Utility class for saving images of {@link IEditorPart IEditorParts} with
 * {@link GraphicalViewer}.
 * </p>
 * The following formats are supported:
 * <ul>
 * <li>{@link SWT#IMAGE_BMP}</li>
 * <li>{@link SWT#IMAGE_ICO}</li>
 * <li>{@link SWT#IMAGE_JPEG}</li>
 * <li>{@link SWT#IMAGE_PNG}</li>
 * <li>{@link SWT#IMAGE_GIF}</li>
 * </ul>
 * 
 * @author Tim Enger
 */
public class ImageSaveUtil {
  
  /**
   * <p>
   * Save editor content as image.
   * </p>
   * 
   * @param editorPart The {@link IEditorPart}
   * @param viewer The {@link GraphicalViewer} that stores the figures
   * @param saveFilePath The file path
   * @param format The format of the image. Choose between:
   *          <ul>
   *          <li>{@link SWT#IMAGE_BMP}</li>
   *          <li>{@link SWT#IMAGE_ICO}</li>
   *          <li>{@link SWT#IMAGE_JPEG}</li>
   *          <li>{@link SWT#IMAGE_PNG}</li>
   *          <li>{@link SWT#IMAGE_GIF}</li>
   *          </ul>
   * @return <tt>True</tt> if save was successful, otherwise <tt>false</tt>.
   */
  public static boolean save(IEditorPart editorPart, GraphicalViewer viewer, String saveFilePath, int format) {
    if (editorPart == null || viewer == null || saveFilePath == null) {
      return false;
    }
    
    if (format != SWT.IMAGE_BMP && format != SWT.IMAGE_JPEG && format != SWT.IMAGE_ICO && format != SWT.IMAGE_PNG && format != SWT.IMAGE_GIF) {
      throw new IllegalArgumentException("Save format not supported");
    }
    
    try {
      saveEditorContentsAsImage(editorPart, viewer, saveFilePath, format);
    }
    catch (Exception ex) {
      MessageDialog.openError(editorPart.getEditorSite().getShell(), "Save Error", "Could not save editor contents");
      return false;
    }
    
    return true;
  }
  
  /**
   * <p>
   * Save editor content as image.
   * </p>
   * <p>
   * Ask the user for the file path and format.
   * </p>
   * 
   * @param editorPart The {@link IEditorPart}
   * @param viewer The {@link GraphicalViewer} that stores the figures
   * @return <tt>True</tt> if save was successful, otherwise <tt>false</tt>.
   */
  public static boolean save(IEditorPart editorPart, GraphicalViewer viewer) {
    if (editorPart == null || viewer == null) {
      return false;
    }
    
    String saveFilePath = getSaveFilePath(editorPart, viewer, -1);
    if (saveFilePath == null) {
      return false;
    }
    
    int format = SWT.IMAGE_JPEG;
    if (saveFilePath.endsWith(".jpeg")) {
      format = SWT.IMAGE_JPEG;
    }
    else if (saveFilePath.endsWith(".bmp")) {
      format = SWT.IMAGE_BMP;
    }
    else if (saveFilePath.endsWith(".ico")) {
      format = SWT.IMAGE_ICO;
    }
    else if (saveFilePath.endsWith(".png")) {
      format = SWT.IMAGE_PNG;
    }
    else if (saveFilePath.endsWith(".gif")) {
      format = SWT.IMAGE_GIF;
    }
    
    return save(editorPart, viewer, saveFilePath, format);
  }
  
  /**
   * <p>
   * Save editor content as image.
   * </p>
   * <p>
   * Query the {@link IEditorPart} for its {@link GraphicalViewer} via the
   * {@link IEditorPart#getAdapter(Class)} method. <br>
   * Ask the user for the file path and format.
   * </p>
   * 
   * @param editorPart The {@link IEditorPart}
   * @return <tt>True</tt> if save was successful, otherwise <tt>false</tt>.
   */
  public static boolean save(IEditorPart editorPart) {
    GraphicalViewer viewer = (GraphicalViewer) editorPart.getAdapter(GraphicalViewer.class);
    if (viewer != null) {
      return save(editorPart, viewer);
    }
    else {
      Log.error("0xA1111 Could not save as image! GraphicalViewer was not found!");
      return false;
    }
  }
  
  /**
   * Ask user for file path.
   * 
   * @param editorPart The {@link IEditorPart}
   * @param viewer The {@link GraphicalViewer}
   * @param format The format to use. Supported formats:
   *          <ul>
   *          <li>{@link SWT#IMAGE_BMP}</li>
   *          <li>{@link SWT#IMAGE_ICO}</li>
   *          <li>{@link SWT#IMAGE_JPEG}</li>
   *          <li>{@link SWT#IMAGE_PNG}</li>
   *          <li>{@link SWT#IMAGE_GIF}</li>
   *          </ul>
   * @return The file path the user chose.
   */
  private static String getSaveFilePath(IEditorPart editorPart, GraphicalViewer viewer, int format) {
    FileDialog fileDialog = new FileDialog(editorPart.getEditorSite().getShell(), SWT.SAVE);
    
    String[] filterExtensions = new String[] { "*.jpeg", "*.bmp", "*.ico", "*.png", "*.gif" };
    if (format == SWT.IMAGE_BMP) {
      filterExtensions = new String[] { "*.bmp" };
    }
    else if (format == SWT.IMAGE_JPEG) {
      filterExtensions = new String[] { "*.jpeg" };
    }
    else if (format == SWT.IMAGE_ICO) {
      filterExtensions = new String[] { "*.ico" };
    }
    else if (format == SWT.IMAGE_PNG) {
      filterExtensions = new String[] { "*.png" };
    }
    else if (format == SWT.IMAGE_GIF) {
      filterExtensions = new String[] { "*.gif" };
    }
    
    fileDialog.setFilterExtensions(filterExtensions);
    
    return fileDialog.open();
  }
  
  private static void saveEditorContentsAsImage(IEditorPart editorPart, GraphicalViewer viewer, String saveFilePath, int format) {
    /*
     * 1. First get the figure whose visuals we want to save as image. So we
     * would like to save the rooteditpart which actually hosts all the
     * printable layers. NOTE: ScalableRootEditPart manages layers and is
     * registered graphicalviewer's editpartregistry with the key
     * LayerManager.ID ... well that is because ScalableRootEditPart manages all
     * layers that are hosted on a FigureCanvas. Many layers exist for doing
     * different things
     */
    ScalableRootEditPart rootEditPart = (ScalableRootEditPart) viewer.getEditPartRegistry().get(LayerManager.ID);
    IFigure rootFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.PRINTABLE_LAYERS);// rootEditPart.getFigure();
    Rectangle rootFigureBounds = rootFigure.getBounds();
    
    /*
     * 2. Now we want to get the GC associated with the control on which all
     * figures are painted by SWTGraphics. For that first get the SWT Control
     * associated with the viewer on which the rooteditpart is set as contents
     */
    Control figureCanvas = viewer.getControl();
    GC figureCanvasGC = new GC(figureCanvas);
    
    /*
     * 3. Create a new Graphics for an Image onto which we want to paint
     * rootFigure
     */
    Image img = new Image(null, rootFigureBounds.width, rootFigureBounds.height);
    GC imageGC = new GC(img);
    imageGC.setBackground(figureCanvasGC.getBackground());
    imageGC.setForeground(figureCanvasGC.getForeground());
    imageGC.setFont(figureCanvasGC.getFont());
    imageGC.setLineStyle(figureCanvasGC.getLineStyle());
    imageGC.setLineWidth(figureCanvasGC.getLineWidth());
    // imageGC.setXORMode(figureCanvasGC.getXORMode());
    Graphics imgGraphics = new SWTGraphics(imageGC);
    
    /* 4. Draw rootFigure onto image. After that image will be ready for save */
    rootFigure.paint(imgGraphics);
    
    /* 5. Save image */
    ImageData[] imgData = new ImageData[1];
    imgData[0] = img.getImageData();
    
    ImageLoader imgLoader = new ImageLoader();
    imgLoader.data = imgData;
    imgLoader.save(saveFilePath, format);
    
    /* release OS resources */
    figureCanvasGC.dispose();
    imageGC.dispose();
    img.dispose();
  }
}
