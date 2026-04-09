<#-- (c) https://github.com/MontiCore/monticore -->
public ${ast.getJavaClassName()}(List<ASTNode> hostGraph) {
  this.hostGraph = hostGraph;
}

public ${ast.getJavaClassName()}(ASTNode... hostGraph){
  this.hostGraph = Lists.newArrayList(hostGraph);
}

public ${ast.getJavaClassName()}(GlobalExtensionManagement glex, ASTNode... hostGraph){
  this.hostGraph = Lists.newArrayList(hostGraph);
  this.glex = glex;
}

public ${ast.getJavaClassName()}(GlobalExtensionManagement glex,ASTNode astNode){
  this(astNode,glex);
}

public ${ast.getJavaClassName()}(ASTNode astNode) {
  hostGraph = new ArrayList<>();
  hostGraph.add(astNode);
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

  Reporting.init("target/generated-sources", "target/generated-sources", factory);
  Reporting.on("${ast.getClassname()}");
}

public ${ast.getJavaClassName()}(ASTNode astNode, GlobalExtensionManagement glex) {
  hostGraph = new ArrayList<>();
  hostGraph.add(astNode);
  this.glex = glex;
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
