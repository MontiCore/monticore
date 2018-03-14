/* (c) https://github.com/MontiCore/monticore */
package de.monticore.templateclassgenerator.codegen;

import de.monticore.generating.templateengine.ExtendedTemplateController;

/**
 * Sets some constants for the Generator and the generated template classes
 *
 * @author Jerome Pfeiffer
 */
public class TemplateClassGeneratorConstants {
  
  /**
   * Alias where the generated TemplateStorage is referencable from
   */
  public static final String TEMPLATES_ALIAS = "TemplatesClass";
  
  /**
   * The package where all template classes are generated to
   */
  public static final String TEMPLATE_CLASSES_PACKAGE = "_templates";
  
  /**
   * The package where the template classes' setup is generated to
   */
  public static final String TEMPLATE_CLASSES_SETUP_PACKAGE = "_setup";
    
  /**
   * Postfix for the name of templateclasses
   */
  public static final String TEMPLATE_CLASSES_POSTFIX = "";
  
  /**
   * Signature method to define template params. IMPORTANT: You also have to
   * change the method name in {@link ExtendedTemplateController}. Otherwise you
   * will loose the dynamic type check of template arguments.
   */
  public static final String PARAM_METHOD = "tc.params";
  
  /**
   * Signature method to define template result.
   */
  public static final String RESULT_METHOD = "tc.result";
  
  
  
  /**
   * Default path where TCG looks for Templates.
   */
  public static final String DEFAULT_TEMPLATEPATH = "src/main/resources";
  
  /**
   * Default path TCG generates template classes to.
   */
  public static final String DEFAULT_OUTPUT_FOLDER = "gen";
  
}
