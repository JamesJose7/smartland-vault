package com.jeeps.smartlandvault.observatories;

public class UserObservatory {
    private UserDetails usuario;
    private ObservatoryDetails observatorio;

    public UserDetails getUsuario() {
        return usuario;
    }

    public void setUsuario(UserDetails usuario) {
        this.usuario = usuario;
    }

    public ObservatoryDetails getObservatorio() {
        return observatorio;
    }

    public void setObservatorio(ObservatoryDetails observatorio) {
        this.observatorio = observatorio;
    }

    public static class UserDetails {
        private String identificacion;
        private String nombre;
        private String correo;
        private int estado;
        private String imagen;
        private boolean interno;
        private String telefono;
        private String celular;
        private String extension;
        private String register;

        public String getIdentificacion() {
            return identificacion;
        }

        public void setIdentificacion(String identificacion) {
            this.identificacion = identificacion;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public int getEstado() {
            return estado;
        }

        public void setEstado(int estado) {
            this.estado = estado;
        }

        public String getImagen() {
            return imagen;
        }

        public void setImagen(String imagen) {
            this.imagen = imagen;
        }

        public boolean isInterno() {
            return interno;
        }

        public void setInterno(boolean interno) {
            this.interno = interno;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getCelular() {
            return celular;
        }

        public void setCelular(String celular) {
            this.celular = celular;
        }

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public String getRegister() {
            return register;
        }

        public void setRegister(String register) {
            this.register = register;
        }
    }

    public static class ObservatoryDetails {
        private int id;
        private String codigo;
        private String nombre;
        private String descripcion;
        private String url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
