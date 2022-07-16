import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.InputStreamReader;

public class ClientHandler implements Runnable{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket clientSocket) throws IOException{
        this.client = clientSocket;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run(){
        // try{
        //     while(true){
        //         String request = in.readLine();
        //         if(request.contains("name")){
        //             out.println("oi");
        //             out.println(DataServer.getIntervalos(this));
        //         } else {
        //             out.println("nothing important");
        //         }
        //     }
        // } catch (IOException e){
        //     System.err.println("IO exception");
        //     System.err.println(e.getStackTrace());
        // }
        //   finally{
        //     out.close();
        //     try {
        //         in.close();
        //     } catch (IOException e){
        //         e.printStackTrace();
        //     }
            
        // }
    }

    public Socket getClient() {
        return this.client;
    }

}
