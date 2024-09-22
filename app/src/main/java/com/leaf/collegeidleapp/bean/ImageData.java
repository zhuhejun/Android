package com.leaf.collegeidleapp.bean;

import java.util.List;


    public class ImageData {

            private long imageCode;
            private List<String> imageUrlList;

            public long getImageCode() {
                return imageCode;
            }

            public void setImageCode(long imageCode) {
                this.imageCode = imageCode;
            }

            public List<String> getImageUrlList() {
                return imageUrlList;
            }

            public void setImageUrlList(List<String> imageUrlList) {
                this.imageUrlList = imageUrlList;
            }

    }
