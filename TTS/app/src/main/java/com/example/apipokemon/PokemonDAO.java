package com.example.apipokemon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonDAO {
    private Conexao conexao;
    private SQLiteDatabase banco;

    public PokemonDAO (Context context){
        conexao = new Conexao(context);
        banco = conexao.getWritableDatabase();
    }

    public void insert (Pokemon pokemon){
        ContentValues values = new ContentValues();
        values.put("id", pokemon.getId());
        values.put("nome", pokemon.getNome());
        values.put("altura", pokemon.getAltura());
        values.put("peso", pokemon.getPeso());
        banco.insert("pokemon", null, values);
    }

    public void delete (Pokemon pokemon){
        banco.delete("pokemon", null, null);
    }

    public List<Pokemon> returnPokemon(){
        List<Pokemon> pokemonList = new ArrayList<>();
        Cursor cursor = banco.query("pokemon", new String[]{"id", "nome", "altura", "peso"}, null,null,null,null,null );
        while (cursor.moveToNext()){
            Pokemon pokemon = new Pokemon();
            pokemon.setId(cursor.getInt(0));
            pokemon.setNome(cursor.getString(1));
            pokemon.setAltura(cursor.getInt(2));
            pokemon.setPeso(cursor.getInt(3));
            pokemonList.add(pokemon);
        }
        return pokemonList;
    }
}
