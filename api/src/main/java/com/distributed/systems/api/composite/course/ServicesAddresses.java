package com.distributed.systems.api.composite.course;

public class ServicesAddresses {
    private final String cmp;
    private final String crs;
    private final String rat;
    private final String lec;
    private final String aut;
    private String usr;
    private String pur;

    public ServicesAddresses(){
        this.crs = null;
        this.rat = null;
        this.lec = null;
        this.usr = null;
        this.pur = null;
        this.cmp = null;
        this.aut = null;
    }

    public ServicesAddresses(String crs, String rat, String lec, String usr, String pur, String aut, String cmp) {
        this.crs = crs;
        this.rat = rat;
        this.lec = lec;
        this.usr = usr;
        this.pur = pur;
        this.aut = aut;
        this.cmp = cmp;
    }

    public ServicesAddresses(String crs, String rat, String lec, String aut, String cmp) {
        this.crs = crs;
        this.rat = rat;
        this.lec = lec;
        this.aut = aut;
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
