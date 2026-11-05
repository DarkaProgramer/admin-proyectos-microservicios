# Ejecuta mvn clean test en ambos microservicios con JDK 21 y guarda evidencia REAL en /evidencia
$root = Split-Path -Parent $PSScriptRoot
$ev = Join-Path $root "evidencia"
New-Item -ItemType Directory -Force $ev | Out-Null
$env:JAVA_HOME = "C:\Tools\jdk-21.0.13+9"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
& java -version 2>&1 | Out-File "$ev\versiones.txt"
& mvn -version 2>&1 | Out-File "$ev\versiones.txt" -Append
$resumen = @()
foreach ($m in @("Ejercicio5MyAPIGestor","Ejercicio5Cliente")) {
  Push-Location (Join-Path $root $m)
  & mvn -B clean test 2>&1 | Tee-Object -FilePath "$ev\mvn-test-$m.log"
  $csv = "target\site\jacoco\jacoco.csv"
  if (Test-Path $csv) {
    Copy-Item $csv "$ev\jacoco-$m.csv" -Force
    $d = Import-Csv $csv
    $mm = ($d | Measure-Object METHOD_MISSED -Sum).Sum
    $mc = ($d | Measure-Object METHOD_COVERED -Sum).Sum
    $lm = ($d | Measure-Object LINE_MISSED -Sum).Sum
    $lc = ($d | Measure-Object LINE_COVERED -Sum).Sum
    $pct = if (($mm+$mc) -gt 0) { [math]::Round(100*$mc/($mm+$mc),2) } else { 0 }
    $resumen += "$m : metodos cubiertos $mc / $($mm+$mc) = $pct % ; lineas cubiertas $lc / $($lm+$lc)"
    $d | Where-Object { [int]$_.METHOD_MISSED -gt 0 } | Select-Object CLASS,METHOD_MISSED,METHOD_COVERED | Format-Table | Out-String | Out-File "$ev\metodos-sin-cubrir-$m.txt"
  } else { $resumen += "$m : NO se genero jacoco.csv (revisar log)" }
  Pop-Location
}
$resumen | Out-File "$ev\resumen-cobertura.txt"
$resumen
Write-Host "Evidencia guardada en $ev"
