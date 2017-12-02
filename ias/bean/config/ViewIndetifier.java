package com.ias.bean.config;

import java.util.List;

/**
 * Created by vector on 16/6/20.
 */
public class ViewIndetifier {
    String xpath;
    List<Property> properties;

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
}
