/*
 * FabricaDeJuguetes.java
 * Contiene la definicion de la clase de FabricaDeJuguetes, que a su vez contiene
 * toda la informacion pertinente acerca de la fabrica: productores, ensambladores
 * y almacenes.
 *
 */
package fabricadejuguetes;

import fabricadejuguetes.GUI.FabricaGUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Ivan Arno y Ricardo Alustiza
 */
public class FabricaDeJuguetes {   
    int milisegundosPorDia=3000;

    public int nBicicletas = 20;
    public int nTriciclos = 10;

    int maxNeumaticosEnAlmacen=10;
    int maxBujesEnAlmacen=10;
    int maxRuedasEnAlmacen=16;
    int maxMarcosEnAlmacen=15;

    int nProdNeumaticos=2;
    int nProdBujes=3;
    int nProdRuedas=3;
    int nProdMarcos=1;

    int maxProdNeumaticos=5;
    int maxProdBujes=5;
    int maxProdRuedas=6;
    int maxProdMarcos=8;

    int nEnsamBicicletas=2;
    int nEnsamTriciclos=3;

    int maxEnsamBicicletas=3;
    int maxEnsamTriciclos=3;    
    
    public Tiempo    tiempo;

    public Almacen   almacenNeumaticos;
    public Almacen   almacenBujes;
    public Almacen   almacenRuedas;
    public Almacen   almacenMarcos;

    public ArrayList<Productor> prodNeumaticos;
    public ArrayList<Productor> prodBujes;
    public ArrayList<ProductorRuedas> prodRuedas;
    public ArrayList<Productor> prodMarcos;
    
    public ArrayList<Ensamblador> ensamBicicletas;
    public ArrayList<Ensamblador> ensamTriciclos;
    
    public Almacen almacenBicicletas;
    public Almacen almacenTriciclos;

    // semáforos para el productor de bujes
    private Semaphore semaProductoresB;
    private Semaphore semaConsumidoresB = new Semaphore(0);
    private Semaphore semaExclusividadB =new Semaphore(1);

    // semáforos para el productor de neumaticos
    private Semaphore semaProductoresN;
    private Semaphore semaConsumidoresN = new Semaphore(0);
    private Semaphore semaExclusividadN =new Semaphore(1);

    // semáforos para el productor de ruedas
    private Semaphore semaProductoresR;
    private Semaphore semaConsumidoresR = new Semaphore(0);
    private Semaphore semaExclusividadR =new Semaphore(1);
    
    // semáforos para el productor de marcos
    private Semaphore semaProductoresM;
    private Semaphore semaConsumidoresM = new Semaphore(0);
    private Semaphore semaExclusividadM =new Semaphore(1);
       
    // semáforos para el productor de bicicletas
    private Semaphore semaProductoresBici;
    private Semaphore semaExclusividadBici =new Semaphore(1);

    // semáforos para el productor de triciclos
    private Semaphore semaProductoresTrici;
    private Semaphore semaExclusividadTrici =new Semaphore(1);
    
