@echo off
setlocal EnableDelayedExpansion

rem (c) https://github.com/MontiCore/monticore
rem script for all preprocessing steps of the pages job
rem is used to have uniform bases for both gitlab and github pages

rem Check if arguments contain the word "inplace"
echo %* | findstr /i "\<inplace\>" >nul
if %errorlevel% equ 0 (
    for /f "delims=" %%f in ('dir /b /s /a-d "docs\docs\*.md" 2^>nul') do (
        rem Use PowerShell to do the sed and perl regex replacements
        powershell -NoProfile -Command "$txt = (Get-Content -Raw -LiteralPath '%%f'); $txt = $txt -replace '\[\[_TOC_\]\]', ''; $txt = $txt -replace '\[([^\[\]\(\)]*)\]\([^\[\]\(\)]*git\.rwth-aachen\.de[^\[\]\(\)]*?\)', '$1'; Set-Content -LiteralPath '%%f' -Value $txt"
    )
    echo [INFO] Removed all occurrences of '[[_TOC_]]' in *.md files
    echo [INFO] Removed all links to https://git.rwth-aachen.de in *.md files
)

rem move all directories that contain *.md files to the docs folder
rem because mkdocs can only find *.md files there
if exist "docs_wd" rmdir /s /q "docs_wd" 2>nul

rem Check if arguments contain the word "symlink"
echo %* | findstr /i "\<symlink\>" >nul
if %errorlevel% equ 0 (
    rem use symlinks to track updates
    mkdir "docs_wd"

    rem Use Directory Junctions (/J) for folders (does not require admin rights)
    mklink /J "docs_wd\overrides" "docs\overrides" >nul
    mklink /J "docs_wd\stylesheets" "docs\stylesheets" >nul
    mklink /J "docs_wd\scripts" "docs\scripts" >nul
    mklink /J "docs_wd\img" "docs\img" >nul

    rem all images referenced in the root-Readme must be handled specially :(
    if not exist "docs_wd\docs\img" mkdir "docs_wd\docs\img"

    rem Standard symlinks for files require Admin rights or Developer Mode enabled in Windows
    mklink "docs_wd\docs\img\MC_Symp_Banner.png" "..\..\..\docs\img\MC_Symp_Banner.png" >nul
    mklink "docs_wd\README.md" "..\docs\README.md" >nul

    rem Link to the javadoc directories
    if not exist "docs_wd\monticore-runtime" mkdir "docs_wd\monticore-runtime"
    mklink /J "docs_wd\monticore-runtime\javadoc" "monticore-runtime\target\docs\javadoc" >nul
    mklink /J "docs_wd\monticore-runtime\testFixturesJavadoc" "monticore-runtime\target\docs\testFixturesJavadoc" >nul

    if not exist "docs_wd\monticore-grammar" mkdir "docs_wd\monticore-grammar"
    mklink /J "docs_wd\monticore-grammar\javadoc" "monticore-grammar\target\docs\javadoc" >nul
    mklink /J "docs_wd\monticore-grammar\testFixturesJavadoc" "monticore-grammar\target\docs\testFixturesJavadoc" >nul

    echo [INFO] Using symlinks for live editing
) else (
    rem Copy fallback
    xcopy /E /I /Q "docs" "docs_wd" >nul
    del /Q "docs_wd\*.md" 2>nul
    copy "docs\README.md" "docs_wd\README.md" >nul

    rem all images referenced in the root-Readme must be handled specially :(
    if not exist "docs_wd\docs\img" mkdir "docs_wd\docs\img"
    copy "docs\img\MC_Symp_Banner.png" "docs_wd\docs\img\MC_Symp_Banner.png" >nul
    echo [INFO] Copied site design

    rem Copy the javadoc directories
    if not exist "docs_wd\monticore-runtime" mkdir "docs_wd\monticore-runtime"
    xcopy /E /I /Q "monticore-runtime\target\docs\javadoc" "docs_wd\monticore-runtime\javadoc" >nul
    xcopy /E /I /Q "monticore-runtime\target\docs\testFixturesJavadoc" "docs_wd\monticore-runtime\testFixturesJavadoc" >nul

    if not exist "docs_wd\monticore-grammar" mkdir "docs_wd\monticore-grammar"
    xcopy /E /I /Q "monticore-grammar\target\docs\javadoc" "docs_wd\monticore-grammar\javadoc" >nul
    xcopy /E /I /Q "monticore-grammar\target\docs\testFixturesJavadoc" "docs_wd\monticore-grammar\testFixturesJavadoc" >nul

    echo [INFO] Copied JavaDocs
)

rem We link to java & mc4 files in our md files - which is why we have to redirect them too
set "BASEDIR=%CD%\"
for %%D in ("00.org" "docs" "monticore-grammar\src" "monticore-libraries\javagen-runtime" "monticore-runtime\src") do (
    if exist "%%~D" (
        for /f "delims=" %%F in ('dir /s /b /a-d "%%~D\*.md" 2^>nul') do (
            set "ABSPATH=%%F"

            rem Enter a localized scope to manipulate relative paths cleanly
            setlocal EnableDelayedExpansion

            rem Extract the relative path by dropping the base directory prefix
            set "RELPATH=!ABSPATH:%BASEDIR%=!"

            rem Convert Windows backslashes (\) to forward slashes (/) for markdown includes
            set "SNIPPETPATH=!RELPATH:\=/!"
            set "TARGET_FILE=docs_wd\!RELPATH!"

            rem Retrieve just the directory path from the target file path
            for %%T in ("!TARGET_FILE!") do set "TARGET_DIR=%%~dpT"

            if not exist "!TARGET_DIR!" mkdir "!TARGET_DIR!"

            rem Use snippets to include the original files content
            if not exist "!TARGET_FILE!" (
                echo --8^<-- "!SNIPPETPATH!" > "!TARGET_FILE!"
            )

            endlocal
        )
    )
)
echo [INFO] Created snippet files

rem the landing page snippet has to be removed again
if exist "docs_wd\docs\README.md" del /Q "docs_wd\docs\README.md"
