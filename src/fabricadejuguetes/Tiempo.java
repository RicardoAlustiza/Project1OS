/*
 * Tiempo.java
 * Contiene la definicion de la clase Tiempo, que se encarga de mantener la cuenta
 * de los dias transcurridos en la produccion de los diferentes productos en la
 * fabrica.
 * 
 */
package fabricadejuguetes;

import fabricadejuguetes.GUI.FabricaGUI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ivan Arno y Ricardo Alustiza
 */
public class Tiempo extends Thread {
    long tiempo;            // tiempo actual en horas desde que se creo esta instancia
    int milisegundosPorHora;    // tiempo en milisegundos que equivalen a una hora en la simulacion
    FabricaGUI gui;             // gui de la fabrica, usada para enviar actualizaciones cada hora simulada
    
    public Tiempo(int msPorHora,FabricaGUI gui)
    {
        tiempo=0;       // el tiempo siempre inicia en 0
        this.milisegundosPorHora=msPorHora; 
        this.gui=gui;
    }
    @Override
    public void run() {
        for (;;) {
            try {
                this.sleep(milisegundosPorHora);    // espera una hora
            } catch (InterruptedException ex) {
                Logger.getLogger(Productor.class.getName()).log(Level.SEVERE, null, ex);
            }
            tiempo++;   // incrementa una hora simulada
            gui.actualizaTiempo();  // actualiza la GUI
        }
    }
    // obtiene la hora actual desde que se creo la instancia actual
    public long hora() {
        return tiempo;
    }
}
