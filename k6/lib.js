// Flujo comun de las pruebas: login (frontend), verificacion de credenciales y consulta de proyectos (backend).
import http from 'k6/http';
import { check, group, sleep } from 'k6';

export const FRONT = __ENV.FRONT_URL || 'https://gestor-iac-frontend.onrender.com';
export const BACK = __ENV.BACK_URL || 'https://gestor-iac-backend.onrender.com';
const CORREO = __ENV.CORREO || 'claudio22540@cbtis75.edu.mx';
const PASS = __ENV.PASS || 'claudio26';
const json = { headers: { 'Content-Type': 'application/json' } };

export function flujo() {
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
