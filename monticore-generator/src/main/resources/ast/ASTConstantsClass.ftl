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
${tc.signature("ast", "qualifiedCdName", "superGrammars")}

<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign nameHelper = glex.getGlobalVar("nameHelper")>

package ${genHelper.getAstPackage()};

import java.util.Collection;
import com.google.common.collect.Sets;

public class ${ast.getName()} extends Object {

  public static final String LANGUAGE = "${qualifiedCdName}";
  
  public static final int DEFAULT = 0;
<#list ast.getCDAttributeList() as constant>
  public static final int ${constant.getName()} = ${constant_index + 1};
</#list>

  <#assign del = " ">
  /** All direct super grammars.*/
  public static String[] superGrammars = {<#list superGrammars as superGrammar>${del}"${superGrammar}"<#assign del = ","></#list>};
  
  public static Collection<String> getAllLanguages() {
    Collection<String> allLanguages = Sets.newLinkedHashSet();
    <#list superGrammars as superGrammar>
    <#assign superPackage = astHelper.getAstPackage(superGrammar)>
    allLanguages.addAll(${superPackage}ASTConstants${nameHelper.getSimpleName(superGrammar)}.getAllLanguages());
    </#list>
    allLanguages.add(LANGUAGE);
    return allLanguages;
  }

  /** Constructs a new ASTConstantsFile.*/
  public ${ast.getName()}() {
  }

}
