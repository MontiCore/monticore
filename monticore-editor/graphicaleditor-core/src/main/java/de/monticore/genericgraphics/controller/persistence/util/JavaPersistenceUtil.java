/*******************************************************************************
 * MontiCore Language Workbench
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
package de.monticore.genericgraphics.controller.persistence.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;

import de.monticore.genericgraphics.model.graphics.IViewElement;


/**
 * @author Tim Enger
 */
public class JavaPersistenceUtil implements IPersistenceUtil {
  
  @Override
  public boolean exportViewElements(final List<IViewElement> ves, final IFile file, final IProgressMonitor progressMonitor) {
    SafeRunner.run(new SafeRunnable() {
      @Override
      public void run() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeToOutputStream(out, ves);
        if (file.exists()) {
          file.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, progressMonitor);
        }
        else {
          file.create(new ByteArrayInputStream(out.toByteArray()), true, progressMonitor);
        }
      }
    });
    return false;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public List<IViewElement> importViewElements(IFile file) {
    Object obj = loadFromInputStream(file);
    return (List<IViewElement>) obj;
  }
  
  private void writeToOutputStream(OutputStream os, Object obj) throws IOException {
    ObjectOutputStream out = new ObjectOutputStream(os);
    out.writeObject(obj);
    out.close();
  }
  
  private Object loadFromInputStream(IFile file) {
    Object load = null;
    InputStream is;
    try {
      is = file.getContents(false);
      ObjectInputStream ois = new ObjectInputStream(is);
      load = ois.readObject();
      ois.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    return load;
  }
  
}
