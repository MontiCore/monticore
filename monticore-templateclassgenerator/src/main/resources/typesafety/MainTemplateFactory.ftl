<#-- (c) https://github.com/MontiCore/monticore -->
${tc.params("String mainTemplateName", "String package")}

package ${package};

public class ${mainTemplateName}Factory {

  /** Factory singleton instance */
  protected static ${mainTemplateName}Factory instance;
  
  /**Protected default constructor */
  protected ${mainTemplateName}Factory() {}
  
  /**
  * Registers a concrete factory instance that is used to produce
  * instances of ${mainTemplateName}.
  *
  * @param factory the factory instance that is to be used
  */
  public static void register(${mainTemplateName}Factory factory) {
    if(instance == null || instance.getClass().equals(${mainTemplateName}Factory.class)) {
      instance = factory;
    }
    else {
      throw new RuntimeException("More then one concrete factory registered for ${mainTemplateName}Factory. Current factory class is: " +
                  instance.getClass().getName() + ", factory class to register: " + factory.getClass().getName());
    }
  }
  
  /**
  * Resets the ${mainTemplateName}Factory to use its default factory.
  */
  public static void reset() {
    instance = new ${mainTemplateName}Factory();
  }
  
  /**
  *
  * @return a new ${mainTemplateName} instance.
  */
  public static ${mainTemplateName} create() {
    if(instance == null) {
      instance = new ${mainTemplateName}Factory();
    }
    return instance.doCreate();
  }
  
  /**
  * ${mainTemplateName} is a main template and implements the 
  * @see{@link de.monticore.templateclassgenerator.util.GeneratorInterface}.
  * The generator developer has to provide a class named 
  * ${mainTemplateName}Impl where the methods of GeneratorInterface 
  * are implemented in.
  *  
  * @return a new ${mainTemplateName} instance.
  */  
  protected ${mainTemplateName} doCreate() {
    // class has to be written by hand and has to be located in
    // ${package}.
    return new ${mainTemplateName}Impl();
  }
  
}
  
