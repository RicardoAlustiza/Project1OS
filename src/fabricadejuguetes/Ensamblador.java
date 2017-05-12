/*
 * Ensamblador.java
 * Contiene la definicion de la clase de Ensamblador, que representa un ensamblador
 * en la fabrica.
 * 
 */
package fabricadejuguetes;

import fabricadejuguetes.GUI.FabricaGUI;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ivan Arno y Ricardo Alustiza
 */
public class Ensamblador extends Productor {
    private Almacen almacenRuedas;
    private Almacen almacenMarcos;
    int ruedasNecesarias;
    int marcosNecesarios;
    int horasEnsamble;
    FabricaGUI gui;
    Semaphore sPr;
    Semaphore sCr;
    Semaphore sEr;
    
    Semaphore sPm;
    Semaphore sCm;
    Semaphore sEm;   
   
    public Ensamblador(Almacen almacen,int ruedasNecesarias,Almacen almacenRuedas,int marcosNecesarios,
                        Almacen almacenMarcos,int horasEnsamble,Tiempo tiempo,FabricaGUI gui,
                        Semaphore sP,Semaphore sE,
                        Semaphore sPr,Semaphore sCr,Semaphore sEr,
                        Semaphore sPm,Semaphore sCm,Semaphore sEm) {
        super(almacen,horasEnsamble,tiempo,sP,null,sE);
        this.almacenRuedas=almacenRuedas;
        this.almacenMarcos=almacenMarcos;
        this.ruedasNecesarias = ruedasNecesarias;
        this.marcosNecesarios = marcosNecesarios;
        this.horasEnsamble = horasEnsamble;
        this.gui=gui;
        this.sPr=sPr;
        this.sCr=sCr;
        this.sEr=sEr;
        this.sPm=sPm;
        this.sCm=sCm;
        this.sEm=sEm;
    }
    @Override
    public void run()
    {      
        while (estado!=2) {  // mientras que el hilo este corriendo o pausado
            try {
                if(estado==1){
                    this.semHilo.acquire();                    
                }                
                
                sCr.acquire(ruedasNecesarias);
                sEr.acquire();
                for(int i=0; i<ruedasNecesarias && estado!=2; i++)   // obtiene las ruedas necesarias del almacen de ruedas
                    almacenRuedas.saca();
                sEr.release();
                sPr.release(ruedasNecesarias);
                
                sCm.acquire(marcosNecesarios);
                sEm.acquire();
                for(int i=0; i<marcosNecesarios && estado!=2; i++)   // obtiene los marcos necesarios del almacen de marcos
                    almacenMarcos.saca();
                sEm.release();
                sPm.release(marcosNecesarios);
                               
                long horaInicial=tiempo.hora();
                while (tiempo.hora() - horaInicial<horasProduccion
                        && estado!=2) { // ciclo por el tiempo de produccion
                    this.sleep(10); 
                }
                if(estado==0) { // si el hilo esta corriendo
                    sP.acquire();
                    sE.acquire();
                    almacen.guarda();       // al terminar la produccion, guarda el producto en almacen
                    sE.release();
                    //sC.release();

                    if(almacen.lleno()) {   // al tener almacen lleno, se ha llenado la demanda, avisar al GUI
                        gui.almacenLleno(almacen);
                        break;
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Ensamblador.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }                
    }
}
