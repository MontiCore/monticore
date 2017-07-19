<#--
***************************************************************************************
Copyright (c) 2016, MontiCore
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
***************************************************************************************
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
  
