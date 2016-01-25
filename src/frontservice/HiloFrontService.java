/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frontservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Seba
 */
//Clase para los hilos del front service
public class HiloFrontService implements Runnable{
    private DataOutputStream salidaCaching;
    private DataOutputStream salidaIndex;
    //protected DataInputStream dis;
    BufferedReader entradaCaching;
    BufferedReader entradaIndex;
    private int id;
    String sentence;
    String desdeCaching;
    String desdeIndex;
    ArrayList Stopwords;    
    String query;
    Socket socketCaching;
    Socket socketIndex;
    String ipCaching;
    String ipIndex;
    String puerto;
    
    public HiloFrontService(int id, String query, ArrayList Stopwords, String ipCaching, String ipIndex, String puerto) {
        this.id = id;
        this.query = query;
        this.Stopwords = Stopwords;
        this.ipCaching = ipCaching;
        this.ipIndex = ipIndex;
        this.puerto = puerto;
    }
    
    @Override
    public void run() {
        List<String> list = new ArrayList<String>();
        String[] palabras = query.split(" ");
        for (int i = 0; i < palabras.length; i++) {
            //Si la palabra no es stopword
            if(!Stopwords.contains(palabras[i].toLowerCase())){
                //Se agrega a un arreglo dinamico
                list.add(palabras[i].toLowerCase());
            }

        }
        System.out.println(list);
        String palabrasFiltradas = "";
        for (int i = 0; i < list.size(); i++) {
            palabrasFiltradas += list.get(i) + " ";
        }
        System.out.println(palabrasFiltradas);
        //Socket para el cliente (host, puerto)
        //socketClient = new Socket("localhost", 8090);
        //outToServer = new DataOutputStream(socketClient.getOutputStream());
        //dis = new DataInputStream(sk.getInputStream());
        //inFromServer = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

        String[] requests = {
        "GET /respuestas/", // <p>hola mundo</>
        "GET /users",
        "GET /users/1234",
        "GET /users/55556",
        "ABC /users/1234",};

//            for (int i = 0; i < requests.length; i++) {
//                System.out.println(requests[i]);
//            }
//            System.out.print("Ingrese numero: ");

        //int numero = Integer.parseInt(inFromUser.readLine());
        int numero = 0;
        requests[numero] = requests[numero] + palabrasFiltradas;
        try {
            socketCaching = new Socket(InetAddress.getByName(ipCaching), Integer.parseInt(puerto));
            salidaCaching = new DataOutputStream(socketCaching.getOutputStream());
            entradaCaching = new BufferedReader(new InputStreamReader(socketCaching.getInputStream()));
            System.out.println("Se envia mensaje a Caching Service");
            System.out.println(requests[numero]);
            salidaCaching.writeBytes(requests[numero]+"\n");
            System.out.println("Se espera respuesta de Caching Service");
            
             //Recibimos del servidor
            desdeCaching = entradaCaching.readLine();
            
            
            
            String delimitadores = "[:\\r?\\n|\\}\\{\"\\[\\]]+";
            //Palabras del texto, separadas
            String[] palabrasSeparadas = desdeCaching.split(delimitadores);
            String resultado = null;
            for (int i = 0; i < palabrasSeparadas.length; i++) {
                if (palabrasSeparadas[i].equals("Result")){
                    resultado = palabrasSeparadas[i+1];
                }
                    
            }
            
            salidaCaching.close();
            socketCaching.close();
            
            if (resultado.equals("Miss")){
                //Se manda consulta a index Service
                socketIndex = new Socket(InetAddress.getByName(ipIndex), Integer.parseInt(puerto));
                //socketIndex = new Socket("localhost", Integer.parseInt(puerto));
                salidaIndex = new DataOutputStream(socketIndex.getOutputStream());
                entradaIndex = new BufferedReader(new InputStreamReader(socketIndex.getInputStream()));
                System.out.println("Se envia mensaje a Index Service");
                System.out.println(requests[numero]);
                salidaIndex.writeBytes(requests[numero]+"\n");
                System.out.println("Se espera respuesta del Index Service");
                
                desdeIndex = entradaIndex.readLine();
            
                socketIndex.close();
                System.out.println("Server response: " + desdeIndex);
            }else{
                System.out.println("Hit en Caching Service");
                System.out.println(desdeCaching);
            }
            
            
        } catch (IOException ex) {
            Logger.getLogger(HiloFrontService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
