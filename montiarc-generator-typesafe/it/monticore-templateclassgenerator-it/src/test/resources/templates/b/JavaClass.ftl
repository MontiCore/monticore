${tc.params("String _package", "String classname", "java.util.List<types.Attribute> attributes")}
package ${_package};

public class ${classname} {

  public ${classname}() {
    super();
  }


  <#list attributes as attribute>
    private ${attribute.getType()} ${attribute.getName()};
    
    public ${attribute.getType()} get${attribute.getName()?cap_first}(){
      return this.${attribute.getName()};
    }
    
    public void set${attribute.getName()?cap_first}(${attribute.getType()} ${attribute.getName()}) {
      this.${attribute.getName()} = ${attribute.getName()};
    }
  </#list>

  

}
