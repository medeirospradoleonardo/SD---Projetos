import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataServer {

    private static int n_clients = 2;
    private static int n_iteracoes = 10000;
    private static int n_matriz = 256;

    private static final int PORT = 9090;

    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(n_clients);

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Scanner in = new Scanner(new FileReader("img01.dat"));
        int n_ponto_fixos = 0;
        int qtd = 0;
        ArrayList<String> pontos = new ArrayList<>();
        while (in.hasNextLine()) {
            String linha = in.nextLine();

            if (qtd == 0) {
                n_matriz = Integer.parseInt(linha.split(" ")[0]) + 2;
                n_ponto_fixos = Integer.parseInt(linha.split(" ")[1]);
            } else {
                if (qtd - 1 != n_ponto_fixos) {
                    pontos.add(linha);
                }
            }

            qtd += 1;
        }

        // Criando a matriz
        int m[][] = new int[n_matriz][n_matriz*3];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < n_matriz; j++) {
                if ((i - 1 >= 0) && (i + 1 < m.length) && ((3*(j-1)) >= 0) && ((3*(j+1)) < m[0].length)) {
                    m[i][(3*j)] = 0;
                    m[i][(3*j)+1] = 0;
                    m[i][(3*j)+2] = 0;
                } else {
                    m[i][(3*j)] = 127;
                    m[i][(3*j)+1] = 127;
                    m[i][(3*j)+2] = 127;
                }
            }
        }

        ServerSocket listener = new ServerSocket(PORT);

        ArrayList<ObjectOutputStream> outputs = new ArrayList<>();
        ArrayList<ObjectInputStream> inputs = new ArrayList<>();

        while (clients.size() < n_clients) {
            System.out.println("[SERVER] waiting for client connection");
            Socket client = listener.accept();
            System.out.println("[SERVER] connected to client");
            ClientHandler clientThread = new ClientHandler(client);

            clients.add(clientThread);
            
            pool.execute(clientThread);
        }
        
        ArrayList<int[][]> partes_matriz = new ArrayList<>();
        // ObjectOutputStream output = new ObjectOutputStream(clientThread.getClient().getOutputStream());
        // ObjectInputStream input = new ObjectInputStream(clientThread.getClient().getInputStream());

        long tempoInicial = System.currentTimeMillis();

        for (int i = 0; i < n_iteracoes; i++) {
            m = colocarPontoFixo(m, pontos);
            for (int j = 0; j < clients.size(); j++) {
                ClientHandler client = clients.get(j);

                if(i == 0){
                    ObjectOutputStream output = new ObjectOutputStream(client.getClient().getOutputStream());
                    outputs.add(output);
                }

                // ObjectOutputStream output = new ObjectOutputStream(client.getClient().getOutputStream());
                outputs.get(j).writeObject(getParteMatriz(client, m));
                outputs.get(j).flush();

                // ObjectInputStream input = new ObjectInputStream(client.getClient().getInputStream());
                // PontoRGB[][] teste = (PontoRGB[][]) input.readObject();
                // printarMatriz(teste);

            }

            for (int j = 0; j < clients.size(); j++) {

                ClientHandler client = clients.get(j);

                if(i == 0){
                    ObjectInputStream input = new ObjectInputStream(client.getClient().getInputStream());
                    inputs.add(input);
                }

                // ObjectInputStream input = new ObjectInputStream(client.getClient().getInputStream());


                int[][] teste = (int[][]) inputs.get(j).readObject();
                partes_matriz.add(teste);

            }

            // System.out.println(partes_matriz.get(0).length);
            // System.out.println(partes_matriz.get(0)[0].length);
            // System.out.println(partes_matriz.get(1).length);
            // System.out.println(partes_matriz.get(1)[0].length);

            m = juntarMatrizes(partes_matriz);

            partes_matriz.clear();


        }

        m = colocarPontoFixo(m, pontos);

        long tempoFinal = System.currentTimeMillis();
        long tempoExecucao = tempoFinal - tempoInicial;

        System.out.println("O método foi executado em " + (tempoExecucao) + " milisegundos");
        System.out.println("O método foi executado em " + (tempoExecucao/1000) + " segundos");

        
        listener.close();

        File arquivo = new File("final.dat");

        // cria um arquivo (vazio)
        arquivo.createNewFile();
        // cria um diretório
        arquivo.mkdir();

        // construtor que recebe o objeto do tipo arquivo
        FileWriter fw = new FileWriter(arquivo);
        // construtor que recebe também como argumento se o conteúdo será acrescentado

        // construtor recebe como argumento o objeto do tipo FileWriter
        BufferedWriter bw = new BufferedWriter(fw);

        // // Colocando a matriz no arquivo considerando as bordas
        // for (int i = 0; i < m.length; i++) {
        //     for (int j = 0; j < m[0].length; j++) {
        //         bw.write("< " + m[i][j].getValorR() + ", " + m[i][j].getValorG() + ", " + m[i][j].getValorB() + " >");
        //         if (j != m[0].length - 1) {
        //             bw.write(" ");
        //         }
        //     }
        //     bw.newLine();
        // }

        // Colocando a matriz no arquivo não considerando as bordas
        for (int i = 1; i < m.length-1; i++) {
            for (int j = 1; j < m.length-1; j++) {
                bw.write("< " + m[i][(3*j)] + ", " + m[i][(3*j)+1] + ", " + m[i][(3*j)+2] + " >");
                if (j != m.length - 1) {
                    bw.write(" ");
                }
            }
            bw.newLine();
        }

        // fecha os recursos
        bw.close();
        fw.close();

        System.out.println("Finalizado");

    }

    public static ArrayList<Integer> getIntervalos(ClientHandler c) {
        ArrayList<Integer> intervalos = new ArrayList<>();
        int pos = 0;

        for (int i = 0; i < clients.size(); i++) {
            if (c.equals(clients.get(i))) {
                pos = i;
                break;
            }
        }

        intervalos.add(((n_matriz - 2) / n_clients) * pos);
        intervalos.add((((n_matriz - 2) / n_clients) * (pos + 1)) + 1);

        return intervalos;
    }

    public static int[][] getParteMatriz(ClientHandler c, int[][] m) {
        ArrayList<Integer> intervalos = getIntervalos(c);
        int n_linhas = Math.abs(intervalos.get(0) - intervalos.get(1)) + 1;
        int p_m[][] = new int[n_linhas][n_matriz*3];
        int x = 0;

        for (int i = intervalos.get(0); i <= intervalos.get(1); i++) {
            for (int j = 0; j < n_matriz; j++) {
                p_m[x][(3*j)] = m[i][(3*j)];
                p_m[x][(3*j)+1] = m[i][(3*j)+1];
                p_m[x][(3*j)+2] = m[i][(3*j)+2];
            }
            x++;
        }

        return p_m;
    }

    public static void printarMatriz(int[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m.length; j++) {
                System.out.print(m[i][(3*j)] + " " + m[i][(3*j)+1] + " " + m[i][(3*j)+2] + " ");
            }
            System.out.println();
        }
    }

    public static int[][] colocarPontoFixo(int[][] m, ArrayList<String> pontos){
        int x = 0;
        int y = 0;
        int r = 0;
        int b = 0;
        int g = 0;
        for(String p : pontos){
            x = Integer.parseInt(p.split(" ")[0]);
            y = Integer.parseInt(p.split(" ")[1]);
            r = Integer.parseInt(p.split(" ")[2]);
            g = Integer.parseInt(p.split(" ")[3]);
            b = Integer.parseInt(p.split(" ")[4]);

            m[x][(3*y)] = r;
            m[x][(3*y)+1] = g;
            m[x][(3*y)+2] = b;
        }

        return m;
    }

    public static int[][] juntarMatrizes(ArrayList<int[][]> partes_matriz){
        int r[][]= new int[n_matriz][n_matriz*3];

        int linha_r = 1;
        int coluna_r = 1;

        for(int i=0; i<n_matriz; i++){
            r[i][0] = 127;
            r[i][0+1] = 127;
            r[i][0+2] = 127;

            r[i][(3*(n_matriz-1))] = 127;
            r[i][(3*(n_matriz-1))+1] = 127;
            r[i][(3*(n_matriz-1))+2] = 127;

            r[0][(3*i)] = 127;
            r[0][(3*i)+1] = 127;
            r[0][(3*i)+2] = 127;

            r[n_matriz-1][(3*i)] = 127;
            r[n_matriz-1][(3*i)+1] = 127;
            r[n_matriz-1][(3*i)+2] = 127;
        }

        for(int[][] p : partes_matriz){
            for(int i=0; i<p.length; i++){
                for(int j=0; j<p[0].length/3; j++){
                    r[linha_r][(3*coluna_r)] = p[i][(3*j)];
                    r[linha_r][(3*coluna_r)+1] = p[i][(3*j)+1];
                    r[linha_r][(3*coluna_r)+2] = p[i][(3*j)+2];
                    coluna_r++;
                }
                coluna_r = 1;
                linha_r++;
            }
        }

        return r;
    }
}