package servidor.procesadores;

import comun.protocolo.Mensaje;
import comun.protocolo.TipoMensaje;
import comun.protocolo.datos.DatosCrearPartida;
import comun.protocolo.datos.DatosPartidaCreada;
import servidor.constantes.Constantes;
import servidor.red.SesionCliente;
import servidor.salas.GestorSalas;
import servidor.salas.Sala;

// CREATE_MATCH crea una sala nueva con este cliente adentro.
public class ProcesadorCrearPartida implements IProcesadorMensaje {

    @Override
    public void procesar(Mensaje pMensaje, SesionCliente pSesion) {
        if (pSesion.getSala() != null) {
            pSesion.enviarError(Constantes.TEXTO_ACCION_NO_PERMITIDA);
            return;
        }
        DatosCrearPartida datos = pMensaje.getDatos(DatosCrearPartida.class);
        pSesion.setNickname(datos.getNickname());
        Sala sala = GestorSalas.getInstancia().crearSala(pSesion);
        pSesion.enviar(new Mensaje(TipoMensaje.MATCH_CREATED, new DatosPartidaCreada(sala.getCodigo())));
    }
}