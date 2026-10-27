package servidor.procesadores;

import comun.protocolo.Mensaje;
import comun.protocolo.datos.DatosUnirsePartida;
import servidor.constantes.Constantes;
import servidor.red.SesionCliente;
import servidor.salas.GestorSalas;
import servidor.salas.Sala;

// JOIN_MATCH une al cliente a una sala existente. 
public class ProcesadorUnirsePartida implements IProcesadorMensaje {

    @Override
    public void procesar(Mensaje pMensaje, SesionCliente pSesion) {
        if (pSesion.getSala() != null) {    // si ya está en una sala, no puede unirse a otra.
            pSesion.enviarError(Constantes.TEXTO_ACCION_NO_PERMITIDA);
            return;
        }
        DatosUnirsePartida datos = pMensaje.getDatos(DatosUnirsePartida.class);
        Sala sala = GestorSalas.getInstancia().buscarSala(datos.getCodigo());
        if (sala == null) {
            pSesion.enviarError(Constantes.TEXTO_CODIGO_INEXISTENTE);
            return;
        }
        pSesion.setNickname(datos.getNickname());
        sala.unirJugador(pSesion);
    }
}