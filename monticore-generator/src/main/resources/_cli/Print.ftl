<#-- (c) https://github.com/MontiCore/monticore -->
// print to stdout or file
  if (path.isEmpty()) {
    System.out.println(content);
  } else {
    java.io.File f = new java.io.File(path);
    // create directories (logs error otherwise)
    f.getAbsoluteFile().getParentFile().mkdirs();
    java.io.FileWriter writer;
    try {
      writer = new java.io.FileWriter(f);
      writer.write(content);
      writer.close();
    } catch (java.io.IOException e) {
      Log.error("0xA7105 Could not write to file " + f.getAbsolutePath());
    }
  }