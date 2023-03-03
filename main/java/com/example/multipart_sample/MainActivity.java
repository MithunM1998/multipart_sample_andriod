package com.example.multipart_sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.multipart_sample.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        clickListeners();
    }

    private void clickListeners() {
        binding.selectImage.setOnClickListener(v->{
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 10);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE },1);
            }
        });

        binding.save.setOnClickListener(v->{
            addCustomer(binding.name.getText().toString(), binding.reference.getText().toString());
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Context context = MainActivity.this;
            path = RealPathUtil.getRealPath(context, uri);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            binding.imageview.setImageBitmap(bitmap);




        }
    }

    public void addCustomer(String name, String refernce) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://75.119.143.175:8080/ErpNext/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body = MultipartBody.Part.createFormData("", file.getName(), requestFile);

        RequestBody cus_name = RequestBody.create(MediaType.parse("multipart/form-data"),name);
        RequestBody cus_reference = RequestBody.create(MediaType.parse("multipart/form-data"), refernce);

        Api apiService = retrofit.create(Api.class);
        Call<AddCustomer> call = apiService.addCustomer(body, cus_name, cus_reference);
        call.enqueue(new Callback<AddCustomer>() {
            @Override
            public void onResponse(Call<AddCustomer> call, Response<AddCustomer> response) {
                if (response.isSuccessful()) {

                    if (response.body().getStatus().toString().equals("200")) {

                        Toast.makeText(getApplicationContext(), "Customer Added", Toast.LENGTH_SHORT).show();
                        Log.d("mi","done added");
                    } else {
                        Toast.makeText(getApplicationContext(), "not Added", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AddCustomer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("mi",t.toString());
            }
        });

    }

}