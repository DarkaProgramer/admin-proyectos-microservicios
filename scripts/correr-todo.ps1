# TODO EN UNO: importa BD si hace falta, prueba BD, levanta apps, prueba endpoints, corre Maven+JaCoCo y empaqueta evidencia.zip
$root = Split-Path -Parent $PSScriptRoot
$ev = Join-Path $root "evidencia"; New-Item -ItemType Directory -Force $ev | Out-Null
$env:JAVA_HOME = "C:\Tools\jdk-21.0.13+9"; $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
function Puerto($p) { (Test-NetConnection localhost -Port $p -WarningAction SilentlyContinue).TcpTestSucceeded }
if (-not (Puerto 3306)) { Write-Host "MySQL no esta encendido. Abre Laragon y dale Iniciar Todo, luego repite."; exit 1 }

# 1) Base de datos: crear e importar si hace falta
$mysql = (Get-ChildItem "C:\laragon\bin\mysql" -Recurse -Filter mysql.exe | Select-Object -First 1).FullName
& $mysql -uroot -e "CREATE DATABASE IF NOT EXISTS dbgestor" 2>&1 | Out-Null
$t = & $mysql -uroot -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='dbgestor' AND table_name='usuario'" 2>&1
if ("$t".Trim() -ne "1") {
  Write-Host "Importando dump..."
  cmd /c "`"$mysql`" -uroot dbgestor < `"$root\dump-dbgestor-202406261453.sql`"" 2>&1 | Out-File "$ev\import-dump.log"
}
& "$PSScriptRoot\pruebas-bd.ps1" | Out-Null

# 2) Levantar apps y probar endpoints
$procs = @()
foreach ($m in @(@("Ejercicio5MyAPIGestor",8081), @("Ejercicio5Cliente",9080))) {
  $p = Start-Process cmd.exe -ArgumentList "/c mvn -B spring-boot:run > `"$ev\run-$($m[0]).log`" 2>&1" -WorkingDirectory "$root\$($m[0])" -PassThru -WindowStyle Minimized
  $procs += $p
  $i = 0; while (-not (Puerto $m[1]) -and $i -lt 40) { Start-Sleep 5; $i++ }
  Write-Host "$($m[0]) en puerto $($m[1]): $(Puerto $m[1])"
}
& "$PSScriptRoot\pruebas-endpoints.ps1"
foreach ($p in $procs) { taskkill /PID $p.Id /T /F 2>&1 | Out-Null }
Start-Sleep 3

# 3) Maven + JaCoCo
& "$PSScriptRoot\ejecutar-pruebas-maven.ps1"

# 4) Empaquetar
if (Test-Path "$root\evidencia.zip") { Remove-Item "$root\evidencia.zip" -Force }
Compress-Archive -Path "$ev\*" -DestinationPath "$root\evidencia.zip"
Write-Host "`nLISTO. Adjunta el archivo evidencia.zip (esta en la carpeta del proyecto) en el chat."
