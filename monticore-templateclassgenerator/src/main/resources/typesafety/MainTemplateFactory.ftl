<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
-->
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
  
