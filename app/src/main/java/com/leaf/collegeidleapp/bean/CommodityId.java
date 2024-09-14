package com.leaf.collegeidleapp.bean;

import java.util.concurrent.ThreadLocalRandom;

public class CommodityId {

    private int commodityId;

    public int getCommodityId() {
        return commodityId;
    }



    public CommodityId( ) {
        this.commodityId = ThreadLocalRandom.current().nextInt(100000, 1000000);
    }
}
