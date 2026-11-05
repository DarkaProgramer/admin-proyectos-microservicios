/**
 * Pruebas automaticas de la validacion del formulario de login.
 * Archivo probado: src/main/resources/static/js/login.js (funcion validarLogin)
 *
 * AUT-E06  Formulario con datos validos  -> se envia la peticion al backend
 * AUT-F04  Ambos campos vacios           -> aviso 1, no se envia
 * AUT-F05  Correo vacio                  -> aviso 2, no se envia
 * AUT-F06  Correo con formato invalido   -> aviso 3, no se envia
 * AUT-F07  Contrasena vacia              -> aviso 4, no se envia
 * AUT-F08  Backend responde usuario inexistente (nombre "000") -> aviso 5, sin sesion
 */
const fs = require('fs');
const path = require('path');

const JS_DIR = path.join(__dirname, '..', 'src', 'main', 'resources', 'static', 'js');
const HTML_TOASTS = ['alerta1', 'alerta2', 'alerta3', 'alerta4', 'alerta5']
  .map(id => `<div id="${id}" class="toast"></div>`).join('');

let toastsMostrados;

beforeEach(() => {
  // DOM minimo equivalente a login.html
  document.body.innerHTML = `
    ${HTML_TOASTS}
    <form>
      <input type="email" id="exampleInputEmail1">
      <input type="password" id="exampleInputPassword1">
    </form>`;
  localStorage.clear();

  // jQuery real del proyecto
  window.eval(fs.readFileSync(path.join(JS_DIR, 'jquery-3.7.1.min.js'), 'utf8'));

  // Doble de bootstrap.Toast: registra que aviso se mostro
  toastsMostrados = [];
  window.bootstrap = {
    Toast: function (el) {
      this.show = () => toastsMostrados.push(el.id);
    }
  };

  // Codigo real del proyecto
  window.eval(fs.readFileSync(path.join(JS_DIR, 'login.js'), 'utf8'));

  // Espia de $.ajax (no hay backend: la prueba es repetible y no toca la BD)
  jest.spyOn(window.$, 'ajax').mockImplementation(() => {});
});

function llenar(email, pass) {
  document.getElementById('exampleInputEmail1').value = email;
  document.getElementById('exampleInputPassword1').value = pass;
}

describe('Validacion del formulario de login (login.js)', () => {

  test('AUT-E06 | Formulario | datos validos: se envia POST /verificarLogin con correo y contrasena', () => {
    llenar('perla22522@cbtis75.edu.mx', '12345');
    window.validarLogin();

    expect(toastsMostrados).toEqual([]);
    expect(window.$.ajax).toHaveBeenCalledTimes(1);
    const peticion = window.$.ajax.mock.calls[0][0];
    expect(peticion.url).toBe('/verificarLogin');
    expect(peticion.type).toBe('POST');
    expect(JSON.parse(peticion.data)).toEqual({
      correo_electronico: 'perla22522@cbtis75.edu.mx',
      contrasena: '12345'
    });
  });

  test('AUT-F04 | Formulario | ambos campos vacios: aviso 1 y no se envia', () => {
    llenar('', '');
    window.validarLogin();

    expect(toastsMostrados).toEqual(['alerta1']);
    expect(window.$.ajax).not.toHaveBeenCalled();
  });

  test('AUT-F05 | Formulario | correo vacio: aviso 2 y no se envia', () => {
    llenar('', '12345');
    window.validarLogin();

    expect(toastsMostrados).toEqual(['alerta2']);
    expect(window.$.ajax).not.toHaveBeenCalled();
  });

  test('AUT-F06 | Formulario | correo con formato invalido: aviso 3 y no se envia', () => {
    llenar('correo-invalido', '12345');
    window.validarLogin();

    expect(toastsMostrados).toEqual(['alerta3']);
    expect(window.$.ajax).not.toHaveBeenCalled();
  });

  test('AUT-F07 | Formulario | contrasena vacia: aviso 4 y no se envia', () => {
    llenar('perla22522@cbtis75.edu.mx', '');
    window.validarLogin();

    expect(toastsMostrados).toEqual(['alerta4']);
    expect(window.$.ajax).not.toHaveBeenCalled();
  });

  test('AUT-F08 | Vista | backend responde nombre "000" (usuario inexistente): aviso 5 y sin sesion', () => {
    window.$.ajax.mockImplementation(opciones => opciones.success({ nombre: '000' }));
    llenar('perla22522@cbtis75.edu.mx', 'incorrecta999');
    window.validarLogin();

    expect(toastsMostrados).toEqual(['alerta5']);
    expect(localStorage.getItem('usuario')).toBeNull();
  });
});