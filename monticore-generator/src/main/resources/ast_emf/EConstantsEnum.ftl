<#--
***************************************************************************************
Copyright (c) 2015, MontiCore
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
${tc.signature("ast", "constants", "astClasses")}
<#assign genHelper = glex.getGlobalValue("astHelper")>
  
<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getAstPackage()};

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;

public enum ${ast.getName()} implements Enumerator {
  <#list constants as constant>
  // Literal Object ${constant.getName()}
  ${constant.getName()}(${constant_index},"${constant.getName()}","${constant.getName()}") <#if constant_has_next>,<#else>;</#if>
  </#list>
  <#list constants as constant>
  public static final int ${constant.getName()}_VALUE = ${constant_index};
  </#list>
  
  private static final ${ast.getName()}[] VALUES_ARRAY =
    new ${ast.getName()}[] {
  <#list constants as const>
    <#list const.getConstants() as constant>
        ${constant.getName()},
    </#list>
  </#list>
  };
    
  public static final List<${ast.getName()}> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));
 
  // Returns the literal with the specified literal value.
  public static ${ast.getName()} get(String literal) {
    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
      ${ast.getName()} result = VALUES_ARRAY[i];
      if (result.toString().equals(literal)) {
        return result;
      }
    }
    return null;
  }

  // Returns the literal with the specified name.
  public static ${ast.getName()} getByName(String name) {
    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
      ${ast.getName()} result = VALUES_ARRAY[i];
      if (result.getName().equals(name)) {
        return result;
      }
    }
    return null;
  }
    
  // Returns the literal with the specified integer value.
  public static ${ast.getName()} get(int value) {
    switch (value) {
  <#list ast.getConstants() as const>
    <#list const.getConstants() as constant>
      case ${constant.getName()}_VALUE: return ${constant.getName()};  
    </#list>
  </#list>
    }
    return null;
  }

  private final int value;
    
  private final String name;
   
  private final String literal;
    
    // Only this class can construct instances.
  private ${ast.getName()}(int value, String name, String literal) {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }
    
  public int getValue() {
    return value;
  }
    
  public String getName() {
    return name;
  }
    
  public String getLiteral() {
    return literal;
  }
    
  @Override
  public String toString() {
    return literal;
  }

