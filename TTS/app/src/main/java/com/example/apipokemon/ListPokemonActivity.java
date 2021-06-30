package com.example.apipokemon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ListPokemonActivity extends AppCompatActivity {

    private ListView listPoke;
    private PokemonDAO dao;
    private List<Pokemon> pokemonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pokemon);
        getSupportActionBar().hide();

        listPoke = findViewById(R.id.listV_poke);
        dao = new PokemonDAO(this);
        pokemonList = dao.returnPokemon();

        ArrayAdapter<Pokemon> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pokemonList);
        listPoke.setAdapter(listAdapter);
    }

}