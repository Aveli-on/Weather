package com.example.weather;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {
    Button buttonWeatherSearch1;
    TextView textView2;
    WeatherData dataContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        buttonWeatherSearch1= (Button)findViewById(R.id.buttonWeatherSearch);
        textView2= findViewById(R.id.textView2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        buttonWeatherSearch1.setOnClickListener(but);
    }
    View.OnClickListener but= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ApiService apiService;
            Retrofit retrofit;
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/") //Базовая часть адреса
                    .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                    .build();
            apiService = retrofit.create(ApiService.class);
            apiService.getData("Орша", "3dc934e74c4394da9d9ec55d1cbfe322","metric","ru")
                    .enqueue(new Callback<WeatherData>()
                    {
                        @Override
                        public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                            dataContext=response.body();
                            textView2.setText(dataContext.name+dataContext.main.temp_max);
                            dataContext=response.body();

                        }
                        @Override
                        public void onFailure(Call<WeatherData> call, Throwable t) {
                            textView2.setText(t.getMessage());
                        }
                    }
            );
        }
    };
    public interface ApiService {
        @GET("/data/2.5/weather")
        Call<WeatherData> getData(@Query("q") String resourceName, @Query("appid") String key, @Query("units") String unit,@Query("lang") String lang);  // Укажите параметры, если они есть

    }





}
