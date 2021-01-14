package it.uninsubria.qrecipe.modelli;


import java.util.List;

public class Ordine {
    private String id;
    private String data;
    private String ricetta;
    private String cliente;
    private String indirizzo;
    private List<IngredienteOrdine> ingredienti;


    public Ordine() {
    }

    public Ordine(String id, String data, String ricetta, String cliente, String indirizzo, List<IngredienteOrdine> ingredienti) {
        this.id = id;
        this.data = data;
        this.ricetta = ricetta;
        this.cliente = cliente;
        this.indirizzo = indirizzo;
        this.ingredienti = ingredienti;

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

    public String getIndirizzo() { return indirizzo; }

    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public List<IngredienteOrdine> getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(List<IngredienteOrdine> ingredienti) { this.ingredienti = ingredienti; }

    public String getStato(){
        String stato = "chiuso";
        for(IngredienteOrdine ingredienteOrdine: this.ingredienti){
            if(ingredienteOrdine.getStato_consegna().equals("spedito")){
                stato = "in_corso";
                break;
            }
        }
        return stato;
    }
}
