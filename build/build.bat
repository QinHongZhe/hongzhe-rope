REM windows package

set buildPath=build
set distPath=%buildPath%\dist
set pluginPath=%distPath%\plugins
set pluginConfigPath=%distPath%\pluginsConfig

cd ../
REM package
call mvn clean install -Dmaven.test.skip=true


rmdir %distPath% /s /q

mkdir %distPath%
mkdir %pluginPath%
mkdir %pluginConfigPath%


REM copy main program and config
xcopy data-transfer\target\data-transfer-*-exec.jar %distPath% /s /i
xcopy data-transfer\src\main\resources\application-prod.yml %distPath% /s


REM copy plugin and config
for /d %%i in (plugins\*) do (
    xcopy %%i\target\*-jar-with-dependencies.jar %pluginPath% /s /i
    xcopy %%i\src\main\resources\*.yml %pluginConfigPath% /s /i
)

xcopy %buildPath%\start\start.bat %distPath%\ /s /i /y

cd %distPath%
rename data-transfer-*-exec.jar data-transfer-start.jar
rename application-prod.yml application.yml



pause