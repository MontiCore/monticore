/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.global;

import org.eclipse.core.runtime.Path;

public final class Constants {
  
  private Constants() {
    // non-instantiable
  }
  
  public static final String LANGUAGE_EXTENSION_POINT = "texteditor-core.language";
  
  public static final String LANGUAGE_EXTENSION_POINT_IMPLEMENTATION = "implementation";
  
  public static final String EDITOR_ID = "de.se_rwth.langeditor";
  
  public static final String MODELPATH_ENTRY = "texteditor-core.modelpathentry";
  
  public static final Path MODELPATH = new Path("texteditor-core.modelpath");
}
