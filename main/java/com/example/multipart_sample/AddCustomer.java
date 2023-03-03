package com.example.multipart_sample;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class AddCustomer {

        @SerializedName("info")
        List<String> info=null;

        @SerializedName("status")
        Integer status;


        public void setInfo(List<String> info) {
            this.info = info;
        }
        public List<String> getInfo() {
            return info;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
        public Integer getStatus() {
            return status;
        }

    }

