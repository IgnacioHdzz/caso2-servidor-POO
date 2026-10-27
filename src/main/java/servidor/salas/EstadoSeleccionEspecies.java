package servidor.salas;

import comun.modelo.Especie;
import comun.protocolo.Mensaje;
import comun.protocolo.TipoMensaje;
import comun.protocolo.datos.DatosAviso;
import servidor.constantes.Constantes;
import servidor.red.SesionCliente;

// Estado en donde los jugadores están eligiendo especie.
public class EstadoSeleccionEspecies extends EstadoSala {

    @Override
    public void unirJugador(Sala pSala, SesionCliente pSesion) {
        enviarError(pSesion, Constantes.TEXTO_SALA_LLENA);
    }

    @Override
    public void seleccionarEspecie(Sala pSala, SesionCliente pSesion, Especie pEspecie) {
        // La especie viene del cliente, puede venir vacía si el mensaje traía un nombre que no existe.
        if (pEspecie == null || !pSala.registrarEspecie(pSesion, pEspecie)) {
            enviarError(pSesion, Constantes.TEXTO_ESPECIE_TOMADA);
            return;
        }
        pSala.enviarSeleccionEspecies();
        if (pSala.ambosEligieron()) {
            pSala.cambiarEstado(new EstadoJugando());
            pSala.iniciarPartida();
        }
    }

    @Override
    public void alDesconectarse(Sala pSala, SesionCliente pSesion) {
        SesionCliente rival = pSala.getRival(pSesion);
        if (rival != null) {
            rival.enviar(new Mensaje(TipoMensaje.OPPONENT_LEFT, new DatosAviso(Constantes.TEXTO_RIVAL_ABANDONO)));
        }
        pSala.cerrar();
    }
}