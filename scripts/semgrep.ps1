# Escaneo SAST con Semgrep (en Docker). Ejecutar desde la raiz del proyecto.
New-Item -ItemType Directory -Force evidencia-u3 | Out-Null
docker run --rm -v "${PWD}:/src" semgrep/semgrep semgrep scan `
  --config p/java --config p/owasp-top-ten --metrics=off `
  --text-output=/src/evidencia-u3/semgrep-reporte.txt `
  --json-output=/src/evidencia-u3/semgrep-reporte.json `
  /src/Ejercicio5MyAPIGestor/src /src/Ejercicio5Cliente/src
Get-Content evidencia-u3\semgrep-reporte.txt | Select-Object -Last 40
