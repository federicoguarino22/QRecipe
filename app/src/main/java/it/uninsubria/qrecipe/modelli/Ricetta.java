package it.uninsubria.qrecipe.modelli;

import java.util.List;

public class Ricetta {
    private String nome;
    private String link;
    private String immagine;
    private List<IngredienteRicetta> ingredienti;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public List<IngredienteRicetta> getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(List<IngredienteRicetta> ingredienti) {
        this.ingredienti = ingredienti;
    }
}
