<#-- (c) https://github.com/MontiCore/monticore -->
public ${ast.getJavaClassName()}(List<ASTNode> hostGraph) {
	this.hostGraph = hostGraph;
	this.glex = new GlobalExtensionManagement();
	// technically, we should call setupReporting here as well
	// but that would be a breaking change on which existing code depends
}

public ${ast.getJavaClassName()}(ASTNode... hostGraph){
  this(Lists.newArrayList(hostGraph));
}

public ${ast.getJavaClassName()}(GlobalExtensionManagement glex, ASTNode... hostGraph){
	this.hostGraph = Lists.newArrayList(hostGraph);
	this.glex = glex;
	// technically, we should call setupReporting here as well
	// but that would be a breaking change on which existing code depends
}

public ${ast.getJavaClassName()}(GlobalExtensionManagement glex,ASTNode astNode){
  this(astNode, glex);
}

public ${ast.getJavaClassName()}(ASTNode astNode) {
	this(astNode, new GlobalExtensionManagement());
}

public ${ast.getJavaClassName()}(ASTNode astNode, GlobalExtensionManagement glex) {
  this.hostGraph = new ArrayList<>();
	this.hostGraph.add(astNode);
  this.glex = glex;
  this.setupReporting();
}

protected void setupReporting() {
  ReportManager.ReportManagerFactory factory = new ReportManager.ReportManagerFactory() {
    @Override
    public ReportManager provide(String modelName) {
      ReportManager reports = new ReportManager("target/generated-sources");
      TransformationReporter transformationReporter = new TransformationReporter(
      "target/generated-sources/reports/transformations", modelName, new ReportingRepository(new ASTNodeIdentHelper()));
      reports.addReportEventHandler(transformationReporter);
      return reports;
    }
  };

  Reporting.init("target/generated-sources/reports/transformations", "target/generated-sources", factory);
  Reporting.on("${ast.getClassname()}");
}
