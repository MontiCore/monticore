/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.persistence.util;

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
