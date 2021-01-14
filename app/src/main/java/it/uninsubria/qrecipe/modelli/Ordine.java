package it.uninsubria.qrecipe.modelli;


import java.util.List;

public class Ordine {
    private String id;
    private String data;
    private Ricetta ricetta;
    private String cliente;
    private String indirizzo;
    private List<IngredienteOrdine> ingredienti;
    private String stato;

    public Ordine() {
    }

    public Ordine(String id, String data, Ricetta ricetta, String cliente, String indirizzo, List<IngredienteOrdine> ingredienti, String stato) {
        this.id = id;
        this.data = data;
        this.ricetta = ricetta;
        this.cliente = cliente;
        this.indirizzo = indirizzo;
        this.ingredienti = ingredienti;
        this.stato = stato;
    }

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

    public Ricetta getRicetta() {
        return ricetta;
    }

    public void setRicetta(Ricetta ricetta) {
        this.ricetta = ricetta;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getIndirizzo() { return indirizzo; }

    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public List<IngredienteOrdine> getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(List<IngredienteOrdine> ingredienti) { this.ingredienti = ingredienti; }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }
}
