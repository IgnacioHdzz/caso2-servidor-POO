package servidor.juego;

import comun.modelo.Especie;
import comun.protocolo.Mensaje;
import servidor.modelo.Manada;
import servidor.red.SesionCliente;

/** Un jugador dentro del juego: su manada, la zona que está viendo y sus tazas. */
public class Jugador {

    private final SesionCliente sesion;
    private final Especie especie;
    private Manada manada;
    private int idZonaVisible;
    private int tazasConsumidas;

    public Jugador(SesionCliente pSesion, Especie pEspecie) {
        this.sesion = pSesion;
        this.especie = pEspecie;
    }

    public void enviar(Mensaje pMensaje) {
        sesion.enviar(pMensaje);
    }

    public void registrarTaza() {
        tazasConsumidas++;
    }

    public void verZona(int pIdZona) {
        this.idZonaVisible = pIdZona;
    }

    public void asignarManada(Manada pManada) {
        this.manada = pManada;
    }

    public String getNickname() {
        return sesion.getNickname();
    }

    public SesionCliente getSesion() {
        return sesion;
    }

    public Especie getEspecie() {
        return especie;
    }

    public Manada getManada() {
        return manada;
    }

    public int getIdZonaVisible() {
        return idZonaVisible;
    }

    public int getTazasConsumidas() {
        return tazasConsumidas;
    }
}
