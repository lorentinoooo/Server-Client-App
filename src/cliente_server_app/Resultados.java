/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente_server_app;


/**
 *
 * @author davidlorente
 */
public class Resultados {
    private double ts_tx, ts_rx; 
    private int id;
    
    public Resultados(int id, double ts_tx, double ts_rx) {
        this.id = id;
        this.ts_tx = ts_tx;
        this.ts_rx = ts_rx;
    }

    public double getTs_tx() {
        return ts_tx;
    }

    public double getTs_rx() {
        return ts_rx;
    }

    public int getId() {
        return id;
    }

    


  

    
    
}
