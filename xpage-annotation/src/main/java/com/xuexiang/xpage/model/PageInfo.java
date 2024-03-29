package com.xuexiang.xpage.model;

import com.alibaba.fastjson.JSON;
import com.xuexiang.xpage.enums.CoreAnim;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * 页面信息
 *
 * @author xuexiang
 */
public class PageInfo implements Serializable {


    private String name;

    private String classPath;

    private String params = "";

    private CoreAnim anim;

    private int extra;

    private Integer sort;

    public PageInfo() {

    }

    public PageInfo(Class<?> clazz) {
        this(clazz.getSimpleName(), clazz.getName());
    }

    public PageInfo(String name, String classPath) {
        this.name = name;
        this.classPath = classPath;
    }

    public PageInfo(String name, Class<?> clazz) {
        this.name = name;
        this.classPath = clazz.getName();
    }

    public PageInfo(String name, String classPath, String params) {
        this.name = name;
        this.classPath = classPath;
        this.params = params;
    }

    public PageInfo(String name, Class<?> clazz, Map<String, Object> params) {
        this.name = name;
        this.classPath = clazz.getName();
        this.params = JSON.toJSONString(params);
    }

    public PageInfo(String name, String classPath, String params, CoreAnim anim, int extra) {
        this.name = name;
        this.classPath = classPath;
        this.params = params;
        this.anim = anim;
        this.extra = extra;
    }

    public PageInfo(String name, String classPath, String params, CoreAnim anim, int extra, int sort) {
        this.name = name;
        this.classPath = classPath;
        this.params = params;
        this.anim = anim;
        this.extra = extra;
        this.sort = sort;
    }

    public static String getParams(String[] params) {
        if (params != null && params.length > 0) {
            Map<String, Object> paramMaps = new HashMap<>();
            for (int i = 0; i < params.length; i++) {
                paramMaps.put(params[i], "");
            }
            return JSON.toJSONString(paramMaps);
        }
        return "";
    }

    public String getName() {
        return name;
    }

    public PageInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getClassPath() {
        return classPath;
    }

    public PageInfo setClassPath(String classPath) {
        this.classPath = classPath;
        return this;
    }

    public PageInfo setClassPath(Class<?> clazz) {
        classPath = clazz.getName();
        return this;
    }

    public String getParams() {
        return params;
    }

    public PageInfo setParams(String params) {
        this.params = params;
        return this;
    }

    public PageInfo setParams(Map<String, Object> params) {
        this.params = JSON.toJSONString(params);
        return this;
    }

    public PageInfo setParams(String[] params) {
        if (params != null && params.length > 0) {
            Map<String, Object> paramMaps = new HashMap<>();
            for (int i = 0; i < params.length; i++) {
                paramMaps.put(params[i], "");
            }
            setParams(paramMaps);
        }
        return this;
    }

    public CoreAnim getAnim() {
        return anim;
    }

    public PageInfo setAnim(CoreAnim anim) {
        this.anim = anim;
        return this;
    }

    public int getExtra() {
        return extra;
    }

    public PageInfo setExtra(int extra) {
        this.extra = extra;
        return this;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "name='" + name + '\'' +
                ", classPath='" + classPath + '\'' +
                ", params='" + params + '\'' +
                ", anim=" + anim +
                ", extra=" + extra +
                '}';
    }
}
