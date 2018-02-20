<#-- (c)  https://github.com/MontiCore/monticore -->

${tc.signature("astType", "astPackage", "cd")}
<#assign genHelper = glex.getGlobalVar("odHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getPackageName()}._od;

import ${genHelper.getVisitorPackage()}.${genHelper.getCdName()}Visitor;
import ${genHelper.getPackageName()}._ast.${genHelper.getASTNodeBaseType()};
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import java.util.Iterator;

public class ${genHelper.getCdName()}2OD implements ${genHelper.getCdName()}Visitor {
      
  private ${genHelper.getCdName()}Visitor realThis = this;
  
  protected IndentPrinter pp;
  
  protected ReportingRepository reporting;
  
  protected boolean printEmptyOptional = false;
  
  protected boolean printEmptyList = false;
    
  public ${genHelper.getCdName()}2OD(IndentPrinter printer, ReportingRepository reporting) {
    this.reporting = reporting;
    this.pp = printer;
  }
    
  <#list cd.getTypes() as type>
    <#if type.isClass()>
      <#assign astName = genHelper.getJavaASTName(type)>
  
      @Override
      public void handle(${astName} node) {
        String name = StringTransformations.uncapitalize(reporting.getASTNodeNameFormatted(node));
        printObject(name, "${astName}");
        pp.indent();
        if (node.isPresentSymbol()) {
          String symName = StringTransformations.uncapitalize(reporting.getSymbolNameFormatted(node.getSymbolOpt().get()));
          pp.println("symbol = " + symName + ";");
        } else if (printEmptyOptional) {
          pp.println("symbol = absent;");
        }
        if (node.isPresentEnclosingScope()) {
          String scopeName = StringTransformations.uncapitalize(reporting.getScopeNameFormatted(node.getEnclosingScopeOpt().get()));
          pp.println("enclosingScope = " + scopeName + ";");
        } else if (printEmptyOptional) {
          pp.println("enclosingScope = absent;");
        }
        if (node.isPresentSpannedScope()) {
          String scopeName = StringTransformations.uncapitalize(reporting.getScopeNameFormatted(node.getSpannedScopeOpt().get()));
          pp.println("spanningScope = " + scopeName + ";");
        } else if (printEmptyOptional) {
          pp.println("spannedScope = absent;");
        }
        <#list type.getAllVisibleFields() as field>
 
          <#if genHelper.isAstNode(field) || genHelper.isOptionalAstNode(field) >
            <#assign attrGetter = genHelper.getPlainGetter(field)>
            <#if genHelper.isOptional(field)>
              if (node.${attrGetter}().isPresent()) {
     			pp.print("${field.getName()}");
   			    pp.print(" = ");
                node.${attrGetter}().get().accept(getRealThis());
                pp.println(";");
              } else if (printEmptyOptional) {
                pp.println("${field.getName()} = absent;");
              }
            <#else>
              if (null != node.${attrGetter}()) {          
      			pp.print("${field.getName()}");
   			    pp.print(" = ");
                node.${attrGetter}().accept(getRealThis());
                pp.println(";");
              }
            </#if>
          <#elseif genHelper.isListAstNode(field)>
            <#assign attrGetter = genHelper.getPlainGetter(field)>
            <#assign astChildTypeName = genHelper.getAstClassNameForASTLists(field)>
            {
              Iterator<${astChildTypeName}> iter_${field.getName()} = node.${attrGetter}().iterator();
              boolean isEmpty = true;
              if (iter_${field.getName()}.hasNext()) {
       			pp.print("${field.getName()}");
   			    pp.print(" = [");
   			    pp.println("// *size: " + node.${attrGetter}().size());
				pp.indent();
   			    isEmpty = false;
              } else if (printEmptyList) {
                pp.print("${field.getName()}");
                pp.println(" = [];");
              }
              boolean isFirst = true;
              while (iter_${field.getName()}.hasNext()) {
                if (!isFirst) {
                  pp.println(",");
                }
                isFirst = false;
                iter_${field.getName()}.next().accept(getRealThis());
              }
              if (!isEmpty) {
              	pp.println("];");
              	pp.unindent();
              }
             
            }
          <#elseif genHelper.isOptional(field)>
            if (node.${genHelper.getPlainGetter(field)}().isPresent()) {
              printAttribute("${field.getName()}", "\"" + String.valueOf(node.${genHelper.getPlainGetter(field)}().get()) + "\"");
            } else if (printEmptyOptional) {
              pp.println("${field.getName()} = absent;");
            }
          <#elseif genHelper.isListType(field.getType())>
            {
              String sep = "";
              <#if genHelper.isListOfString(field)>
                String str = "\"";
              <#else>
                String str = "";
              </#if>
              Iterator<?> it = node.${genHelper.getPlainGetter(field)}().iterator();
              boolean isEmpty = true;
              if (it.hasNext() || printEmptyList) {
                pp.print("${field.getName()}" + " = [");
                isEmpty = false;
              }
              while (it.hasNext()) {
                pp.print(sep); 
                pp.print(str + String.valueOf(it.next()) + str);
                sep = ", ";
              }
              if (!isEmpty) {
              	pp.println("];");
              }
            }
          <#else>
            <#assign fieldType = field.getType()>
            <#if genHelper.isString(fieldType.getName())>
              printAttribute("${field.getName()}", "\"" + String.valueOf(node.${genHelper.getPlainGetter(field)}()) + "\"");
            <#else>
              printAttribute("${field.getName()}", String.valueOf(node.${genHelper.getPlainGetter(field)}()));
            </#if>
          </#if>
        </#list>
        pp.unindent();
        pp.print("}");
     } 

    </#if>
  </#list>  
  
  private void printAttribute(String name, String value) {
    pp.print(name);
    pp.print(" = ");
    pp.print(value);
    pp.println(";");
  }
  
  private void printObject(String objName, String objType) {
    pp.print(objName);
    pp.print(":");
    pp.print(Names.getSimpleName(objType));
    pp.println(" {");
  }
  
  public String printObjectDiagram(String modelName, ${genHelper.getASTNodeBaseType()} node) {
    pp.clearBuffer();
    pp.setIndentLength(2);
    pp.print("objectdiagram ");
    pp.print(modelName);
    pp.println(" {");
    pp.indent();
    node.accept(getRealThis());
    pp.print(";");
    pp.unindent();
    pp.println("}");
    return pp.getContent();
  }
  
  @Override
  public void setRealThis(${genHelper.getCdName()}Visitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public ${genHelper.getCdName()}Visitor getRealThis() {
    return realThis;
  }
  
  /**
   * @return the printEmptyOptional
   */
  public boolean isPrintEmptyOptional() {
    return this.printEmptyOptional;
  }

  
  /**
   * @param printEmptyOptional the printEmptyOptional to set
   */
  public void setPrintEmptyOptional(boolean printEmptyOptional) {
    this.printEmptyOptional = printEmptyOptional;
  }

  
  /**
   * @return the printEmptyList
   */
  public boolean isPrintEmptyList() {
    return this.printEmptyList;
  }

  
  /**
   * @param printEmptyList the printEmptyList to set
   */
  public void setPrintEmptyList(boolean printEmptyList) {
    this.printEmptyList = printEmptyList;
  }
  
}
  
