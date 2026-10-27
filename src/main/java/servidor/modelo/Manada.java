package servidor.modelo;

import java.util.ArrayList;
import java.util.List;
import comun.modelo.Especie;
import servidor.juego.Jugador;
import servidor.constantes.Constantes;

// Un grupo de animales de la misma especie. El dano es compartido.
public class Manada {

    private final int id;
    private final Especie especie;
    private final boolean esBot;
    private final Jugador duenio;
    private final List<Animal> miembros;
    private int capacidadDanio;
    private int botsEliminados;

    // pDuenio es null si la manada es bot.
    public Manada(int pId, Especie pEspecie, boolean pEsBot, int pDanioInicial, Jugador pDuenio) {
        this.id = pId;
        this.especie = pEspecie;
        this.esBot = pEsBot;
        this.capacidadDanio = pDanioInicial;
        this.duenio = pDuenio;
        this.miembros = new ArrayList<>();
    }

    public void agregarMiembro(Animal pAnimal) {
        miembros.add(pAnimal);
    }

    public void aumentarDanio(int pCantidad) {
        capacidadDanio += pCantidad;
    }

    public void registrarBotEliminado() {
        botsEliminados++;
        if (botsEliminados % Constantes.BOTS_PARA_BONO == 0) {
            for (Animal animal : miembros) {
                if (animal.estaVivo()) {
                    animal.curar(Constantes.BONO_ENERGIA, Constantes.ENERGIA_MAXIMA_JUGADOR);
                }
            }
        }
    }

    public int contarVivos() {
        int cantidad = 0;
        for (Animal animal : miembros) {
            if (animal.estaVivo()) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public int contarEliminados() {
        return miembros.size() - contarVivos();
    }

    public int getId() {
        return id;
    }

    public Especie getEspecie() {
        return especie;
    }

    public boolean esBot() {
        return esBot;
    }

    public Jugador getDuenio() {
        return duenio;
    }

    public List<Animal> getMiembros() {
        return miembros;
    }

    public int getCapacidadDanio() {
        return capacidadDanio;
    }

    public int getBotsEliminados() {
        return botsEliminados;
    }
}
