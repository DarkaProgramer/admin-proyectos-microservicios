// Prueba de carga (load): carga normal esperada. Sube a 10 usuarios, se mantiene y baja.
import { flujo } from './lib.js';

export const options = {
  stages: [
    { duration: '20s', target: 10 },
    { duration: '60s', target: 10 },
    { duration: '10s', target: 0 },
  ],
  thresholds: { http_req_failed: ['rate<0.01'], http_req_duration: ['p(95)<1000'] },
};
export default function () { flujo(); }
