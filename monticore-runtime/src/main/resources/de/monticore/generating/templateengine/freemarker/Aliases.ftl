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

<#-- Defines freemarker functions as aliases of Java methods.
     These functions are injected in all templates.
-->


<#function include templ>
	<#return tc.include(templ)>
</#function>


<#function includeArgs templ param...>
	<#return tc.includeArgs(templ, param)>
</#function>


<#function signature param...>
	<#return tc.signature(param)>
</#function>


<#-- Aliases for logging methods -->

<#function trace msg logger>
	<#return log.trace(msg, logger)>
</#function>

<#function debug msg logger>
	<#return log.debug(msg, logger)>
</#function>

<#function info msg logger>
	<#return log.info(msg, logger)>
</#function>

<#function warn msg>
	<#return log.warn(msg)>
</#function>

<#function error msg>
	<#return log.error(msg)>
</#function>

<#-- Aliases for global vars methods -->

<#function defineGlobalVar name value>
	<#return glex.defineGlobalVar(name, value)>
</#function>

<#function defineGlobalVar name>
	<#return glex.defineGlobalVar(name)>
</#function>

<#function defineGlobalVars name...>
	<#return glex.defineGlobalVars(name)>
</#function>

<#function changeGlobalVar name value>
	<#return glex.changeGlobalVar(name, value)>
</#function>

<#function addToGlobalVar name value>
	<#return glex.addToGlobalVar(name, value)>
</#function>

<#function getGlobalVar name>
	<#return glex.getGlobalVar(name)>
</#function>

<#function requiredGlobalVar name>
	<#return glex.requiredGlobalVar(name)>
</#function>

<#function requiredGlobalVars name...>
	<#return glex.requiredGlobalVars(name)>
</#function>

<#-- Aliases for hook points methods -->
<#function bindHookPoint name hp>
	<#return glex.bindHookPoint(name, hp)>
</#function>

<#function defineHookPoint name ast>
	<#return glex.defineHookPoint(tc, name, ast)>
</#function>

<#function defineHookPoint name>
	<#return glex.defineHookPoint(tc, name)>
</#function>

<#function existsHookPoint name>
	<#return glex.existsHookPoint(name)>
</#function>
