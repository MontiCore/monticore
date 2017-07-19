<#--
***************************************************************************************
Copyright (c) 2017, MontiCore
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
${tc.signature("symbol", "package", "comments", "modifier", "prefix", "superComponent", "fqCompInterfaceWithTypeParameters", "helper", "timingParadigm", "existsHWC", "formalTypeParameters")}
<#assign genHelper = helper>

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

package ${package};


import de.montiarc.generator.datatypes.Port;
/**
 * ${comments}
 * <br><br>
 * Java representation of component ${symbol.getFullName()}.<br>
 * <br>
 * Generated with MontiArc ${genHelper.getMontiArcVersion()}.<br>
 * @date ${genHelper.getTimeNow()}<br>
 *
 */

<#if existsHWC>abstract</#if> public class ${symbol.getName()}<#if existsHWC>TOP</#if> ${formalTypeParameters} <#t>
{
  // members for ports + getter + setter
  <#list symbol.getPorts() as port>
    <#assign portNameCap=port.getName()?cap_first>
    protected Port<${helper.printType(port.getTypeReference())}> ${port.getName()};
    
    <#if port.isIncoming()>
    public void set${portNameCap}(Port<${helper.printType(port.getTypeReference())}> ${port.getName()}) {
      this.${port.getName()} = ${port.getName()};
    }
    
    <#else>
    public Port<${helper.printType(port.getTypeReference())}> get${portNameCap}() {
        return ${port.getName()};
    }
    </#if>   
    
  </#list>
  
  
  // members for subcomps + getter + setter
  <#list symbol.getSubComponents() as sub>
    <#assign subNameCap=sub.getName()?cap_first>
    protected ${helper.printType(sub.getComponentType())} ${sub.getName()};
    
    public ${helper.printType(sub.getComponentType())} get${subNameCap}() {
        return ${sub.getName()};
    }
    
    public void set${subNameCap}(${helper.printType(sub.getComponentType())} ${sub.getName()}) {
      this.${sub.getName()} = ${sub.getName()};
    }
  </#list>
  
    
    public void compute() {
      <#if symbol.isAtomic()>
      // TODO Top Dinge
      <#else>
        <#list symbol.getSubComponents() as sub>
            ${sub.getName()}.compute();
        </#list>
      </#if>
    }
    
    public void init() {
      <#if symbol.isAtomic()>
      // Atomic components generate nothing.
      <#else>
        <#list symbol.getConnectors() as connector>
            <#if helper.containsDot(connector.getTarget())>
              <#if !helper.containsDot(connector.getSource())>
                ${helper.getConnectorPortType(connector.getTarget())}.set${helper.getConnectorPortName(connector.getTarget())?cap_first}(${connector.getSource()});
              <#else>
                this.${helper.getConnectorPortType(connector.getTarget())}.set${helper.getConnectorPortName(connector.getTarget())?cap_first}(this.${helper.getConnectorPortType(connector.getSource())}.get${helper.getConnectorPortName(connector.getSource())?cap_first}());
              </#if>              
            </#if>
        </#list>
        <#list symbol.getSubComponents() as sub>
            ${sub.getName()}.init();
        </#list>
      </#if>
    }
    
    public void setUp(){
      <#if symbol.isAtomic()>
        <#list symbol.getPorts() as port>
          this.${port.getName()} = new Port<${helper.printType(port.getTypeReference())}>();
        </#list>
      <#else>
        <#list symbol.getSubComponents() as sub>
          this.${sub.getName()} = new ${helper.printType(sub.getComponentType())}();
        </#list>
        <#list symbol.getSubComponents() as sub>
          this.${sub.getName()}.setUp();
        </#list>
      </#if>
      <#list symbol.getConnectors() as connector>
        <#if !helper.containsDot(connector.getTarget())>
          this.${connector.getTarget()} = this.${helper.getConnectorPortType(connector.getSource())}.get${helper.getConnectorPortName(connector.getSource())?cap_first}();
        </#if>
      </#list>
    }
    
    public void update() {
      <#if symbol.isAtomic()>
        <#list symbol.getPorts() as port>
          <#if port.isOutgoing()>
            this.${port.getName()}.update();
          </#if>
        </#list>
      <#else>
        <#list symbol.getSubComponents() as sub>
          this.${sub.getName()}.update();
        </#list>
      </#if>
    }



}
