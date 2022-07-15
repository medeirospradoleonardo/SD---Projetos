package proj;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class DataServer {

    private static final int PORT = 9090;

    public static void main(String[] args) throws IOException {

        int n_clientes = 2;

        Pessoa pessoa = new Pessoa("gabriel_viado_da_silva");

        ServerSocket listener = new ServerSocket(PORT);

        System.out.println("[SERVER] waiting for client connection");

        ArrayList<Socket> clientes = new ArrayList<>();

        // Socket client = listener.accept();
        // System.out.println("[SERVER] connected to client");

        // // PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        // //out.println(pessoa.getNome());
        // ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
        // output.writeObject(pessoa);
        // output.flush();

        while (clientes.size() != n_clientes) {
            Socket client = listener.accept();
            clientes.add(client);
            System.out.println("[SERVER] connected to client");

            // PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            // out.println(pessoa.getNome());
            ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
            output.writeObject(pessoa);
            output.flush();
        }

        // String date = new Date().toString();
        // System.out.println("[SERVER] date is "+ date);

        System.out.println("[SERVER] sent date. Closing...");
        // client.close();
        listener.close();
    }
}