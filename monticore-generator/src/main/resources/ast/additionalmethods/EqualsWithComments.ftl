<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast","astType")}
   <#assign genHelper = glex.getGlobalVar("astHelper")>
   <#assign astName = genHelper.getPlainName(astType)>
      ${astName} comp;
    if ((o instanceof ${astName})) {
      comp = (${astName}) o;
    } else {
      return false;
    }
    if (!equalAttributes(comp)) {
      return false;
    }
    <#-- TODO: attributes of super class - use symbol table -->
    <#-- list astType.getCDAttributeList()  as attribute> 
      <#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
      <#if !genHelper.isAstNode(attribute)>
	// comparing ${attributeName} 
	    <#if genHelper.isPrimitive(attribute.getType())>
	if (!(this.${attributeName} == comp.${attributeName})) {
	  return false;
	}
	    <#else>
	if ( (this.${attributeName} == null && comp.${attributeName} != null) 
	  || (this.${attributeName} != null && !this.${attributeName}.equals(comp.${attributeName})) ) {
	  return false;
	}
	    </#if>
	  </#if>  
    </#list --> 
    // comparing comments
    if (get_PreCommentList().size() == comp.get_PreCommentList().size()) {
      java.util.Iterator<de.monticore.ast.Comment> one = get_PreCommentList().iterator();
      java.util.Iterator<de.monticore.ast.Comment> two = comp.get_PreCommentList().iterator();
      while (one.hasNext()) {
        if (!one.next().equals(two.next())) {
          return false;
        }
      }
    } else {
      return false;
    }
    
    if (get_PostCommentList().size() == comp.get_PostCommentList().size()) {
      java.util.Iterator<de.monticore.ast.Comment> one = get_PostCommentList().iterator();
      java.util.Iterator<de.monticore.ast.Comment> two = comp.get_PostCommentList().iterator();
      while (one.hasNext()) {
        if (!one.next().equals(two.next())) {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
