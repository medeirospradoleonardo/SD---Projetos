package proj;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataServer {

    private static String [] names = {"1", "2", "3", "4"};
    private static String [] surnames = {"11", "22", "33", "44"};

    private static final int PORT = 9090;

    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(16);

    public static void main (String[] args) throws IOException {
        // ArrayList<String> strings = new ArrayList<>();
        // strings.add("oi");
        // strings.add("leo viadao"); 
        // Pessoa pessoa = new Pessoa("gabriel_viado_da_silva");

        ServerSocket listener = new ServerSocket(PORT);

        while(true){
            System.out.println("[SERVER] waiting for client connection");
            Socket client = listener.accept();
            System.out.println("[SERVER] connected to client");
            ClientHandler clientThread = new ClientHandler(client);
            clients.add(clientThread);

            pool.execute(clientThread);
        }
        



        // PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        //out.println(pessoa.getNome());
        // ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
        // output.writeObject(pessoa);
        // output.flush(); 
        // String date = new Date().toString();
		//System.out.println("[SERVER] date is "+ date);

        // System.out.println("[SERVER] sent date. Closing...");
        // client.close();
        // listener.close();

    }   
    public static String getRandomName(){
        String name = names [(int) (Math.random() * names.length)];
        String surname = surnames [(int) (Math.random() * names.length)];
        return name + " " + surname;
    } 
}