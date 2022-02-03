/* (c) https://github.com/MontiCore/monticore */
package de.monticore.io;

import sun.misc.CompoundEnumeration;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

public class MontiCoreClassLoader extends URLClassLoader {

  private final ClassLoader parent;

  public MontiCoreClassLoader(URL[] urls) {
    super(urls);
    this.parent = null;
  }

  public MontiCoreClassLoader(URL[] urls, ClassLoader parent){
    super(urls, parent);
    this.parent = parent;
  }

  @Override
  public Enumeration<URL> getResources(String name) throws IOException {
    Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration<?>[2];
    if(parent != null){
      tmp[0] = parent.getResources(name);
    }
    tmp[1] = findResources(name);

    return new CompoundEnumeration<>(tmp);
  }


}
