import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestarSpencil {

    public static PontoRGB[][] calcularMatriz(PontoRGB[][] m) {
        int colunas = m[0].length;
        int linhas = m.length;
        PontoRGB r[][] = new PontoRGB[linhas][colunas];
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if ((i - 1 >= 0) && (i + 1 < linhas) && (j - 1 >= 0) && (j + 1 < colunas)) {
                    int R = (m[i][j].getValorR() + m[i - 1][j].getValorR() + m[i + 1][j].getValorR()
                            + m[i][j - 1].getValorR() + m[i][j + 1].getValorR()) / 5;
                    int G = (m[i][j].getValorG() + m[i - 1][j].getValorG() + m[i + 1][j].getValorG()
                            + m[i][j - 1].getValorG() + m[i][j + 1].getValorG()) / 5;
                    int B = (m[i][j].getValorB() + m[i - 1][j].getValorB() + m[i + 1][j].getValorB()
                            + m[i][j - 1].getValorB() + m[i][j + 1].getValorB()) / 5;
                    r[i][j] = new PontoRGB(R, G, B);
                } else {
                    r[i][j] = new PontoRGB(m[i][j].getValorR(), m[i][j].getValorG(), m[i][j].getValorB());
                }
            }
        }

        return r;

    }

    public static PontoRGB[][] colocarPontoFixo(PontoRGB[][] m, ArrayList<String> pontos){
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

            m[x][y] = new PontoRGB(r, g, b);
        }

        return m;
    }

    public static void main(String[] args) throws IOException {

        // Scanner in = new Scanner(new FileReader("dados.txt"));
        Scanner in = new Scanner(new FileReader("img01.dat"));
        int n_iteracoes = 1;
        int n_matriz = 0;
        int n_ponto_fixos = 0;
        int qtd = 0;
        ArrayList<String> pontos = new ArrayList<>();
        while (in.hasNextLine()) {
            String linha = in.nextLine();

            if(qtd == 0){
                n_matriz = Integer.parseInt(linha.split(" ")[0]) + 2;
                n_ponto_fixos = Integer.parseInt(linha.split(" ")[1]);
            }else{
                if(qtd-1 != n_ponto_fixos){
                    pontos.add(linha);
                }
            }

            qtd += 1;
        }

        // Criando a matriz
        PontoRGB m[][] = new PontoRGB[n_matriz][n_matriz];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if ((i - 1 >= 0) && (i + 1 < m.length) && (j - 1 >= 0) && (j + 1 < m[0].length)) {
                    m[i][j] = new PontoRGB(0, 0, 0);
                } else {
                    m[i][j] = new PontoRGB(127, 127, 127);
                }
            }
        }

        long tempoInicial = System.currentTimeMillis();

        // Fazendo as iterações
        for(int i=0; i<n_iteracoes; i++){
            m = colocarPontoFixo(m, pontos);
            m = calcularMatriz(m);
        }

        m = colocarPontoFixo(m, pontos);

        long tempoFinal = System.currentTimeMillis();
        long tempoExecucao = tempoFinal - tempoInicial;

        System.out.println("O método foi executado em " + (tempoExecucao) + " milisegundos");
        System.out.println("O método foi executado em " + (tempoExecucao/1000) + " segundos");

        File arquivo = new File("saidaNossa.dat");

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
            for (int j = 1; j < m[0].length-1; j++) {
                bw.write("< " + m[i][j].getValorR() + ", " + m[i][j].getValorG() + ", " + m[i][j].getValorB() + " >");
                if (j != m[0].length - 1) {
                    bw.write(" ");
                }
            }
            bw.newLine();
        }

        // fecha os recursos
        bw.close();
        fw.close();

    }
}
