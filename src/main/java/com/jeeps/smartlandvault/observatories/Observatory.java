package com.jeeps.smartlandvault.observatories;

import com.google.gson.annotations.SerializedName;

public class Observatory {
    private int id;
    @SerializedName("name_observatorio")
    private String nameObservatorio;
    @SerializedName("icon_observatorio")
    private String iconObservatorio;
    @SerializedName("url_api")
    private String urlApi;
    @SerializedName("url_observatorio")
    private String urlObservatorio;
    @SerializedName("imagen_portada")
    private String imagenPortada;
    private String equipo;
    private String acronimo;

    private String titulo;

    private boolean shared;

    public int getId() {
        getIdFromUrl(this.urlApi);
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameObservatorio() {
        return nameObservatorio;
    }

    public void setNameObservatorio(String nameObservatorio) {
        this.nameObservatorio = nameObservatorio;
    }

    public String getIconObservatorio() {
        return iconObservatorio;
    }

    public void setIconObservatorio(String iconObservatorio) {
        this.iconObservatorio = iconObservatorio;
    }

    public String getUrlApi() {
        return urlApi;
    }

    public void setUrlApi(String urlApi) {
        getIdFromUrl(urlApi);
        this.urlApi = urlApi;
    }

    public String getUrlObservatorio() {
        return urlObservatorio;
    }

    public void setUrlObservatorio(String urlObservatorio) {
        this.urlObservatorio = urlObservatorio;
    }

    public String getImagenPortada() {
        return imagenPortada;
    }

    public void setImagenPortada(String imagenPortada) {
        this.imagenPortada = imagenPortada;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public String getAcronimo() {
        return acronimo;
    }

    public void setAcronimo(String acronimo) {
        this.acronimo = acronimo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    private void getIdFromUrl(String url) {
        if (url != null) {
            String[] split = url.split("/");
            int id = Integer.parseInt(split[split.length - 1]);
            setId(id);
        }
    }
}
