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

class ${ast.getName()}(object):
    LANGUAGE = '${qualifiedCdName}'

    DEFAULT = 0
    <#list ast.getCDAttributes() as constant>
    ${constant.getName()} = ${constant_index + 1}
    </#list>

    <#assign del = " ">
    # All direct super grammars.
    superGrammars = (<#list superGrammars as superGrammar>${del}"${superGrammar}"<#assign del = ","></#list>)

    @classmethod
    def getAllLanguages(cls):
        allLanguages = list()
        <#list superGrammars as superGrammar>
        <#assign superPackage = astHelper.getAstPackage(superGrammar)>
        allLanguages.extend(${superPackage}ASTConstants${nameHelper.getSimpleName(superGrammar)}.getAllLanguages())
        </#list>
        allLanguages.append(cls.LANGUAGE)
        return allLanguages

    def __init__(self):
        """
        Constructs a new ASTConstantsFile.
        """
        pass

