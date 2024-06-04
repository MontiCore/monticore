/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen;

import de.monticore.cd.codegen.CD2JavaTemplates;

public class CD2JavaTemplatesFix {
  // Until the JavaDoc template reaches the next version, we re-use the annotations template (which is unused for methods)
  public static final String JAVADOC = CD2JavaTemplates.ANNOTATIONS;
}
