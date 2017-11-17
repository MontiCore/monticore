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
	<#return tc.trace(msg, logger)>
</#function>

<#function debug msg logger>
	<#return tc.debug(msg, logger)>
</#function>

<#function info msg logger>
	<#return tc.info(msg, logger)>
</#function>

<#function warn msg>
	<#return tc.warn(msg)>
</#function>

<#function error msg>
	<#return tc.error(msg)>
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
