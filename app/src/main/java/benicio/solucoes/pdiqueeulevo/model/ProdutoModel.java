package benicio.solucoes.pdiqueeulevo.model;

public class ProdutoModel {

    String id, nome, descri, preco, linkImage;

    boolean temEstoque;

    public ProdutoModel() {
    }

    public ProdutoModel(String id, String nome, String descri, String preco, String linkImage, boolean temEstoque) {
        this.id = id;
        this.nome = nome;
        this.descri = descri;
        this.preco = preco;
        this.linkImage = linkImage;
        this.temEstoque = temEstoque;
    }

    @Override
    public String toString() {

        return   descri + '\n' +
                 (temEstoque ? "Estoque Disponível" : "Estoque Não Disponível") ;
    }

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

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public boolean isTemEstoque() {
        return temEstoque;
    }

    public void setTemEstoque(boolean temEstoque) {
        this.temEstoque = temEstoque;
    }
}
