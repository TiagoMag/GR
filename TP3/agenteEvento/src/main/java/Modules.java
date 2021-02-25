

//--AgentGen BEGIN=_BEGIN
//--AgentGen END

import ca.szc.configparser.Ini;
import org.snmp4j.agent.mo.*;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.agent.MOGroup;
import org.snmp4j.agent.MOServer;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.smi.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.LocalDateTime;
import Exceptions.NaoExisteEventoException;

//--AgentGen BEGIN=_IMPORT
//--AgentGen END

public class Modules implements MOGroup {

    /** Constantes temporais **/
    private static final int PASSADO = 1; // Representa Passado
    private static final int PRESENTE = 2; // Representa Presente
    private static final int FUTURO = 3; // Representa Futuro

    private static final LogAdapter LOGGER =
            LogFactory.getLogger(Modules.class);

    private EventosMib eventosMib;

    private MOFactory factory;

//--AgentGen BEGIN=_MEMBERS
//--AgentGen END

    public Modules() {
        eventosMib = new EventosMib();
//--AgentGen BEGIN=_DEFAULTCONSTRUCTOR
//--AgentGen END
    }

    public Modules(MOFactory factory) {
        eventosMib = new EventosMib(factory);
//--AgentGen BEGIN=_CONSTRUCTOR
//--AgentGen END
    }

    public void registerMOs(MOServer server, OctetString context)
            throws DuplicateRegistrationException
    {
        eventosMib.registerMOs(server, context);
//--AgentGen BEGIN=_registerMOs
//--AgentGen END
    }

    public void unregisterMOs(MOServer server, OctetString context) {
       eventosMib.unregisterMOs(server, context);
//--AgentGen BEGIN=_unregisterMOs
//--AgentGen END
    }

    public EventosMib getEventosMib() {
        return eventosMib;
    }

    /**
     * Função que vai receber um evento para popular a tabela de eventos.
     * @param evento
     */
    public void addEventoTable(Evento evento){
        EventosMib.GrEventosEntryRow row = getEventosMib().getGrEventosEntry().createRow(new OID(String.valueOf(evento.getIndice())));

        row.setIndex(new Counter32((evento.getIndice())));
        row.setDescricao(new OctetString(evento.getDescricao()));

        int tempo = getFrameTemporal(evento);
        if(tempo == PRESENTE) row.setMensagem(new OctetString(evento.getPresente()));
        if(tempo == PASSADO) row.setMensagem(new OctetString(concatMsg(calculaAnos(evento.getDataFim()), calculaMeses(evento.getDataFim()),
                                                            calculaSemanas(evento.getDataFim()),calculaDias(evento.getDataFim()),
                                                            calculaHoras(evento.getDataFim()),calculaMin(evento.getDataFim()),
                                                            evento.getPassado())));
        if(tempo == FUTURO) row.setMensagem(new OctetString(concatMsg(calculaAnos(evento.getDataInicio()), calculaMeses(evento.getDataInicio()),
                                                            calculaSemanas(evento.getDataInicio()),calculaDias(evento.getDataInicio()),
                                                            calculaHoras(evento.getDataInicio()),calculaMin(evento.getDataInicio()),
                                                            evento.getFuturo())));
        getEventosMib().getGrEventosEntry().addRow(row);
        addEventoTimeTable(evento);
    }

    /**
     * Função que vai receber um evento para popular a tabela de tempos.
     * @param evento
     */
    private void addEventoTimeTable(Evento evento){
        EventosMib.TempoEntryRow row = getEventosMib().getTempoEntry().createRow(new OID(String.valueOf(evento.getIndice())));
        int time = getFrameTemporal(evento); // Identifica frame temporal
        if(time == PASSADO) {
            row.setAnos(new Integer32(calculaAnos(evento.getDataFim())));
            row.setMeses(new Integer32(calculaMeses(evento.getDataFim())));
            row.setSemanas(new Integer32(calculaSemanas(evento.getDataFim())));
            row.setDias(new Integer32(calculaDias(evento.getDataFim())));
            row.setHoras(new Integer32(calculaHoras(evento.getDataFim())));
            row.setMinutos(new Integer32(calculaMin(evento.getDataFim())));
        }
        else if(time == FUTURO){
            row.setAnos(new Integer32(calculaAnos(evento.getDataInicio())));
            row.setMeses(new Integer32(calculaMeses(evento.getDataInicio())));
            row.setSemanas(new Integer32(calculaSemanas(evento.getDataInicio())));
            row.setDias(new Integer32(calculaDias(evento.getDataInicio())));
            row.setHoras(new Integer32(calculaHoras(evento.getDataInicio())));
            row.setMinutos(new Integer32(calculaMin(evento.getDataInicio())));
        }
        else if(time == PRESENTE){
            row.setAnos(new Integer32(0));
            row.setMeses(new Integer32(0));
            row.setSemanas(new Integer32(0));
            row.setDias(new Integer32(0));
            row.setHoras(new Integer32(0));
            row.setMinutos(new Integer32(0));
        }
        getEventosMib().getTempoEntry().addRow(row);
    }

