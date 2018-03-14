/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.actions;

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
