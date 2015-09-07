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
${tc.signature("ast", "qualifiedCdName", "superGrammars")}

<#assign genHelper = glex.getGlobalValue("astHelper")>
<#assign nameHelper = glex.getGlobalValue("nameHelper")>

package ${genHelper.getAstPackage()};

import java.util.Collection;
import com.google.common.collect.Sets;

public class ${ast.getName()} extends Object {

  public static final String LANGUAGE = "${qualifiedCdName}";
  
  public static final int DEFAULT = 0;
<#list ast.getCDAttributes() as constant>
  public static final int ${constant.getName()} = ${constant_index + 1};
</#list>

  <#assign del = " ">
  /** All direct super grammars.*/
  public static String[] superGrammars = {<#list superGrammars as superGrammar>${del}"${superGrammar}"<#assign del = ","></#list>};
  
  public static Collection<String> getAllLanguages() {
    Collection<String> allLanguages = Sets.newHashSet();
    <#list superGrammars as superGrammar>
    <#assign superPackage = astHelper.getAstPackage(superGrammar)>
    allLanguages.addAll(${superPackage}.ASTConstants${nameHelper.getSimpleName(superGrammar)}.getAllLanguages());
    </#list>
    allLanguages.add(LANGUAGE);
    return allLanguages;
  }

  /** Constructs a new ASTConstantsFile.*/
  public ${ast.getName()}() {
  }

}
