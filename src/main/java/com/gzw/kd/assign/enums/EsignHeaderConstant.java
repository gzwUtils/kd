package com.gzw.kd.assign.enums;


@SuppressWarnings("all")
public enum EsignHeaderConstant {

    ACCEPT("*/*"),
    DATE(""),
    HEADERS(""),
    CONTENTTYPE_FORMDATA("application/x-www-form-urlencoded"),
    CONTENTTYPE_JSON("application/json; charset=UTF-8"),
    CONTENTTYPE_PDF("application/pdf"),
    CONTENTTYPE_STREAM("application/octet-stream"),
    AUTHMODE("Signature");
    private String value;

    private EsignHeaderConstant(String value) {
        this.value = value;
    }

    public String VALUE() {
        return this.value;
    }
}
