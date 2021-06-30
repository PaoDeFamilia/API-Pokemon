package com.example.apipokemon;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class CarregaPokemon extends AsyncTaskLoader <String> {
    private String mQueryString;
    CarregaPokemon(Context context, String queryString) {
        super(context);
        mQueryString = queryString;
    }
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
    @Nullable
    @Override
    public String loadInBackground() {
        return ClasseURL.buscaPoke(mQueryString);
    }
}
