<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className")}
  /**
    * Builder for {@link ${className}}.
    */

  public static class ${className}Builder {

    protected String name;

    protected ${className}Builder() {}

    public ${className} build() {
      return new ${className}(name);
    }

    public ${className}Builder name(String name) {
      this.name = name;
      return this;
    }
  }