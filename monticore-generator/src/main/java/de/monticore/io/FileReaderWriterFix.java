/* (c) https://github.com/MontiCore/monticore */
package de.monticore.io;

import de.se_rwth.commons.io.SharedCloseable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarFile;


// Remove after 7.6.0-SNAPSHOT
public class FileReaderWriterFix {

  static {
    // delegate the original Set<JarFile> to Set<SharedCloseable<JarFile>>
    Set<JarFile> before = FileReaderWriter.openedJarFiles;
    // We utilize SharedCloseables, as JarFiles are not isolated against each other
    // Instead, the SharedCloseable ensures that the backing JarFile is
    // closed when all uses have finished
    FileReaderWriter.openedJarFiles = new Set<>() {

      @Override
      public int size() {
        return openedJarFiles.size();
      }

      @Override
      public boolean isEmpty() {
        return openedJarFiles.isEmpty();
      }

      @Override
      public boolean contains(Object o) {
        throw new IllegalStateException();
      }

      @Override
      public Iterator<JarFile> iterator() {
        return openedJarFiles.stream().map(x->x.get()).iterator();
      }

      @Override
      public Object[] toArray() {
        throw new IllegalStateException();
      }

      @Override
      public <T> T[] toArray(T[] a) {
        throw new IllegalStateException();
      }

      @Override
      public boolean add(JarFile jarFile) {
        return openedJarFiles.add(new SharedCloseable<>(jarFile));
      }

      @Override
      public boolean remove(Object o) {
        throw new IllegalStateException();
      }

      @Override
      public boolean containsAll(Collection<?> c) {
        throw new IllegalStateException();
      }

      @Override
      public boolean addAll(Collection<? extends JarFile> c) {
        throw new IllegalStateException();
      }

      @Override
      public boolean retainAll(Collection<?> c) {
        throw new IllegalStateException();
      }

      @Override
      public boolean removeAll(Collection<?> c) {
        throw new IllegalStateException();
      }

      @Override
      public void clear() {
        openedJarFiles.clear();
      }
    };
    // In case we wrap later
    new HashSet<>(before).forEach(FileReaderWriter.openedJarFiles::add);
  }

  protected static Set<SharedCloseable<JarFile>> openedJarFiles = new HashSet<>();
  public static void closeOpenedJarFiles() {
      openedJarFiles.forEach(SharedCloseable::close);
      openedJarFiles.clear();
  }

  public static void init() {
    // empty
  }
}
