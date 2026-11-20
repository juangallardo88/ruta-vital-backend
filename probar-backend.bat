@echo off
setlocal
cd /d "%~dp0"
set LOG=%~dp0resultado-pruebas.log
echo Ruta Vital - compilacion y pruebas > "%LOG%"
echo %DATE% %TIME% >> "%LOG%"

where java >nul 2>&1
if errorlevel 1 (
  echo ERROR: no se encontro Java. Instala el JDK 17. >> "%LOG%"
  echo FIN-ERROR >> "%LOG%"
  exit /b 1
)
java -version >> "%LOG%" 2>&1

set MVN=mvn
where mvn >nul 2>&1
if errorlevel 1 (
  if not exist "%USERPROFILE%\.m2\apache-maven\apache-maven-3.9.11\bin\mvn.cmd" (
    echo Descargando Apache Maven 3.9.11... >> "%LOG%"
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
      "$ErrorActionPreference='Stop'; $d=Join-Path $env:USERPROFILE '.m2\apache-maven'; New-Item -ItemType Directory -Force -Path $d | Out-Null; $z=Join-Path $d 'maven.zip'; Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.9.11/binaries/apache-maven-3.9.11-bin.zip' -OutFile $z -UseBasicParsing; Expand-Archive -Path $z -DestinationPath $d -Force; Remove-Item $z" >> "%LOG%" 2>&1
  )
  set MVN="%USERPROFILE%\.m2\apache-maven\apache-maven-3.9.11\bin\mvn.cmd"
)

echo Ejecutando pruebas... >> "%LOG%"
call %MVN% -B test >> "%LOG%" 2>&1
if errorlevel 1 (
  echo FIN-ERROR >> "%LOG%"
) else (
  echo FIN-OK >> "%LOG%"
)
endlocal
