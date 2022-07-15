package proj;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Pessoa implements Serializable{
    private String nome;

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Pessoa(String nome) {
        this.nome = nome;
    }

}