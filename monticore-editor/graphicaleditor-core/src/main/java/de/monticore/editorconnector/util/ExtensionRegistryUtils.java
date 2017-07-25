/*******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
package de.monticore.editorconnector.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.part.FileEditorInput;

import de.monticore.genericgraphics.GenericGraphicsViewer;
import de.se_rwth.langeditor.texteditor.TextEditorImpl;

/**
 * Provides methods for accessing information from extensions that
 * other plugins added to this plugin's extension points.
 * 
 * @author Philipp Kehrbusch
 *
 */
public class ExtensionRegistryUtils {
  
  /**
   * Looks up and instantiates a {@code GenericGraphicsViewer} that was
   * registered for the specified file's extension. 
   * @param file  Input file for the {@code GenericGraphicsViewer}
   * @return
   * @throws CoreException 
   */
  public static GenericGraphicsViewer getViewer(IFile file) throws CoreException {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint extensionPoint = registry.getExtensionPoint("de.monticore.GraphicsViewer");
    String fileExt = file.getFileExtension();
    
    for(IExtension extension : extensionPoint.getExtensions()) {
      // find element "viewer"
      for(IConfigurationElement element : extension.getConfigurationElements()) {
        
        if(element.getName().equals("viewer")) {
          // search for fileExt in comma seperated list
          String attr = element.getAttribute("extensions").replace(" ", "");  // remove whitespaces 
          String attrExtensions[] = attr.split(",");                          // split by ","
          boolean containsExt = false;
          
          for(String ext : attrExtensions) {
            if(ext.equalsIgnoreCase(fileExt)) {
              containsExt = true;
              break;
            }
          }
          
          // instantiate viewer
          if(containsExt) {
//            JOptionPane.showMessageDialog(null, "ExtensionRegistryUtils: creating viewer...");
            return (GenericGraphicsViewer) element.createExecutableExtension("class");
          }
        }
      }
    }
    
    return null;
  }
  
  public static boolean hasGraphicalViewer(TextEditorImpl editor) {
    IFile file = ((FileEditorInput)editor.getEditorInput()).getFile();
    return hasGraphicalViewer(file);
  }
  
  public static boolean hasGraphicalViewer(IFile file) {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint extensionPoint = registry.getExtensionPoint("de.monticore.GraphicsViewer");
    String fileExt = file.getFileExtension();
    
    for(IExtension extension : extensionPoint.getExtensions()) {
      // find element "viewer"
      for(IConfigurationElement element : extension.getConfigurationElements()) {
        
        if(element.getName().equals("viewer")) {
          // search for fileExt in comma seperated list
          String attr = element.getAttribute("extensions").replace(" ", "");  // remove whitespaces 
          String attrExtensions[] = attr.split(",");                          // split by ","
          boolean containsExt = false;
          
          for(String ext : attrExtensions) {
            if(ext.equalsIgnoreCase(fileExt)) {
              containsExt = true;
              break;
            }
          }
          
          // instantiate viewer
          if(containsExt)
            return true;
        }
      }
    }
    
    return false;
  }
}