    /**
     * Remove um evento da MIB.
     * @param index de um Evento
     */
    public void removeEvento(int index){
        Evento evento = null;
        try {
            evento = serializaEvento(index);
        } catch (NaoExisteEventoException e) {
            e.printStackTrace();
        }
        // Remove da tabela de tempos
        getEventosMib().getTempoEntry().removeRow(new OID(String.valueOf(evento.getIndice())));
        // Remove da tabela de eventos
        getEventosMib().getGrEventosEntry().removeRow(new OID(String.valueOf(evento.getIndice())));
    }

    /**
     * Recebe uma string que representa uma data e dá parse para LocalDateTime.
     * @param date
     * @return
     */
    private LocalDateTime parseData(String date){
        String[] data = date.split("-");
        int dia = Integer.parseInt(data[0].split("/")[0]);
        int mes = Integer.parseInt(data[0].split("/")[1]);
        int ano = Integer.parseInt(data[0].split("/")[2]);
        int hora = Integer.parseInt(data[1].split(":")[0]);
        int minuto = Integer.parseInt(data[1].split(":")[1]);

        LocalDateTime dateTime = LocalDateTime.of(ano, mes, dia, hora, minuto);
        return dateTime;
    }

    /**
     * Identifica frame temporal de um evento.
     * @param evento
     * @return constante relativa ao frame
     */
    private int getFrameTemporal(Evento evento){
        LocalDateTime begin_date = parseData(evento.getDataInicio());
        LocalDateTime end_date = parseData(evento.getDataFim());

        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(begin_date) && now.isBefore(end_date)){
            return PRESENTE;
        }
        else if(now.isBefore(begin_date)){
            return PASSADO;
        }
        else if(now.isAfter(end_date)){
            return FUTURO;
        }
        return -1;
    }

    /**
     * Concatena às mensagens de alerta o tempo que já passou/falta,
     * @param anos
     * @param meses
     * @param semanas
     * @param dias
     * @param horas
     * @param minutos
     * @param mensagem
     * @return
     */
    private String concatMsg (int anos, int meses, int semanas, int dias, int horas, int minutos, String mensagem){
        String s=(new StringBuilder()).append(mensagem).append(" ")
                .append(Math.abs(anos)).append(" ano(s), ")
                .append(Math.abs(meses)).append(" mes(es), ")
                .append(Math.abs(semanas)).append(" semana(s), ")
                .append(Math.abs(dias)).append(" dia(s), ")
                .append(Math.abs(horas)).append(" hora(s) e ")
                .append(Math.abs(minutos)).append(" minuto(s).")
                .toString();
        return s;
    }

    /**
     * Através de um índice vai ao ficheiro de configuração e serializa para evento.
     * @param index
     * @return
     */
    private Evento serializaEvento(int index) throws NaoExisteEventoException {
        Path input = Paths.get("config.cfg");
        Ini ini = null;
        try {
            ini = new Ini().read(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Map<String, String>> sections = ini.getSections();
        if(!sections.containsKey("evento"+ index)){
            throw new NaoExisteEventoException();
        }

        Evento e = new Evento(index,sections.get("evento"+ index).get("descricao"),
                                sections.get("evento"+ index).get("begin_date"),
                                sections.get("evento"+ index).get("end_date"),
                                sections.get("evento"+ index).get("mensagem_passado"),
                                sections.get("evento"+ index).get("mensagem_presente"),
                                sections.get("evento"+ index).get("mensagem_futuro"));
        return e;
    }

    /**
     *  Dá update aos objetos da MIB.
     */
    public void update(){
        Iterator it = getEventosMib().getGrEventosEntry().getModel().iterator();

        /* Itera tabela */
        while(it.hasNext()) {
            /* Linha da tabela eventos */
            EventosMib.GrEventosEntryRow eventosRow = (EventosMib.GrEventosEntryRow) it.next();
            int index = eventosRow.getIndexRow().toInt(); // índice da linha da tabela eventos

            /* Linha da tabela tempos correspondente ao índice */
            EventosMib.TempoEntryRow temposRow = getEventosMib().getTempoEntry().getModel().getRow(new OID(String.valueOf(index)));
            Evento e = null;
            try {
                e = serializaEvento(index);
            } catch (NaoExisteEventoException naoExisteEventoException) {
                naoExisteEventoException.printStackTrace();
            }

            int time = getFrameTemporal(e);
            if (time == PASSADO) {
                temposRow.setAnos(new Integer32(calculaAnos(e.getDataFim())));
                temposRow.setMeses(new Integer32(calculaMeses(e.getDataFim())));
                temposRow.setSemanas(new Integer32(calculaSemanas(e.getDataFim())));
                temposRow.setDias(new Integer32(calculaDias(e.getDataFim())));
                temposRow.setHoras(new Integer32(calculaHoras(e.getDataFim())));
                temposRow.setMinutos(new Integer32(calculaMin(e.getDataFim())));
                eventosRow.setMensagem(new OctetString(concatMsg(calculaAnos(e.getDataFim()), calculaMeses(e.getDataFim()),
                                                                calculaSemanas(e.getDataFim()),calculaDias(e.getDataFim()),
                                                                calculaHoras(e.getDataFim()),calculaMin(e.getDataFim()),
                                                                e.getPassado())));
            } else if (time == FUTURO) {
                temposRow.setAnos(new Integer32(calculaAnos(e.getDataInicio())));
                temposRow.setMeses(new Integer32(calculaMeses(e.getDataInicio())));
                temposRow.setSemanas(new Integer32(calculaSemanas(e.getDataInicio())));
                temposRow.setDias(new Integer32(calculaDias(e.getDataInicio())));
                temposRow.setHoras(new Integer32(calculaHoras(e.getDataInicio())));
                temposRow.setMinutos(new Integer32(calculaMin(e.getDataInicio())));
                eventosRow.setMensagem(new OctetString(concatMsg(calculaAnos(e.getDataInicio()), calculaMeses(e.getDataInicio()),
                                                                calculaSemanas(e.getDataInicio()),calculaDias(e.getDataInicio()),
                                                                calculaHoras(e.getDataInicio()),calculaMin(e.getDataInicio()),
                                                                e.getFuturo())));
            } else if (time == PRESENTE) {
                temposRow.setAnos(new Integer32(0));
                temposRow.setMeses(new Integer32(0));
                temposRow.setSemanas(new Integer32(0));
                temposRow.setDias(new Integer32(0));
                temposRow.setHoras(new Integer32(0));
                temposRow.setMinutos(new Integer32(0));
                eventosRow.setMensagem(new OctetString(e.getPresente()));
            }
        }
    }


    /**
     * Calcula diferença de anos entre uma data e a data atual.
     * @param data
     * @return
     */
    private int calculaAnos (String data){
        LocalDateTime pdata = parseData(data);
        LocalDateTime now = LocalDateTime.now();
        long anos = ChronoUnit.YEARS.between(now, pdata);
        return (int)anos;
    }

    /**
     * Calcula diferença de meses entre uma data e a data atual.
     * @param data
     * @return
     */
    private int calculaMeses (String data){
        LocalDateTime pdata = parseData(data);
        LocalDateTime dateTime2 = LocalDateTime.now();
        long meses = ChronoUnit.MONTHS.between(dateTime2, pdata);
        return (int)meses;
    }

    /**
     * Calcula diferença de semanas entre uma data e a data atual.
     * @param data
     * @return
     */
    private int calculaSemanas (String data){
        LocalDateTime pdata = parseData(data);
        LocalDateTime now = LocalDateTime.now();
        long semanas = ChronoUnit.WEEKS.between(now,pdata);
        return (int)semanas;
    }

    /**
     * Calcula diferença de dias entre uma data e a data atual.
     * @param data
     * @return
     */
    private int calculaDias (String data){
        LocalDateTime pdata = parseData(data);
        LocalDateTime now = LocalDateTime.now();
        long dias = ChronoUnit.DAYS.between(now,pdata);
        return (int)dias;
    }

    /**
     * Calcula diferença de horas entre uma data e a data atual.
     * @param data
     * @return
     */
    private int calculaHoras(String data){
        LocalDateTime pdata = parseData(data);
        LocalDateTime now = LocalDateTime.now();
        long horas = ChronoUnit.HOURS.between(now, pdata);
        return (int)horas;
    }

    /**
     * Calcula diferança de minutos entre uma data e a data atual.
     * @param data
     * @return
     */
    private int calculaMin(String data){
        LocalDateTime pdata = parseData(data);
        LocalDateTime now = LocalDateTime.now();
        long minutos = java.time.Duration.between(now,pdata).toMinutes();
        return (int)minutos;
    }


//--AgentGen BEGIN=_METHODS
//--AgentGen END

//--AgentGen BEGIN=_CLASSES
//--AgentGen END

//--AgentGen BEGIN=_END
//--AgentGen END

}

