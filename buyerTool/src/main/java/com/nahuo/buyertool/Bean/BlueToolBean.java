package com.nahuo.buyertool.Bean;

import com.gprinter.io.PortParameters;

/**
 * Created by jame on 2017/10/27.
 */

public class BlueToolBean {
    String name="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
  public PortParameters parameters;

    public PortParameters getParameters() {
        return parameters;
    }

    public void setParameters(PortParameters parameters) {
        this.parameters = parameters;
    }
}
