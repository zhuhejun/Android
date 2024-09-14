package com.leaf.collegeidleapp.bean;


/**
 * 我的收藏实体类
 * @author autumn_leaf
 */
public class Collection {

    //编号
    private Integer commodityId;
    //学生学号
    private String StuId;

    public Integer getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Integer commodityId) {
        this.commodityId = commodityId;
    }

    public String getStuId() {
        return StuId;
    }

    public void setStuId(String stuId) {
        StuId = stuId;
    }

}
