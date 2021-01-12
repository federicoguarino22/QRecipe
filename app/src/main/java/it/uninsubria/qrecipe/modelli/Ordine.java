package it.uninsubria.qrecipe.modelli;


import java.util.List;

public class Ordine {
    private String id;
    private String data;
    private String ricetta;
    private String cliente;
    private List<IngredienteOrdine> ingredienti;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRicetta() {
        return ricetta;
    }

    public void setRicetta(String ricetta) {
        this.ricetta = ricetta;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public List<IngredienteOrdine> getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(List<IngredienteOrdine> ingredienti) {
        this.ingredienti = ingredienti;
    }
}
