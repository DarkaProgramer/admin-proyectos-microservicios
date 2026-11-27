// Prueba de estres (stress): sube la carga por escalones hasta 40 usuarios (limite controlado, entorno propio)
// para encontrar el punto donde el servicio se degrada, y luego baja para ver si se recupera.
import { flujo } from './lib.js';

export const options = {
  stages: [
    { duration: '30s', target: 10 },
    { duration: '30s', target: 20 },
    { duration: '30s', target: 30 },
    { duration: '30s', target: 40 },
    { duration: '30s', target: 0 },
  ],
  thresholds: { http_req_failed: ['rate<0.10'], http_req_duration: ['p(95)<3000'] },
};
export default function () { flujo(); }
