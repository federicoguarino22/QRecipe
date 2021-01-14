package it.uninsubria.qrecipe.modelli;

public class IngredienteOrdine {
    private String id;
    private String stato_consegna;
    private Ingrediente ingrediente;
    private Double quantita;

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStato_consegna() {
        return stato_consegna;
    }

    public void setStato_consegna(String stato_consegna) {
        this.stato_consegna = stato_consegna;
    }

    public Double getQuantita() {
        return quantita;
    }

    public void setQuantita(Double quantita) {
        this.quantita = quantita;
    }
}
