package proj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Client {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);

        //BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //String serverResponse = input.readLine();

        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        Pessoa pessoa = (Pessoa) input.readObject();
        String serverResponse = pessoa.getNome();
        JOptionPane.showMessageDialog(null, serverResponse);

        // socket.close();
        // System.exit(0);
    }
}
