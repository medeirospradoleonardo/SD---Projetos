import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestarSpencil {

    public static int[][] calcularMatriz(int[][] m) {
        int linhas = m.length;
        int colunas = m[0].length;
        int m_r[][] = new int[linhas][colunas];
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < linhas; j++) {
                if ((i - 1 >= 0) && (i + 1 < linhas) && ((3 * (j - 1)) >= 0) && ((3 * (j + 1)) < colunas)) {
                    int r = (m[i][(3 * j)] + m[i - 1][(3 * j)] + m[i + 1][(3 * j)]
                            + m[i][(3 * (j - 1))] + m[i][(3 * (j + 1))]) / 5;
                    int g = (m[i][(3 * j) + 1] + m[i - 1][(3 * j) + 1] + m[i + 1][(3 * j) + 1]
                            + m[i][(3 * (j - 1)) + 1] + m[i][(3 * (j + 1)) + 1]) / 5;
                    int b = (m[i][(3 * j) + 2] + m[i - 1][(3 * j) + 2] + m[i + 1][(3 * j) + 2]
                            + m[i][(3 * (j - 1)) + 2] + m[i][(3 * (j + 1)) + 2]) / 5;

                    m_r[i][(3 * j)] = r;
                    m_r[i][(3 * j) + 1] = g;
                    m_r[i][(3 * j) + 2] = b;
                
                } else {
                    m_r[i][(3 * j)] = m[i][(3 * j)];
                    m_r[i][(3 * j) + 1] = m[i][(3 * j) + 1];
                    m_r[i][(3 * j) + 2] = m[i][(3 * j) + 2];
                }
            }
        }

        return m_r;

    }

    public static int[][] colocarPontoFixo(int[][] m, ArrayList<String> pontos) {
        int x = 0;
        int y = 0;
        int r = 0;
        int b = 0;
        int g = 0;
        for (String p : pontos) {
            x = Integer.parseInt(p.split(" ")[0]);
            y = Integer.parseInt(p.split(" ")[1]);
            r = Integer.parseInt(p.split(" ")[2]);
            g = Integer.parseInt(p.split(" ")[3]);
            b = Integer.parseInt(p.split(" ")[4]);

            m[x][(3 * y)] = r;
            m[x][(3 * y) + 1] = g;
            m[x][(3 * y) + 2] = b;
        }

        return m;
    }

    public static void main(String[] args) throws IOException {

        // Scanner in = new Scanner(new FileReader("dados.txt"));
        Scanner in = new Scanner(new FileReader("img01.dat"));
        int n_iteracoes = 10000;
        int n_matriz = 0;
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
        int m[][] = new int[n_matriz][n_matriz * 3];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < n_matriz; j++) {
                if ((i - 1 >= 0) && (i + 1 < m.length) && ((3 * (j - 1)) >= 0) && ((3 * (j + 1)) < m[0].length)) {
                    m[i][(3 * j)] = 0;
                    m[i][(3 * j) + 1] = 0;
                    m[i][(3 * j) + 2] = 0;
                } else {
                    m[i][(3 * j)] = 127;
                    m[i][(3 * j) + 1] = 127;
                    m[i][(3 * j) + 2] = 127;
                }
            }
        }

        long tempoInicial = System.currentTimeMillis();

        // Fazendo as iterações
        for (int i = 0; i < n_iteracoes; i++) {
            m = colocarPontoFixo(m, pontos);
            m = calcularMatriz(m);
        }

        m = colocarPontoFixo(m, pontos);

        long tempoFinal = System.currentTimeMillis();
        long tempoExecucao = tempoFinal - tempoInicial;

        System.out.println("O método foi executado em " + (tempoExecucao) + " milisegundos");
        System.out.println("O método foi executado em " + (tempoExecucao / 1000) + " segundos");

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
        // for (int j = 0; j < m[0].length; j++) {
        // bw.write("< " + m[i][j].getValorR() + ", " + m[i][j].getValorG() + ", " +
        // m[i][j].getValorB() + " >");
        // if (j != m[0].length - 1) {
        // bw.write(" ");
        // }
        // }
        // bw.newLine();
        // }

        // Colocando a matriz no arquivo não considerando as bordas
        for (int i = 1; i < m.length - 1; i++) {
            for (int j = 1; j < m.length - 1; j++) {
                bw.write("< " + m[i][(3 * j)] + ", " + m[i][(3 * j) + 1] + ", " + m[i][(3 * j) + 2] + " >");
                if (j != m.length - 1) {
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
