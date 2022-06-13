package com.distributed.systems.api.composite.course;

public class ServicesAddresses {
    private final String cmp;
    private final String crs;
    private final String rat;
    private final String lec;
    private final String usr;
    private final String pur;

    public ServicesAddresses(){
        this.crs = null;
        this.rat = null;
        this.lec = null;
        this.usr = null;
        this.pur = null;
        this.cmp = null;
    }

    public ServicesAddresses(String crs, String rat, String lec, String usr, String pur, String cmp) {
        this.crs = crs;
        this.rat = rat;
        this.lec = lec;
        this.usr = usr;
        this.pur = pur;
        this.cmp = cmp;
    }

    public String getCmp() {
        return cmp;
    }

    public String getCrs() {
        return crs;
    }

    public String getRat() {
        return rat;
    }

    public String getLec() {
        return lec;
    }

    public String getUsr() {
        return usr;
    }

    public String getPur() {
        return pur;
    }
}
