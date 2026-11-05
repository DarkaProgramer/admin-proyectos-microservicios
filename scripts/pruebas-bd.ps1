# Ejecuta pruebas-bd.sql contra el MySQL de Laragon y guarda evidencia/bd-resultados.txt
$root = Split-Path -Parent $PSScriptRoot
$ev = Join-Path $root "evidencia"; New-Item -ItemType Directory -Force $ev | Out-Null
$mysql = Get-ChildItem "C:\laragon\bin\mysql" -Recurse -Filter mysql.exe -ErrorAction SilentlyContinue | Select-Object -First 1
if (-not $mysql) { Write-Host "No encontre mysql.exe en C:\laragon\bin\mysql"; exit 1 }
Get-Content "$PSScriptRoot\pruebas-bd.sql" -Raw | & $mysql.FullName -uroot --force --table 2>&1 | Out-File "$ev\bd-resultados.txt" -Encoding UTF8
Get-Content "$ev\bd-resultados.txt"
Write-Host "Evidencia en $ev\bd-resultados.txt"
