# Pruebas reales de endpoints. Requiere: Laragon/MySQL con dbgestor y backend corriendo en :8081
# (y opcionalmente el cliente en :9080). Genera evidencia/endpoints-resultados.csv y .md
param([string]$Api = "http://localhost:8081", [string]$Cliente = "http://localhost:9080")
Add-Type -AssemblyName System.Net.Http
$http = New-Object System.Net.Http.HttpClient
$http.Timeout = [TimeSpan]::FromSeconds(20)
$root = Split-Path -Parent $PSScriptRoot
$ev = Join-Path $root "evidencia"; New-Item -ItemType Directory -Force $ev | Out-Null
$script:res = @()
function Call($method, $url, $body) {
  $req = New-Object System.Net.Http.HttpRequestMessage ([System.Net.Http.HttpMethod]::new($method)), $url
  if ($null -ne $body) { $req.Content = New-Object System.Net.Http.StringContent($body, [Text.Encoding]::UTF8, "application/json") }
  try { $r = $http.SendAsync($req).Result; return @{ code = [int]$r.StatusCode; body = $r.Content.ReadAsStringAsync().Result } }
  catch { return @{ code = 0; body = "ERROR: $($_.Exception.Message)" } }
}
function Reg($id, $desc, $method, $ruta, $esperado, $r, $ok) {
  $b = "$($r.body)"; if ($b.Length -gt 120) { $b = $b.Substring(0,120) + "..." }
  $b = $b -replace "[\r\n,]", " "
  $script:res += [pscustomobject]@{ ID=$id; Descripcion=$desc; Metodo=$method; Ruta=$ruta; Esperado=$esperado; HTTP=$r.code; Obtenido=$b; Resultado=$(if($ok){"PASS"}else{"FAIL"}) }
  Write-Host ("{0} {1} -> {2}" -f $id, $(if($ok){"PASS"}else{"FAIL"}), $r.code)
}
$n = 0
function Id($p) { $script:n++; return ("{0}-{1:00}" -f $p, $script:n) }

# ---- Disponibilidad
$r = Call "POST" "$Api/buscarUsuarios" $null
Reg (Id "EP") "Backend responde y lista usuarios" "POST" "/buscarUsuarios" "200 y arreglo JSON" $r (($r.code -eq 200) -and ($r.body.StartsWith("[")))
if ($r.code -ne 200) { Write-Host "El backend no responde en $Api. Levantalo y vuelve a correr."; $script:res | Export-Csv "$ev\endpoints-resultados.csv" -NoTypeInformation -Encoding UTF8; exit 1 }

# ---- Usuario: casos especificos
$r = Call "GET" "$Api/buscarUsuario?id=131" $null
Reg (Id "EP") "Buscar usuario existente (id 131 del dump)" "GET" "/buscarUsuario?id=131" "200 con id_usuario=131" $r (($r.code -eq 200) -and ($r.body -match '"id_usuario":131'))
$r = Call "GET" "$Api/buscarUsuario?id=999999" $null
Reg (Id "EP") "Buscar usuario inexistente" "GET" "/buscarUsuario?id=999999" "200 con entidad vacia (id_usuario null)" $r (($r.code -eq 200) -and ($r.body -match '"id_usuario":null'))
$r = Call "GET" "$Api/buscarUsuario" $null
Reg (Id "EP") "Buscar usuario sin parametro id" "GET" "/buscarUsuario" "400" $r ($r.code -eq 400)
$r = Call "GET" "$Api/buscarUsuario?id=abc" $null
Reg (Id "EP") "Buscar usuario con id no numerico" "GET" "/buscarUsuario?id=abc" "400" $r ($r.code -eq 400)
$r = Call "POST" "$Api/getUsuarioLogin" '{"correo_electronico":"ana22533@cbtis75.edu.mx","contrasena":"0722"}'
Reg (Id "EP") "Login con credenciales validas" "POST" "/getUsuarioLogin" "200 con nombre del usuario" $r (($r.code -eq 200) -and ($r.body -match 'Ana Paola'))
$r = Call "POST" "$Api/getUsuarioLogin" '{"correo_electronico":"no@existe.com","contrasena":"mal"}'
Reg (Id "EP") "Login con credenciales invalidas" "POST" "/getUsuarioLogin" "200 con nombre 000" $r (($r.code -eq 200) -and ($r.body -match '"nombre":"000"'))
$r = Call "POST" "$Api/getUsuarioLogin" $null
Reg (Id "EP") "Login sin cuerpo" "POST" "/getUsuarioLogin" "400" $r ($r.code -eq 400)
$r = Call "POST" "$Api/addUsuario" '{no es json'
Reg (Id "EP") "Alta de usuario con JSON malformado" "POST" "/addUsuario" "400" $r ($r.code -eq 400)

