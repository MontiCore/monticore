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
${tc.signature("astType", "qualifiedCDName", "visitorTypeFQN", "superVisitorTypeFQN")}
<#assign genHelper = glex.getGlobalValue("astHelper")>
<#assign plainName = genHelper.getPlainName(astType)>
    <#-- when overriding nonterminals we might have a visitor calling this
         method even if it is a sublanguage visitor, see #1708 -->
    if (visitor instanceof ${visitorTypeFQN}) {
      accept((${visitorTypeFQN}) visitor);      
    } else {
      de.se_rwth.commons.logging.Log.error("0xA7000${genHelper.getGeneratedErrorCode(ast)}AST node type ${plainName} of the sub language ${qualifiedCDName} expected a visitor of type ${visitorTypeFQN}, but got ${superVisitorTypeFQN}. Visitors of a super language may not be used on ASTs containing nodes of the sub language. Use a visitor of the sub language.");
    }
