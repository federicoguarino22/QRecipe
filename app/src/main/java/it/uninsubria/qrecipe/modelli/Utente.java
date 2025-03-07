package it.uninsubria.qrecipe.modelli;

public class Utente {
    private String email;
    private String password;
    private String name;
    private String surname;
    private String username;
    private String phone;
    private String tipo;
    private String id;

    public Utente(){
    }

    public Utente(String email, String password, String name, String surname, String username, String phone, String tipo, String id) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.phone = phone;
        this.tipo = tipo;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

}
