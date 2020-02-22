<!-- (c) https://github.com/MontiCore/monticore -->
<center><div style="text-align:center" ><img src="mc-logo.png" /></div></center>

# MontiCore - Language Workbench And Development Tool Framework 

* [**MontiCore Reference Manual**](http://monticore.de/MontiCore_Reference-Manual.2017.pdf).

## General disclaimer

(Repeated from the the BSD 3 Clause license): 

This software is provided by the copyright holders and contributors
"as is" and any expressed or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.

## Included Software

This product includes the following software:
* [AntLR](http://www.antlr.org/)
* [FreeMarker](http://freemarker.org/)

## Contribution 

When you want to contribute: Please make sure that your complete workspace only 
uses UNIX line endings (LF) and all files are UTF-8 without BOM. On Windows you should 
configure git to not automatically replace LF with CRLF during checkout 
by executing the following configuration: 

    git config --global core.autocrlf input
    
## Build MontiCore

MontiCore is currently partially still built using maven, but partially 
already migrated to gradle. It is recommended to use the MontiCore internal gradle
wrapper (`gradlew`).

Please note that from the top level build script, not everything is built and 
all tests executed. It is a deliberate decision, to exclude some of the longer 
lasting tasks.

* build the productive code (including the unit tests, ~8 min)
`mvn install`
  * skipping the unit tests: `mvn install -Dmaven.test.skip=true`
* run integration tests (which are not included in the unit tests, ~30 min)   
  * Integration tests of the generator: 
    * maven (deprecated): `mvn install -f monticore-generator/it/pom.xml` or 
    * gradle: in `monticore-generator/it/` call `gradlew build`
  * EMF Integration tests of the generator: 
    * maven (deprecated): `mvn install -f monticore-generator/it/pom.xml -P emf-it-tests` or 
    * gradle: in `monticore-generator/it/` call `gradlew build -PbuildProfile=emf`
  * Experiments (from the Reference Manual) as integration tests:
    * maven (deprecated): `mvn install -f monticore-generator/it/experiments/pom.xml` or
    * gradle: in `monticore-generator/it/experiments/` call `gradlew build`
  * Grammar integration tests:
     * in `monticore-grammar/monticore-grammar-it` call `gradlew build`
  * TemplateClassGenerator integration tests 
    * maven (deprecated): `mvn install -f /monticore-templateclassgenerator/it/monticore-templateclassgenerator-it/pom.xml` or 
    * gradle: in `/monticore-templateclassgenerator/it/monticore-templateclassgenerator-it` call `gradlew build`
* clean:
  * call `mvn clean`
  * cleaning integration tests:
    * using maven (deprecated): `mvn clean` (including the `-f` argument, see above) 
    * using gradle `gradlew clean` within the corresponding directory (see above)

  
## Further Information

* see also [**MontiCore Reference Manual**](http://www.monticore.de/)

* [Changelog](CHANGELOG.md) - Release Notes

* [MontiCore project](README.md) - MontiCore 
