package com.example.weather;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {
    Button buttonWeatherSearch;
    TextView tVFeelsLike;
    TextView tVHumidity;
    TextView tVDescription;
    TextView tVWindSpeed;
    TextView tVWindDeg;
    TextView tVSunrise;
    TextView tvSunset;
    TextView textView2;
    WeatherData dataContext;
    ImageView im;
    CardView t;
    CardView card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        card=findViewById(R.id.card);
        buttonWeatherSearch= (Button)findViewById(R.id.buttonWeatherSearch);
        tVFeelsLike= findViewById(R.id.tVFeelsLike);;
        tVHumidity= findViewById(R.id.tVHumidity);
        tVDescription= findViewById(R.id.tVDescription);
        tVWindSpeed= findViewById(R.id.tVWindSpeed);
        tVWindDeg= findViewById(R.id.tVWindDeg);
        tVSunrise= findViewById(R.id.tVSunrise);
        tvSunset= findViewById(R.id.tvSunset);
        textView2= findViewById(R.id.textView2);
        im=findViewById(R.id.imageView);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        buttonWeatherSearch.setOnClickListener(but);
    }
    View.OnClickListener but= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ApiService apiService;
            Retrofit retrofit;
            SearchView siv=(SearchView) findViewById(R.id.searchView);
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/") //Базовая часть адреса
                    .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                    .build();
            apiService = retrofit.create(ApiService.class);
            apiService.getData(siv.getQuery().toString(), "3dc934e74c4394da9d9ec55d1cbfe322","metric","ru").enqueue(new Callback<WeatherData>()
                    {
                        @Override
                        public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                            if (response.body()!=null) {
                                dataContext = response.body();
                                textView2.setText(dataContext.name + dataContext.main.temp_max);
                                setDisplay();
                                new ProgressTask().execute();
                            }
                            else textView2.setText("Город не найден");


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

    public void setDisplay(){
        tVFeelsLike.setText("Ощущаемая температура "+dataContext.main.feels_like.toString());
        tVHumidity.setText("Влажность "+(int)Math.ceil(dataContext.main.humidity));
        tVDescription.setText(dataContext.weather.get(0).description);
        tVWindSpeed.setText("Скорость ветра "+(int)Math.ceil(dataContext.wind.speed));
        tVWindDeg.setText("Направление ветра "+dataContext.wind.deg+" градусов");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(dataContext.sys.sunrise * 1000);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (minute>9)tVSunrise.setText("Время восхода "+hour+":"+minute);
        else tVSunrise.setText("Время восхода "+hour+":"+"0"+minute);
        calendar.setTimeInMillis(dataContext.sys.sunset * 1000);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        if (minute>9)tvSunset.setText("Время заката "+hour+":"+minute);
        else tvSunset.setText("Время заката "+hour+":"+"0"+minute);

   }
    class ProgressTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            try {
                InputStream is = (InputStream) new URL("https://openweathermap.org/img/wn/"+ dataContext.weather.get(0).icon +"@2x.png").getContent();
                Drawable d = Drawable.createFromStream(is, "src name");
                im.setImageDrawable(d);

            } catch (Exception e) {
                Log.d("Mytag","изображение не загружено");
            }
            return null;
        }

    }



}

