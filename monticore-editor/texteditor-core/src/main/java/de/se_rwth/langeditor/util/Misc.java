/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJarEntryResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.part.FileEditorInput;

@SuppressWarnings("restriction")
public final class Misc {
  
  private Misc() {
    // noninstantiable
  }
  
  public static Optional<Image> loadImage(String location) {
    try {
      URL url = new URL("platform:/plugin/texteditor-core/" + location);
      InputStream inputStream = url.openConnection().getInputStream();
      return Optional.of(new Image(Display.getCurrent(), inputStream));
    }
    catch (IOException e) {
      return Optional.empty();
    }
  }
  
  public static <T> T callGetter(Object element, String getterName, Class<T> returnType) {
    try {
      return returnType.cast(element.getClass().getMethod(getterName).invoke(element));
    }
    catch (ClassCastException
           | IllegalAccessException
           | IllegalArgumentException
           | InvocationTargetException
           | NoSuchMethodException
           | SecurityException e) {
      throw new IllegalArgumentException("Object of type " + element.getClass().getName()
          + "didn't have correctly typed, no-argument method called "
          + getterName, e);
    }
  }
  
  public static <T> Stream<T> preorder(T start,
      Function<T, Collection<? extends T>> generatingFunction) {
    List<T> elements = new ArrayList<>();
    Stack<T> stack = new Stack<>();
    stack.push(start);
    T currentElement;
    while (!stack.isEmpty()) {
      currentElement = stack.pop();
      elements.add(currentElement);
      stack.addAll(generatingFunction.apply(currentElement));
    }
    return elements.stream();
  }
  
  public static <T> void traverse(T root, Function<T, Collection<? extends T>> childGenerator,
      Consumer<? super T> enter, Consumer<? super T> exit) {
    
    Set<T> previouslyVisited = new HashSet<>();
    Stack<T> yetToVisit = new Stack<>();
    yetToVisit.push(root);
    
    T nextElement;
    while (!yetToVisit.isEmpty()) {
      nextElement = yetToVisit.peek();
      if (!previouslyVisited.contains(nextElement)) {
        enter.accept(nextElement);
        previouslyVisited.add(nextElement);
        yetToVisit.addAll(childGenerator.apply(nextElement));
      }
      else {
        exit.accept(yetToVisit.pop());
      }
    }
  }
  
  public static String getContents(IStorage storage) {
    try {
      return IOUtils.toString(storage.getContents());
    }
    catch (IOException | CoreException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static IStorageEditorInput getEditorInput(IStorage storage) {
    if (storage instanceof IFile) {
      return new FileEditorInput((IFile) storage);
    }
    else if (storage instanceof IJarEntryResource) {
      return new JarEntryEditorInput(storage);
    }
    else {
      throw new IllegalArgumentException("Unknown IStorage implementation");
    }
  }
  
  public static IStorage getStorage(IEditorInput editorInput) {
    if (editorInput instanceof IStorageEditorInput) {
      try {
        return ((IStorageEditorInput) editorInput).getStorage();
      }
      catch (CoreException e) {
        throw new RuntimeException(e);
      }
    }
    else {
      throw new IllegalArgumentException("Unknown IStorage implementation");
    }
  }
  
  public static IProject getProject(IStorage storage) {
    if (storage instanceof IFile) {
      return ((IFile) storage).getProject();
    }
    else if (storage instanceof IJarEntryResource) {
      return ((IJarEntryResource) storage).getPackageFragmentRoot().getJavaProject().getProject();
    }
    else {
      throw new IllegalArgumentException("Unknown IStorage implementation");
    }
  }
  
  public static void addToClasspath(IJavaProject javaProject, List<IClasspathEntry> newEntries) {
    try {
      IClasspathEntry[] oldClasspath = javaProject.getRawClasspath();
      IClasspathEntry[] newClasspath =
          Arrays.copyOf(oldClasspath, oldClasspath.length + newEntries.size());
      for (int i = 0; i < newEntries.size(); i++) {
        newClasspath[oldClasspath.length + i] = newEntries.get(i);
      }
      javaProject.setRawClasspath(newClasspath, null);
    }
    catch (JavaModelException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static boolean removeFromClasspath(IJavaProject javaProject,
      Predicate<IClasspathEntry> predicate) {
    try {
      IClasspathEntry[] oldClasspath = javaProject.getRawClasspath();
      List<IClasspathEntry> filteredClasspath = Arrays.stream(oldClasspath)
          .filter(predicate.negate())
          .collect(Collectors.toList());
      IClasspathEntry[] newClasspath =
          filteredClasspath.toArray(new IClasspathEntry[filteredClasspath.size()]);
      javaProject.setRawClasspath(newClasspath, null);
      return oldClasspath.length > newClasspath.length;
    }
    catch (JavaModelException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static int convertLineAndColumnToLinearIndex(String string, int line, int column) {
    String[] lines = string.split("\n");
    if (lines.length <= line) {
      return 0;
    }
    else {
      int linearIndex = 0;
      for (int i = 0; i < line - 1; i++) {
        linearIndex += lines[i].length() + 1;
      }
      linearIndex += column;
      return linearIndex;
    }
  }
}
