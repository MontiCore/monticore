/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.modelpath;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class ModelPathContainerInitializer extends ClasspathContainerInitializer {
  
  @Override
  public void initialize(IPath containerPath, IJavaProject javaProject) throws CoreException {
    ModelPathContainer modelPathContainer = new ModelPathContainer(javaProject);
    JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { javaProject },
        new IClasspathContainer[] { modelPathContainer }, null);
  }
}
