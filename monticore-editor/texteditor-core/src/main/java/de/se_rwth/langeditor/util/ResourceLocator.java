/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.util;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.se_rwth.langeditor.global.Constants;

public final class ResourceLocator {
  
  private ResourceLocator() {
    // non instantiable
  }
  
  public static ImmutableMap<IStorage, IProject> getStorages() {
    return ImmutableMap.<IStorage, IProject> builder()
        .putAll(getModelsOnModelPath())
        .build();
  }
  
  public static ImmutableList<Path> assembleModelPath(IProject project) {
    ImmutableList.Builder<Path> builder = ImmutableList.builder();
    for (IClasspathEntry classpathEntry : getModelPathEntries(JavaCore.create(project))) {
      builder.add(classpathEntry.getPath().toFile().toPath());
    }
    return builder.build();
  }
  
  public static Optional<IClasspathEntry> getModelPathClasspathEntry(IJavaProject javaProject) {
    try {
      return Arrays.stream(javaProject.getRawClasspath())
          .filter(classpathEntry -> classpathEntry.getEntryKind() == IClasspathEntry.CPE_CONTAINER)
          .filter(classpathEntry -> classpathEntry.getPath().equals(Constants.MODELPATH))
          .findFirst();
    }
    catch (JavaModelException e) {
      return Optional.empty();
    }
  }
  
  private static Map<IStorage, IProject> getModelsOnModelPath() {
    Map<IStorage, IProject> models = new HashMap<>();
    for (Entry<IPackageFragmentRoot, IProject> entry : getPackageFragmentRootsOnModelPath()
        .entrySet()) {
      for (IStorage storage : getStorages(entry.getKey())) {
        models.put(storage, entry.getValue());
      }
    }
    return models;
  }
  
  private static Set<IStorage> getStorages(Object resource) {
    Set<IStorage> storages = new HashSet<>();
    try {
      if (resource instanceof IParent) {
        IJavaElement[] children = ((IParent) resource).getChildren();
        for (Object child : children) {
          storages.addAll(getStorages(child));
        }
      }
      if (resource instanceof IStorage) {
        storages.add((IStorage) resource);
      }
      if (resource instanceof IPackageFragment) {
        for (Object child : ((IPackageFragment) resource).getNonJavaResources()) {
          storages.addAll(getStorages(child));
        }
      }
      if (resource instanceof IPackageFragmentRoot) {
        for (Object child : ((IPackageFragmentRoot) resource).getNonJavaResources()) {
          storages.addAll(getStorages(child));
        }
      }
    }
    catch (JavaModelException e) {
      throw new RuntimeException(e);
    }
    return storages;
  }
  
  private static Map<IPackageFragmentRoot, IProject> getPackageFragmentRootsOnModelPath() {
    Map<IPackageFragmentRoot, IProject> packageFragmentRoots = new HashMap<>();
    for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
      IJavaProject javaProject = JavaCore.create(project);
      try {
        for (IClasspathEntry entry : getModelPathEntries(javaProject)) {
          Optional.ofNullable(javaProject.findPackageFragmentRoot(entry.getPath()))
              .ifPresent(
                  packageFragmentRoot -> packageFragmentRoots.put(packageFragmentRoot, project));
        }
      }
      catch (JavaModelException e) {
        continue;
      }
    }
    return packageFragmentRoots;
  }
  
  private static IClasspathEntry[] getModelPathEntries(IJavaProject javaProject) {
    try {
      return Optional.ofNullable(JavaCore.getClasspathContainer(Constants.MODELPATH, javaProject))
          .map(IClasspathContainer::getClasspathEntries)
          .orElse(new IClasspathEntry[] {});
    }
    catch (JavaModelException e) {
      return new IClasspathEntry[] {};
    }
  }
}
