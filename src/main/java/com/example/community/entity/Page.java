package com.example.community.entity;

/**
 * @program: community
 * @description:
 * @author: LK
 * @create: 2023-10-18 20:15
 **/


public class Page {

    // 当前页码
    private int current = 1;
    // 每页显示上线
    private int limit = 10;
    // 数据总数（用于计算总页数）
    private int rows;
    // 查询路径(用于复用分页链接)
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
/*
*         返回当前页的起始行

 * */
    public int getOffset(){
        return (current - 1) * limit;
    }

    /*
    * 获取总页数
    * */
    public int getTotal(){
        if(rows % limit == 0){
            return rows % limit;
        }else{
            return rows % limit + 1;
        }
    }

    /*
    * 获取起始页码
    * */
    public int getFrom(){
        return current - 2 > 0 ? current - 2 : 1;
    }
    /*
     * 获取结束页码
     * */
    public int getTo(){
        int total = getTotal();
        return current + 2 <= total ? current + 2 : total;
    }









}
