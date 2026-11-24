// Prueba de carga controlada (solo contra el entorno propio en Docker local).
// Escenario: sube a 10 usuarios virtuales, se mantiene 60 s y baja.
import http from 'k6/http';
import { check, group, sleep } from 'k6';

const FRONT = __ENV.FRONT_URL || 'http://localhost:9080';
const BACK = __ENV.BACK_URL || 'http://localhost:8081';
const CORREO = __ENV.CORREO || 'claudio@gmail.com';
const PASS = __ENV.PASS || '123';

export const options = {
  stages: [
    { duration: '20s', target: 10 },
    { duration: '60s', target: 10 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],      // menos de 1% de errores
    http_req_duration: ['p(95)<1000'],   // p95 menor a 1 segundo
  },
};

const json = { headers: { 'Content-Type': 'application/json' } };

export default function () {
  group('frontend: pagina de login', () => {
    const r = http.get(`${FRONT}/login`);
    check(r, { 'login 200': (x) => x.status === 200 });
  });
  group('frontend: verificar login', () => {
    const r = http.post(`${FRONT}/verificarLogin`,
      JSON.stringify({ correo_electronico: CORREO, contrasena: PASS }), json);
    check(r, { 'verificarLogin 200': (x) => x.status === 200 });
  });
  group('backend: buscar proyectos', () => {
    const r = http.post(`${BACK}/buscarProyectos`, null, json);
    check(r, { 'buscarProyectos 200': (x) => x.status === 200 });
  });
  sleep(1);
}
