package servidor.procesadores;

import comun.protocolo.Mensaje;
import comun.protocolo.datos.DatosAtacar;
import servidor.juego.comandos.ComandoAtacar;
import servidor.red.SesionCliente;
import servidor.salas.Sala;

// ATTACK convierte el mensaje en un ComandoAtacar. Tampoco ataca acá, igual que el de mover animales. 
public class ProcesadorAtacar extends ProcesadorEnSala {

    @Override
    protected void procesarEnSala(Mensaje pMensaje, SesionCliente pSesion, Sala pSala) {
        DatosAtacar datos = pMensaje.getDatos(DatosAtacar.class);
        pSala.recibirComando(new ComandoAtacar(pSesion, datos.getIdsAnimales(), datos.getIdObjetivo()));
    }
}