package servidor.procesadores;

import comun.protocolo.Mensaje;
import comun.protocolo.datos.DatosVerZona;
import servidor.juego.comandos.ComandoVerZona;
import servidor.red.SesionCliente;
import servidor.salas.Sala;

// VIEW_ZONE convierte el mensaje en un ComandoVerZona.
public class ProcesadorVerZona extends ProcesadorEnSala {

    @Override
    protected void procesarEnSala(Mensaje pMensaje, SesionCliente pSesion, Sala pSala) {
        DatosVerZona datos = pMensaje.getDatos(DatosVerZona.class);
        pSala.recibirComando(new ComandoVerZona(pSesion, datos.getIdZona()));
    }
}