    public FabricaDeJuguetes(FabricaGUI gui) {        

        cargaDatos();
        
        tiempo = new Tiempo(milisegundosPorDia/24,gui);
        
        almacenNeumaticos=new Almacen(maxNeumaticosEnAlmacen);
        almacenBujes=new Almacen(maxBujesEnAlmacen);
        almacenRuedas=new Almacen(maxRuedasEnAlmacen);
        almacenMarcos=new Almacen(maxMarcosEnAlmacen);

        almacenBicicletas = new Almacen(nBicicletas);
        almacenTriciclos  = new Almacen(nTriciclos);

        semaProductoresB = new Semaphore(maxBujesEnAlmacen);        
        semaProductoresN = new Semaphore(maxNeumaticosEnAlmacen);
        semaProductoresR = new Semaphore(maxRuedasEnAlmacen);        
        semaProductoresM = new Semaphore(maxMarcosEnAlmacen);
        semaProductoresBici = new Semaphore(nBicicletas);
        semaProductoresTrici = new Semaphore(nTriciclos);       
        
        prodNeumaticos = new ArrayList<>(nProdNeumaticos);
        for(int i=0; i<nProdNeumaticos; i++)
            prodNeumaticos.add(new Productor(almacenNeumaticos,24,tiempo,semaProductoresN,semaConsumidoresN,semaExclusividadN));
        prodBujes = new ArrayList<>(nProdBujes);
        for(int i=0; i<nProdBujes; i++)
            prodBujes.add(new Productor(almacenBujes,24,tiempo,semaProductoresB,semaConsumidoresB,semaExclusividadB));
        prodRuedas = new ArrayList<>(nProdRuedas);
        for(int i=0; i<nProdRuedas; i++)
            prodRuedas.add(new ProductorRuedas(almacenRuedas,almacenNeumaticos,almacenBujes,12,tiempo,
                    semaProductoresR,semaConsumidoresR,semaExclusividadR,
                    semaProductoresB,semaConsumidoresB,semaExclusividadB,
                    semaProductoresN,semaConsumidoresN,semaExclusividadN));
        prodMarcos = new ArrayList<>(nProdMarcos);
        for(int i=0; i<nProdMarcos; i++)
            prodMarcos.add(new Productor(almacenMarcos,48,tiempo,semaProductoresM,semaConsumidoresM,semaExclusividadM));
        
        ensamBicicletas=new ArrayList<>(nEnsamBicicletas);
        for(int i=0; i<nEnsamBicicletas; i++)
            ensamBicicletas.add(nuevoEnsambladorBicicletas(gui));
        ensamTriciclos=new ArrayList<>(nEnsamTriciclos);
        for(int i=0; i<nEnsamTriciclos; i++)
            ensamTriciclos.add(nuevoEnsambladorTriciclos(gui));
        
    }
    private Ensamblador nuevoEnsambladorBicicletas(FabricaGUI gui) {
        return new Ensamblador(almacenBicicletas,2,almacenRuedas,1,almacenMarcos,48,tiempo,gui,
                    semaProductoresBici,semaExclusividadBici,
                    semaProductoresR,semaConsumidoresR,semaExclusividadR,
                    semaProductoresM,semaConsumidoresM,semaExclusividadM);
    }
    private Ensamblador nuevoEnsambladorTriciclos(FabricaGUI gui) {
        return new Ensamblador(almacenTriciclos,3,almacenRuedas,1,almacenMarcos,72,tiempo,gui,
                    semaProductoresTrici,semaExclusividadTrici,
                    semaProductoresR,semaConsumidoresR,semaExclusividadR,
                    semaProductoresM,semaConsumidoresM,semaExclusividadM);
    }
    // carga los datos (cantidades y maximos) iniciales desde el archivo "datos.txt"
    private void cargaDatos() {
        try {
            Scanner s = new Scanner(new File("datos.txt"));
            while (s.hasNext()){
                String line=s.nextLine();
                String [] parte=line.split("=");
                if(parte.length==2) {
                    if(parte[0].startsWith("segundosPorDia"))
                        milisegundosPorDia=Integer.parseInt(parte[1])*1000;
                    else if(parte[0].startsWith("numBicicletas"))
                        nBicicletas=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("numTriciclos"))
                        nTriciclos=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("maxNeumaticosEnAlmacen"))
                        maxNeumaticosEnAlmacen=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("maxBujesEnAlmacen"))
                        maxBujesEnAlmacen=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("maxRuedasEnAlmacen"))
                        maxRuedasEnAlmacen=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("maxMarcosEnAlmacen"))
                        maxMarcosEnAlmacen=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("numProdNeumaticos"))
                        nProdNeumaticos=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("numProdBujes"))
                        nProdBujes=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("numProdRuedas"))
                        nProdRuedas=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("numProdMarcos"))
                        nProdMarcos=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("maxProdNeumaticos"))
                        maxProdNeumaticos=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("maxProdBujes"))
                        maxProdBujes=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("maxProdRuedas"))
                        maxProdRuedas=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("maxProdMarcos"))
                        maxProdMarcos=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("numEnsamBicicletas"))
                        nEnsamBicicletas=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("numEnsamTriciclos"))
                        nEnsamTriciclos=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("maxEnsamBicicletas"))
                        maxEnsamBicicletas=Integer.parseInt(parte[1]);
                    else if(parte[0].startsWith("maxEnsamTriciclos"))
                        maxEnsamTriciclos=Integer.parseInt(parte[1]);                
                }                        
            }
            s.close();		
        } catch (FileNotFoundException | NumberFormatException e) {
            System.out.println("Error cargando archivo!");
        }
    }
    
    // inicia todos los hilos para empezar la produccion de los productos
    public void iniciaProduccion() {
        tiempo.start();
        
        for(int i=0; i<prodNeumaticos.size(); i++)
            prodNeumaticos.get(i).start();        
        for(int i=0; i<prodBujes.size(); i++)
            prodBujes.get(i).start();
        for(int i=0; i<prodMarcos.size(); i++)
            prodMarcos.get(i).start();
        
        for(int i=0; i<prodRuedas.size(); i++)
            prodRuedas.get(i).start();
        
        for(int i=0; i<ensamBicicletas.size(); i++)
            ensamBicicletas.get(i).start();
        for(int i=0; i<ensamTriciclos.size(); i++)
            ensamTriciclos.get(i).start();       
    }
    
    public void agregaProductorNeumaticos() {
        if(prodNeumaticos.size()<maxProdNeumaticos) {   // si aun no llegamos al maximo de productores
            prodNeumaticos.add(new Productor(almacenNeumaticos,24,tiempo,semaProductoresN,semaConsumidoresN,semaExclusividadN));  // contrata nuevo productor
            prodNeumaticos.get(prodNeumaticos.size()-1).start();    // inicia su produccion
        }        
    }

    public void agregaProductorBujes() {
        if(prodBujes.size()<maxProdBujes) { // si aun no llegamos al maximo de productores
            prodBujes.add(new Productor(almacenBujes,24,tiempo,semaProductoresB,semaConsumidoresB,semaExclusividadB));            // contrata nuevo productor
            prodBujes.get(prodBujes.size()-1).start();    // inicia su produccion
        }
    }
    public void agregaProductorRuedas() {
        if(prodRuedas.size()<maxProdRuedas) { // si aun no llegamos al maximo de productores
            prodRuedas.add(new ProductorRuedas(almacenRuedas,almacenNeumaticos,almacenBujes,12,tiempo,
                    semaProductoresR,semaConsumidoresR,semaExclusividadR,
                    semaProductoresB,semaConsumidoresB,semaExclusividadB,
                    semaProductoresN,semaConsumidoresN,semaExclusividadN)); // contrata nuevo productor
            prodRuedas.get(prodRuedas.size()-1).start();    // inicia su produccion
        }        
    }
    public void agregaProductorMarcos() {
        if(prodMarcos.size()<maxProdMarcos) { // si aun no llegamos al maximo de productores
            prodMarcos.add(new Productor(almacenMarcos,48,tiempo,semaProductoresM,semaConsumidoresM,semaExclusividadM));            // contrata nuevo productor
            prodMarcos.get(prodMarcos.size()-1).start();    // inicia su produccion
        }        
    }
    public void agregaEnsambladorBicicletas(FabricaGUI gui) {
        if(ensamBicicletas.size()<maxEnsamBicicletas) { // si aun no llegamos al maximo de productores
            ensamBicicletas.add(nuevoEnsambladorBicicletas(gui)); // contrata nuevo ensamblador
            ensamBicicletas.get(ensamBicicletas.size()-1).start();    // inicia su produccion
        }        
    }
    public void agregaEnsambladorTriciclos(FabricaGUI gui) {
        if(ensamTriciclos.size()<maxEnsamTriciclos) { // si aun no llegamos al maximo de productores
            ensamTriciclos.add(nuevoEnsambladorTriciclos(gui)); // contrata nuevo ensamblador
            ensamTriciclos.get(ensamTriciclos.size()-1).start();    // inicia su produccion
        }        
    }
    public void eliminaProductorNeumaticos() {
        if(!prodNeumaticos.isEmpty()) { // si hay productores disponibles en la fabrica
            prodNeumaticos.get(0).detiene();
            prodNeumaticos.remove(0);   // elimina productor de la fabrica
        }        
    }
    public void eliminaProductorBujes() {
        if(!prodBujes.isEmpty()) { // si hay productores disponibles en la fabrica
            prodBujes.get(0).detiene();
            prodBujes.remove(0); // elimina productor de la fabrica
        }       
    }
    public void eliminaProductorRuedas() {
        if(!prodRuedas.isEmpty()) { // si hay productores disponibles en la fabrica
            prodRuedas.get(0).detiene(); 
            prodRuedas.remove(0); // elimina productor de la fabrica
        }       
    }
    public void eliminaProductorMarcos() {
        if(!prodMarcos.isEmpty()) { // si hay productores disponibles en la fabrica
            prodMarcos.get(0).detiene(); 
            prodMarcos.remove(0); // elimina productor de la fabrica
        }        
    }
    public void eliminaEnsambladorBicicletas() {
        if(!ensamBicicletas.isEmpty()) { // si hay ensambladores disponibles en la fabrica
            ensamBicicletas.get(0).detiene();
            ensamBicicletas.remove(0); // elimina ensamblador de la fabrica
        }      
    }
    public void eliminaEnsambladorTriciclos() {
        if(!ensamTriciclos.isEmpty()) { // si hay ensambladores disponibles en la fabrica
            ensamTriciclos.get(0).detiene(); 
            ensamTriciclos.remove(0); // elimina ensamblador de la fabrica
        }        
    }
    public void pausaProduccionNeumaticos() {
        for(int i=0; i<prodNeumaticos.size(); i++)  // pausa todos los hilos de productores
            prodNeumaticos.get(i).pausa();
        
    }
    public void pausaProduccionBujes() {
        for(int i=0; i<prodBujes.size(); i++) // pausa todos los hilos de productores
            prodBujes.get(i).pausa();
        
    }
    public void pausaProduccionRuedas() {
        for(int i=0; i<prodRuedas.size(); i++) // pausa todos los hilos de productores
            prodRuedas.get(i).pausa();
        
    }
    public void pausaProduccionMarcos() {
        for(int i=0; i<prodMarcos.size(); i++) // pausa todos los hilos de productores
            prodMarcos.get(i).pausa();
        
    }
    public void pausaProduccionBicicletas() {
        for(int i=0; i<ensamBicicletas.size(); i++) // pausa todos los hilos de ensambladores
            ensamBicicletas.get(i).pausa();
        
    }
    public void pausaProduccionTriciclos() {
        for(int i=0; i<ensamTriciclos.size(); i++) // pausa todos los hilos de ensambladores
            ensamTriciclos.get(i).pausa();
        
    }
    public void reanudaProduccionNeumaticos() {
        for(int i=0; i<prodNeumaticos.size(); i++) // reanuda todos los hilos de productores
            prodNeumaticos.get(i).reanuda();
        
    }
    public void reanudaProduccionBujes() {
        for(int i=0; i<prodBujes.size(); i++) // reanuda todos los hilos de productores
            prodBujes.get(i).reanuda();  // restaura el color de fondo para indicar que esta corriendo
        
    }
    public void reanudaProduccionRuedas() {
        for(int i=0; i<prodRuedas.size(); i++) // reanuda todos los hilos de productores
            prodRuedas.get(i).reanuda();
        
    }
    public void reanudaProduccionMarcos() {
        for(int i=0; i<prodMarcos.size(); i++) // reanuda todos los hilos de productores
            prodMarcos.get(i).reanuda();
        
    }
    public void reanudaProduccionBicicletas() {
        for(int i=0; i<ensamBicicletas.size(); i++) // reanuda todos los hilos de ensambladores
            ensamBicicletas.get(i).reanuda();
        
    }
    public void reanudaProduccionTriciclos() {
        for(int i=0; i<ensamTriciclos.size(); i++) // reanuda todos los hilos de ensambladores
            ensamTriciclos.get(i).reanuda();
        
    }

    public void mueveEnsambladoresBicicletasATriciclos(FabricaGUI gui) {
        if (!almacenTriciclos.lleno()) {    // si aun falta producir triciclos, cambiamos a todos los ensambladores de bicicletas para producir triciclos
            for (int i=0; i<ensamBicicletas.size(); i++)    // crea nuevos productores de triciclos, tantos como haya de bicicletas
                ensamTriciclos.add(nuevoEnsambladorTriciclos(gui));
            ensamBicicletas.removeAll(ensamBicicletas); // retira todos los productores de bicicletas
        }       
    }
    public void mueveEnsambladoresTriciclosABicicletas(FabricaGUI gui) {
        if (!almacenBicicletas.lleno()) { // si aun falta producir bicicletas, cambiamos a todos los ensambladores de triciclos para producir bicicletas
            for (int i=0; i<ensamTriciclos.size(); i++) // crea nuevos productores de bicicletas, tantos como haya de triciclos
                ensamBicicletas.add(nuevoEnsambladorBicicletas(gui));
            ensamTriciclos.removeAll(ensamTriciclos);   // retira todos los productores de triciclos
        }
    }
}
