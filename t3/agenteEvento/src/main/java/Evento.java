public class Evento {
    private int index;
    private String descricao ;
    private String dataInicio ;
    private String dataFim;
    private String futuro ;
    private String presente ;
    private String passado;

    public Evento(int index ,String descricao, String dataInicio, String dataFim, String futuro, String presente, String passado) {
        this.index = index;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.futuro = futuro;
        this.presente = presente;
        this.passado = passado;
    }

    public int getIndice(){
        return index;
    }

    public void setIndice(int index){
        this.index = index;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setEnd(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getFuturo() {
        return futuro;
    }

    public void setFuturo(String futuro) {
        this.futuro = futuro;
    }

    public String getPresente() {
        return presente;
    }

    public void setPresente(String presente) {
        this.presente = presente;
    }

    public String getPassado() {
        return passado;
    }

    public void setPassado(String passado) {
        this.passado = passado;
    }
}