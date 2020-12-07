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

MontiCore is currently built using Gradle. It is recommended to use the MontiCore internal gradle
wrapper (`gradlew`).

Please note that from the top level build script, not everything is built and 
all tests executed. It is a deliberate decision, to exclude some of the longer 
lasting tasks.

* build the productive code (including the unit tests, ~8 min)
  * `gradlew buildMC`
  * skipping the unit tests: `gradlew assembleMC`
* run integration tests (which are not included in the unit tests, ~30 min)   
  * all integration tests
    * `gradlew testIT`
  * Integration tests of the generator: 
    *  `gradlew -p monticore-test/it build`
  * EMF Integration tests of the generator (only test collection not included in `testIt`): 
    * `gradlew -p monticore-test/it build -PbuildProfile=emf`
  * Experiments (from the Reference Manual) as integration tests:
    * `gradlew -p monticore-test/01.experiments build` and 
    * `gradlew -p monticore-test/02.experiments build`
  * Grammar integration tests:
     * `gradlew -p monticore-test/monticore-grammar-it build`
* clean:
  * call `gradlew clean`
  * cleaning integration tests:
    * using gradle `gradlew clean` within the corresponding subproject (see above)

  
## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

