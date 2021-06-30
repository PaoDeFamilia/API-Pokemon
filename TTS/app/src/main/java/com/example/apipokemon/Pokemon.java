package com.example.apipokemon;

import java.io.Serializable;

public class Pokemon implements Serializable {
    private Integer id;
    private String nome;
    private Integer altura;
    private Integer peso;

    public Integer getId() { return id; }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getAltura() { return altura; }
    public void setAltura(Integer altura) {
        this.altura = altura;
    }

    public Integer getPeso() { return peso; }
    public void setPeso(Integer peso) {
        this.peso = peso;
    }

    @Override
    public String toString(){
        return "Pokémon: " +nome.toUpperCase()+ "\nNúmero: " +id+ "\nAltura: " +altura+ "dm\nPeso: " +peso+ "hg";
    }
}
