package com.matias.disciteomnes.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Singleton class for providing a configured Retrofit instance
public class RetrofitInstance {

    // Localhost for Android emulator; replace with actual IP or domain in production
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit;

    // Returns a singleton Retrofit instance
    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    //  MISSING METHOD: provides a ready-to-use API service instance
    public static DisciteOmnesApi getApi() {
        return getInstance().create(DisciteOmnesApi.class);
    }
}
