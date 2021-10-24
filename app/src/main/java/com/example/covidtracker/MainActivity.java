package com.example.covidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covidtracker.api.ApiUtilities;
import com.example.covidtracker.api.CountryData;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView totalDeaths, totalActive, totalTests, totalConfirmed, totalRecovered;
    private TextView todayDeaths, todayConfirmed, todayRecovered, date;
    private List<CountryData> list;
    private PieChart pieChart;
    String country = "India";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = new ArrayList<>();
        if(getIntent().getStringExtra("country") != null){
            country = getIntent().getStringExtra("country");
        }
        findID();
        TextView cname = findViewById(R.id.cname);
        cname.setText(country);
        cname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CountryActivity.class);
                startActivity(intent);
            }
        });
        ApiUtilities.getApiInterface().getCountryData()
                .enqueue(new Callback<List<CountryData>>() {
                    @Override
                    public void onResponse(Call<List<CountryData>> call, Response<List<CountryData>> response) {
                        list.addAll(response.body());
                        for(int i=0; i<list.size(); i++) {
                            if(list.get(i).getCountry().equals(country)){
                                ConstraintLayout coordinatorLayout = findViewById(R.id.coordinateLayout);
                                coordinatorLayout.setVisibility(View.GONE);
                                ProgressBar progressBar = findViewById(R.id.progressBar);
                                progressBar.setVisibility(View.GONE);
                                int confirm = Integer.parseInt(list.get(i).getCases());
                                int active = Integer.parseInt(list.get(i).getActive());
                                int deaths = Integer.parseInt(list.get(i).getDeaths());
                                int recovered = Integer.parseInt(list.get(i).getRecovered());

                                totalActive.setText(NumberFormat.getInstance().format(active));
                                totalConfirmed.setText(NumberFormat.getInstance().format(confirm));
                                totalDeaths.setText(NumberFormat.getInstance().format(deaths));
                                totalRecovered.setText(NumberFormat.getInstance().format(recovered));

                                todayDeaths.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayDeaths())));
                                todayConfirmed.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayCases())));
                                todayRecovered.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayRecovered())));
                                totalTests.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTests())));


                                setText(list.get(i).getUpdated());
                                pieChart.addPieSlice(new PieModel("Active", active,getResources().getColor(R.color.blue_pie)));
                                pieChart.addPieSlice(new PieModel("deaths", deaths,getResources().getColor(R.color.red_pie)));
                                pieChart.addPieSlice(new PieModel("recovered", recovered,getResources().getColor(R.color.green_pie)));
                                pieChart.startAnimation();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CountryData>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setText(String updated) {
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        long milliseconds = Long.parseLong(updated);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        date.setText("Updated at "+ format.format(calendar.getTime()));
    }

    private void findID() {
        totalDeaths = findViewById(R.id.totalDeaths);
        totalActive = findViewById(R.id.totalActive);
        totalTests = findViewById(R.id.totalTests);
        totalConfirmed = findViewById(R.id.totalConfirmed);
        totalRecovered = findViewById(R.id.totalRecovered);
        todayConfirmed = findViewById(R.id.todayConfirmed);
        todayDeaths = findViewById(R.id.todayDeaths);
        todayRecovered = findViewById(R.id.todayRecovered);
        pieChart = findViewById(R.id.pieChart);
        date = findViewById(R.id.date);
    }
}