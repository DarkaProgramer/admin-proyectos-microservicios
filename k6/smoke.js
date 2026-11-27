// Prueba de humo (smoke): 1 usuario durante 30 s. Comprueba que el sistema responde antes de cargarlo.
import { flujo } from './lib.js';

export const options = {
  vus: 1,
  duration: '30s',
  thresholds: { http_req_failed: ['rate<0.01'], http_req_duration: ['p(95)<2000'] },
};
export default function () { flujo(); }
