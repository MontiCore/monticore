/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.EditPart;

import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.controller.persistence.util.IPersistenceUtil;
import de.monticore.genericgraphics.model.graphics.IViewElement;
import de.monticore.genericgraphics.view.layout.ILayoutAlgorithm;
import de.se_rwth.commons.logging.Log;


/**
 * This class provides a default implementation of the {@link IGraphicsLoader}
 * interface. This class makes use of the following classes/tools:
 * <ul>
 * <li>{@link IPersistenceUtil}: to import and export {@link IViewElement
 * IViewElements}</li>
 * <li>{@link DSLTool}: to parse an domain model file</li>
 * </ul>
 * <b>Note</b>: Per default the
 * {@link DefaultGraphicsLoader#getDefaultParseArguments(IFile)} are passed to
 * the {@link DSLTool}. If you want to use your own arguments, just ignore the
 * passed ones and set your own.
 * 
 * @author Tim Enger
 */
public class DefaultGraphicsLoader implements IGraphicsLoader {
  
  private static final String EXTENSION = "mcvd";
  
  /* utils */
  private IPersistenceUtil util;
  
  /* data */
  private IFile mFile;
  private IFile vFile;
  private List<IViewElement> loadedVes;
  
  /**
   * Constructor
   * 
   * @param util The {@link IPersistenceUtil} to use for view data files
   * @param dslTool The {@link DSLTool} to use for parsing domain model files
   * @param mFile The {@link IFile} of the <b>model</b> data, must not be
   *          <code>null</code>
   * @param vFile The {@link IFile} of the <b>view</b> data, must not be
   *          <code>null</code>
   */
  public DefaultGraphicsLoader(IPersistenceUtil util, IFile mFile, IFile vFile) {
    this.util = util;
    this.mFile = mFile;
    this.vFile = vFile;
  }
  
  /**
   * <p>
   * Constructor
   * </p>
   * <p>
   * This constructor tries to find the {@link IFile} for the view data. It
   * takes the location and the name of the model {@link IFile}
   * <code>mFile</code> and tries to load a view file in the same location with
   * the same name, but different file extension.<br>
   * If a view vile cannot be found, a new file is created.
   * </p>
   * 
   * @param util The {@link IPersistenceUtil} to use for view data files, must
   *          not be <code>null</code>
   * @param dslTool The {@link DSLTool} to use for parsing domain model files
   * @param mFile The {@link IFile} of the <b>model</b> data, must not be
   *          <code>null</code>
   */
  public DefaultGraphicsLoader(IPersistenceUtil util, IFile mFile) {
    this.util = util;
    this.mFile = mFile;
    
    initVFile();
  }
  
  private void initVFile() {
    IPath loca = (IPath) mFile.getLocation().clone();
    loca = loca.removeFileExtension().addFileExtension(EXTENSION);
    vFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(loca);
  }
  
  @Override
  public void saveViewData(List<EditPart> editparts, IProgressMonitor monitor) {
    List<IViewElement> ves = new ArrayList<IViewElement>();
    
    for (EditPart ep : editparts) {
      if (ep instanceof IMCViewElementEditPart) {
        ves.add(((IMCViewElementEditPart) ep).getViewElement());
      }
    }
    util.exportViewElements(ves, vFile, monitor);
  }
  
  @Override
  public List<IViewElement> loadViewData() {
    if (getViewFile() == null) {
      Log.error("0xA1107 AbstractPersistanceHandler: View File is null, cannot load view data");
      return Collections.emptyList();
    }
    if (vFile.exists()) {
      loadedVes = util.importViewElements(vFile);
    }
    else {
      Log.error("0xA1108 view data file does not exist and thus cannot be loaded!");
    }
    return loadedVes;
  }

  @Override
  public boolean combineModelViewData(List<EditPart> editparts, ILayoutAlgorithm layout) {
    boolean doLayout = true;
    
    // not using the layout algorithm at the moment
    // build a map: identifier -> EditPart
    Map<String, IMCViewElementEditPart> epMap = new HashMap<String, IMCViewElementEditPart>();
    for (EditPart ep : editparts) {
      if (ep instanceof IMCViewElementEditPart) {
        IMCViewElementEditPart vep = (IMCViewElementEditPart) ep;
        epMap.put(vep.getIdentifier(), vep);
      }
      
    }
    
    // assign view elements to editparts
    // and store assigned editparts
    List<IMCViewElementEditPart> assignedEP = new ArrayList<IMCViewElementEditPart>();
    if (getLoadedViewData() != null) {
      for (IViewElement ve : getLoadedViewData()) {
        IMCViewElementEditPart ep = epMap.get(ve.getIdentifier());
        if (ep instanceof IMCViewElementEditPart) {
          ep.setViewElement(ve);
          doLayout = false;
          ep.refresh();
          assignedEP.add(ep);
        }
      }
    }
    
    // for the not assigned editparts we need to create a IViewElement
    // with some default values
    List<EditPart> notAssignedEP = new ArrayList<EditPart>(editparts);
    notAssignedEP.removeAll(assignedEP);
    
    // save view elements for possible layout
    List<IMCViewElementEditPart> eps = new ArrayList<IMCViewElementEditPart>();
    for (EditPart ep : notAssignedEP) {
      if (ep instanceof IMCViewElementEditPart) {
        eps.add((IMCViewElementEditPart) ep);
      }
    }
    
    if (layout != null && doLayout && !eps.isEmpty()) {
      layout.layout(eps);
      return true;
    }
    
    return false;
  }
  
  @Override
  public List<IViewElement> getLoadedViewData() {
    return loadedVes;
  }
  
  @Override
  public void setModelFile(IFile mFile) {
    this.mFile = mFile;
  }
  
  @Override
  public IFile getModelFile() {
    return mFile;
  }
  
  @Override
  public void setViewFile(IFile vFile) {
    this.vFile = vFile;
  }
  
  @Override
  public void setViewFileAccordingToModelFile() {
    initVFile();
  }
  
  @Override
  public IFile getViewFile() {
    return vFile;
  }
  
  /**
   * @param file The {@link IFile} whose absolute folder should be returned.
   * @return The absolute path of the editor file.
   */
  protected static String getAbsoluteFilePath(IFile file) {
    return file.getRawLocation().toOSString();
  }
  
  /**
   * @param file The {@link IFile} whose project folder should be returned.
   * @return The project folder.
   */
  protected static String getProjectFolder(IFile file) {
    return file.getProject().getLocation().toString();
  }
    
  
  @Override
  public IPersistenceUtil getPersistenceUtil() {
    return util;
  }
  
  @Override
  public void setPersistenceUtil(IPersistenceUtil util) {
    this.util = util;
  }
}
