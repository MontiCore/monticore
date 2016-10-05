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
${tc.signature("antlrGenerator")}
<#assign genHelper = glex.getGlobalVar("parserHelper")>
<#assign symbolTable = genHelper.getGrammarSymbol()>
// Generated antlr file

// Parser header
${tc.include("parser.ParserHeader")}
{
// Global actions
<#list antlrGenerator.getHWParserJavaCode() as javaCode>
  ${javaCode}
</#list>

// Convert functions
<#list genHelper.getIdentsToGenerate() as ident>
  ${ident.getConvertFunction()}
</#list>  
}

// Lexer header
${tc.includeArgs("parser.LexerMember", [antlrGenerator])}

// Global actions

// Parser rules 
<#list genHelper.getParserRulesToGenerate() as parserProd>
  <#list antlrGenerator.createAntlrCode(parserProd) as parserRule>
  ${parserRule}
  </#list>
</#list>

// Extra Rules for Interfaces
<#list genHelper.getInterfaceRulesToGenerate() as interfaceProd>
  <#list antlrGenerator.createAntlrCodeForInterface(interfaceProd) as interfaceRule>
  ${interfaceRule}
  </#list>
</#list>
 
// Lexer symbols
<#list symbolTable.getLexSymbolsWithInherited() as lexSymbol>
  ${symbolTable.getLexSymbolName(lexSymbol)} : '${lexSymbol}';
</#list>
 
// Lexer rules 
<#list genHelper.getLexerRulesToGenerate() as lexProd>
  <#list antlrGenerator.createAntlrCode(lexProd) as lexerRule>
  ${lexerRule}
  </#list>
</#list>

