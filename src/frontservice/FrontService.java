/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frontservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Seba
 */
public class FrontService {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        ArrayList Stopwords = leerStopWords();
        File archivo = new File("Config.txt");
 
        FileReader fr = new FileReader(archivo);

        BufferedReader br = new BufferedReader(fr);

        //Lineas del Config.txt
        //Linea 1 tiene la cantidad de particiones de la bd
        String linea1 = br.readLine();
        String linea2 = br.readLine();
        String linea3 = br.readLine();
        String LineaCaching[] = linea1.split(" ");
        String LineaIndex[] = linea2.split(" ");
        String LineaPuerto[] = linea3.split(" ");
        
        String ipCaching = (LineaCaching[1]);
        String ipIndex = (LineaIndex[1]);
        String puerto = (LineaPuerto[1]);
        fr.close();
        
        
        
        while (true) {
            System.out.print("Ingrese las palabras de la busqueda: ");            
            Scanner scanner = new Scanner(System.in);
            String query = scanner.nextLine();
        
            for (int i = 0; i < 1; i++) {
                //String numero_query = Integer.toString(i);
                //String query2  = query + numero_query;
                (new Thread (new HiloFrontService(i, query, Stopwords, ipCaching, ipIndex, puerto))).start();   
            }
        }
        
    }
    
    //Función para leer las stopwords y pasarlas a un ArrayList y luego retornar dicho ArrayList
    private static ArrayList leerStopWords() {
        ArrayList Stopwords = new ArrayList();
        FileReader fr = null;
        BufferedReader br = null;
        //Cadena de texto donde se guardara el contenido del archivo
        String contenido = "";
        try {
            String ruta = "Stopwords.txt";
            fr = new FileReader(ruta);
            br = new BufferedReader(fr);
            String linea;
            //Obtenemos el contenido del archivo linea por linea
            while ((linea = br.readLine()) != null) {
                Stopwords.add(linea);
            }

        } catch (Exception e) {
        } //finally se utiliza para que si todo ocurre correctamente o si ocurre
        //algun error se cierre el archivo que anteriormente abrimos
        finally {
            try {
                br.close();
            } catch (Exception ex) {
            }
        }
        return Stopwords;
    }
}