# ---- CRUD completo por entidad (alta, consulta, actualizacion, baja, verificacion)
$tag = "ZZ_TEST_" + [guid]::NewGuid().ToString("N").Substring(0,8)
$ents = @(
 @{ n="Usuario"; list="buscarUsuarios"; find="buscarUsuario?id="; add="addUsuario"; upd="updateUsuario"; del="deleteUsuario"; idf="id_usuario"; mk="nombre"; mv=$tag; chg="rol";
    body=@{ nombre=$tag; correo_electronico="$tag@test.com"; contrasena="x"; rol="Estudiante" } },
 @{ n="Proyecto"; list="buscarProyectos"; find="buscarProyecto?id_proyecto="; add="addProyecto"; upd="updateProyecto"; del="deleteProyecto"; idf="id_proyecto"; mk="nombre"; mv=$tag; chg="descripcion";
    body=@{ nombre=$tag; descripcion="prueba"; fecha_inicio="2024-01-01"; fecha_finalizacion="2024-02-01"; estado_proyecto="Activo"; fondo="f.png" } },
 @{ n="Tarea"; list="buscarTareas"; find="buscarTarea?id_tarea="; add="addTareas"; upd="updateTareas"; del="deleteTareas"; idf="id_tarea"; mk="nombre"; mv=$tag; chg="estado";
    body=@{ nombre=$tag; descripcion="prueba"; fecha_vencimiento="2024-03-01"; estado="pendiente"; lista="L1"; id_proyecto=197 } },
 @{ n="Comentario"; list="buscarComentarios"; find="buscarComentario?id="; add="addComentario"; upd="updateComentario"; del="deleteComentario"; idf="id_comentario"; mk="texto_comentario"; mv=$tag; chg="texto_comentario";
    body=@{ texto_comentario=$tag; fecha_hora="2024-06-26T10:00:00"; id_usuario="Tester"; id_tarea=199 } },
 @{ n="Asignacion"; list="buscarAsignaciones"; find="buscarAsignacion?id="; add="addAsignacion"; upd="updateAsignacion"; del="deleteAsignacion"; idf="id_asignacion"; mk="id_usuario"; mv=99999; chg="id_tarea"; val=88888;
    body=@{ id_usuario=99999; id_tarea=99999; fecha_asignacion="2024-06-26" } }
)
foreach ($e in $ents) {
  $p = "CRUD-" + $e.n
  $r = Call "POST" "$Api/$($e.add)" ($e.body | ConvertTo-Json)
  Reg (Id $p) "Alta de $($e.n) valida" "POST" "/$($e.add)" "200 y valor 1" $r (($r.code -eq 200) -and ($r.body.Trim() -eq "1"))
  $lst = (Call "POST" "$Api/$($e.list)" $null).body | ConvertFrom-Json
  $row = $lst | Where-Object { $_.($e.mk) -eq $e.mv } | Select-Object -First 1
  $found = $null -ne $row
  Reg (Id $p) "El $($e.n) dado de alta aparece en el listado" "POST" "/$($e.list)" "registro con marcador presente" @{code=200;body="encontrado=$found"} $found
  if (-not $found) { continue }
  $id = $row.($e.idf)
  $r = Call "GET" "$Api/$($e.find)$id" $null
  Reg (Id $p) "Consulta por id del $($e.n) creado" "GET" "/$($e.find)$id" "200 con id_$id" $r (($r.code -eq 200) -and ($r.body -match "`"$($e.idf)`":$id"))
  if (-not $e.val) { $e.val = "MODIFICADO" }; $row.($e.chg) = $e.val
  $r = Call "POST" "$Api/$($e.upd)" ($row | ConvertTo-Json)
  Reg (Id $p) "Actualizacion del $($e.n)" "POST" "/$($e.upd)" "200 y valor 1" $r (($r.code -eq 200) -and ($r.body.Trim() -eq "1"))
  $r = Call "GET" "$Api/$($e.find)$id" $null
  Reg (Id $p) "El cambio se refleja al consultar el $($e.n)" "GET" "/$($e.find)$id" "campo $($e.chg)=$($e.val)" $r ($r.body -match "$($e.val)")
  $r = Call "DELETE" "$Api/$($e.del)" ($row | ConvertTo-Json)
  Reg (Id $p) "Baja del $($e.n)" "DELETE" "/$($e.del)" "200 y valor 1" $r (($r.code -eq 200) -and ($r.body.Trim() -eq "1"))
  $r = Call "GET" "$Api/$($e.find)$id" $null
  Reg (Id $p) "El $($e.n) ya no existe tras la baja" "GET" "/$($e.find)$id" "entidad vacia (id null)" $r ($r.body -match "`"$($e.idf)`":null")
  # Casos de fallo
  $r = Call "DELETE" "$Api/$($e.del)" ($row | ConvertTo-Json)
  Reg (Id $p) "Baja de $($e.n) que YA no existe (se espera rechazo: 0 o 404)" "DELETE" "/$($e.del)" "0 o 404 (no debe reportar exito)" $r (($r.body.Trim() -eq "0") -or ($r.code -eq 404))
  $r = Call "POST" "$Api/$($e.add)" $null
  Reg (Id $p) "Alta de $($e.n) sin cuerpo" "POST" "/$($e.add)" "400" $r ($r.code -eq 400)
}

# ---- Datos invalidos: debe responder 400 (no 500)
$r = Call "POST" "$Api/addProyecto" '{"nombre":"ZZ_INVALIDO","fecha_inicio":"no-es-fecha"}'
Reg (Id "EP") "Alta de proyecto con fecha invalida" "POST" "/addProyecto" "400" $r ($r.code -eq 400)

# ---- Tareas por proyecto y comentarios por tarea
$r = Call "POST" "$Api/getTarea" '{"id_proyecto":197}'
Reg (Id "EP") "Tareas del proyecto 197" "POST" "/getTarea" "200 y arreglo JSON" $r (($r.code -eq 200) -and ($r.body.StartsWith("[")))
$r = Call "POST" "$Api/getTarea" '{"id_proyecto":999999}'
Reg (Id "EP") "Tareas de un proyecto inexistente" "POST" "/getTarea" "200 y arreglo vacio []" $r (($r.code -eq 200) -and ($r.body.Trim() -eq "[]"))
$r = Call "POST" "$Api/getComentario" '{"id_tarea":199}'
Reg (Id "EP") "Comentarios de la tarea 199" "POST" "/getComentario" "200 y arreglo JSON" $r (($r.code -eq 200) -and ($r.body.StartsWith("[")))

# ---- Integracion frontend (cliente 9080) -> backend (8081)
$n = 0
foreach ($ruta in @("/","/login","/register","/tutorial","/manual")) {
  $r = Call "GET" "$Cliente$ruta" $null
  Reg (Id "INT") "Cliente sirve la vista $ruta" "GET" $ruta "200" $r ($r.code -eq 200)
}
foreach ($ruta in @("/consultarProyectos","/consultarUsuarios","/consultarTareas","/consultarComentarios")) {
  $r = Call "POST" "$Cliente$ruta" "{}"
  Reg (Id "INT") "Cliente consume backend via $ruta" "POST" $ruta "200" $r ($r.code -eq 200)
}

$script:res | Export-Csv "$ev\endpoints-resultados.csv" -NoTypeInformation -Encoding UTF8
$md = @("| ID | Descripcion | Metodo | Ruta | Esperado | HTTP | Obtenido | Resultado |","|---|---|---|---|---|---|---|---|")
foreach ($x in $script:res) { $md += "| $($x.ID) | $($x.Descripcion) | $($x.Metodo) | $($x.Ruta) | $($x.Esperado) | $($x.HTTP) | $($x.Obtenido -replace '\|','/') | $($x.Resultado) |" }
$md | Out-File "$ev\endpoints-resultados.md" -Encoding UTF8
$pass = ($script:res | Where-Object Resultado -eq "PASS").Count
Write-Host "`nTOTAL: $($script:res.Count)  PASS: $pass  FAIL: $($script:res.Count - $pass)"
