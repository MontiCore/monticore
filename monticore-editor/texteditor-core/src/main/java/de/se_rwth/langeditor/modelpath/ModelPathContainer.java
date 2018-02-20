/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.modelpath;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import de.se_rwth.langeditor.global.Constants;

public class ModelPathContainer implements IClasspathContainer {
  
  private final IJavaProject javaProject;
  
  ModelPathContainer(IJavaProject javaProject) {
    this.javaProject = javaProject;
  }
  
  @Override
  public IClasspathEntry[] getClasspathEntries() {
    try {
      IClasspathAttribute[] attributeEntries = Arrays.stream(javaProject.getRawClasspath())
          .filter(classpathEntry -> classpathEntry.getPath().equals(Constants.MODELPATH))
          .findFirst()
          .map(IClasspathEntry::getExtraAttributes)
          .orElse(new IClasspathAttribute[] {});
      List<IClasspathEntry> libraryEntries = Arrays.stream(attributeEntries)
          .map(IClasspathAttribute::getValue)
          .map(value -> JavaCore.newLibraryEntry(new Path(value).makeAbsolute(), null, null))
          .collect(Collectors.toList());
      return libraryEntries.toArray(new IClasspathEntry[libraryEntries.size()]);
    }
    catch (JavaModelException e) {
      return new IClasspathEntry[] {};
    }
  }
  
  @Override
  public String getDescription() {
    return "Modelpath";
  }
  
  @Override
  public int getKind() {
    return K_APPLICATION;
  }
  
  @Override
  public IPath getPath() {
    return Constants.MODELPATH.append(javaProject.getProject().getName());
  }
}
