

//--AgentGen BEGIN=_BEGIN
//--AgentGen END

import org.snmp4j.smi.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.agent.*;
import org.snmp4j.agent.mo.*;
import org.snmp4j.agent.mo.snmp.*;
import org.snmp4j.agent.mo.snmp.smi.*;
import org.snmp4j.agent.request.*;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.agent.mo.snmp.tc.*;



//--AgentGen BEGIN=_BEGIN
//--AgentGen END

import org.snmp4j.smi.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.agent.*;
import org.snmp4j.agent.mo.*;
import org.snmp4j.agent.mo.snmp.*;
import org.snmp4j.agent.mo.snmp.smi.*;
import org.snmp4j.agent.request.*;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogAdapter;
import org.snmp4j.agent.mo.snmp.tc.*;



//--AgentGen BEGIN=_IMPORT
//--AgentGen END

public class EventosMib
//--AgentGen BEGIN=_EXTENDS
//--AgentGen END
        implements MOGroup
//--AgentGen BEGIN=_IMPLEMENTS
//--AgentGen END
{

    private static final LogAdapter LOGGER =
            LogFactory.getLogger(EventosMib.class);

//--AgentGen BEGIN=_STATIC
//--AgentGen END

    // Factory
    private MOFactory moFactory =
            DefaultMOFactory.getInstance();

    // Constants

    /**
     * OID of this MIB module for usage which can be
     * used for its identification.
     */
    public static final OID oidEventosMib =
            new OID(new int[] { 1,3,6,1,2,1,60,10 });

    // Identities
    // Scalars
    // Tables

    // Notifications

    // Enumerations




    // TextualConventions

    // Scalars

    // Tables
    public static final OID oidGrEventosEntry =
            new OID(new int[] { 1,3,6,1,2,1,60,10,4,1 });

    // Index OID definitions
    public static final OID oidIndex =
            new OID(new int[] { 1,3,6,1,2,1,60,10,4,1,1 });

    // Column TC definitions for grEventosEntry:

    // Column sub-identifier definitions for grEventosEntry:
    public static final int colIndex = 1;
    public static final int colDescricao = 2;
    public static final int colMensagem = 3;

    // Column index definitions for grEventosEntry:
    public static final int idxIndex = 0;
    public static final int idxDescricao = 1;
    public static final int idxMensagem = 2;

    private MOTableSubIndex[] grEventosEntryIndexes;
    private MOTableIndex grEventosEntryIndex;

    @SuppressWarnings(value={"rawtypes"})
    private MOTable<GrEventosEntryRow, MOColumn,
            MOTableModel<GrEventosEntryRow>> grEventosEntry;
    private MOTableModel<GrEventosEntryRow> grEventosEntryModel;
    public static final OID oidTempoEntry =
            new OID(new int[] { 1,3,6,1,2,1,60,10,5,1 });

    // Index OID definitions
    //public static final OID oidIndex =
    //  new OID(new int[] { 1,3,6,1,2,1,60,10,4,1,1 });

    // Column TC definitions for tempoEntry:

    // Column sub-identifier definitions for tempoEntry:
    public static final int colAnos = 1;
    public static final int colMeses = 2;
    public static final int colSemanas = 3;
    public static final int colDias = 4;
    public static final int colHoras = 5;
    public static final int colMinutos = 6;

    // Column index definitions for tempoEntry:
    public static final int idxAnos = 0;
    public static final int idxMeses = 1;
    public static final int idxSemanas = 2;
    public static final int idxDias = 3;
    public static final int idxHoras = 4;
    public static final int idxMinutos = 5;

    private MOTableSubIndex[] tempoEntryIndexes;
    private MOTableIndex tempoEntryIndex;

    @SuppressWarnings(value={"rawtypes"})
    private MOTable<TempoEntryRow, MOColumn,
            MOTableModel<TempoEntryRow>> tempoEntry;
    private MOTableModel<TempoEntryRow> tempoEntryModel;


//--AgentGen BEGIN=_MEMBERS
//--AgentGen END

    /**
     * Constructs a EventosMib instance without actually creating its
     * <code>ManagedObject</code> instances. This has to be done in a
     * sub-class constructor or after construction by calling
     * {@link #createMO(MOFactory moFactory)}.
     */
    protected EventosMib() {
//--AgentGen BEGIN=_DEFAULTCONSTRUCTOR
//--AgentGen END
    }

    /**
     * Constructs a EventosMib instance and actually creates its
     * <code>ManagedObject</code> instances using the supplied
     * <code>MOFactory</code> (by calling
     * {@link #createMO(MOFactory moFactory)}).
     * @param moFactory
     *    the <code>MOFactory</code> to be used to create the
     *    managed objects for this module.
     */
    public EventosMib(MOFactory moFactory) {
        this();
        //--AgentGen BEGIN=_FACTORYCONSTRUCTOR::factoryWrapper
        //--AgentGen END
        this.moFactory = moFactory;
        createMO(moFactory);
//--AgentGen BEGIN=_FACTORYCONSTRUCTOR
//--AgentGen END
    }

//--AgentGen BEGIN=_CONSTRUCTORS
//--AgentGen END

    /**
     * Create the ManagedObjects defined for this MIB module
     * using the specified {@link MOFactory}.
     * @param moFactory
     *    the <code>MOFactory</code> instance to use for object
     *    creation.
     */
    protected void createMO(MOFactory moFactory) {
        addTCsToFactory(moFactory);
        createGrEventosEntry(moFactory);
        createTempoEntry(moFactory);
    }



    @SuppressWarnings(value={"rawtypes"})
    public MOTable<GrEventosEntryRow,MOColumn,MOTableModel<GrEventosEntryRow>> getGrEventosEntry() {
        return grEventosEntry;
    }


    @SuppressWarnings(value={"unchecked"})
    private void createGrEventosEntry(MOFactory moFactory) {
        // Index definition
        grEventosEntryIndexes =
                new MOTableSubIndex[] {
                        moFactory.createSubIndex(oidIndex,
                                SMIConstants.SYNTAX_INTEGER, 1, 1)
                };

        grEventosEntryIndex =
                moFactory.createIndex(grEventosEntryIndexes,
                        false,
                        new MOTableIndexValidator() {
                            public boolean isValidIndex(OID index) {
                                boolean isValidIndex = true;
                                //--AgentGen BEGIN=grEventosEntry::isValidIndex
                                //--AgentGen END
                                return isValidIndex;
                            }
                        });

        // Columns
        MOColumn<?>[] grEventosEntryColumns = new MOColumn<?>[3];
        grEventosEntryColumns[idxIndex] =
                moFactory.createColumn(colIndex,
                        SMIConstants.SYNTAX_COUNTER32,
                        moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
        grEventosEntryColumns[idxDescricao] =
                moFactory.createColumn(colDescricao,
                        SMIConstants.SYNTAX_OCTET_STRING,
                        moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
        grEventosEntryColumns[idxMensagem] =
                moFactory.createColumn(colMensagem,
                        SMIConstants.SYNTAX_INTEGER,
                        moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
        // Table model
        grEventosEntryModel =
                moFactory.createTableModel(oidGrEventosEntry,
                        grEventosEntryIndex,
                        grEventosEntryColumns);
        ((MOMutableTableModel<GrEventosEntryRow>)grEventosEntryModel).setRowFactory(
                new GrEventosEntryRowFactory());
        grEventosEntry =
                moFactory.createTable(oidGrEventosEntry,
                        grEventosEntryIndex,
                        grEventosEntryColumns,
                        grEventosEntryModel);
    }

    @SuppressWarnings(value={"rawtypes"})
    public MOTable<TempoEntryRow,MOColumn,MOTableModel<TempoEntryRow>> getTempoEntry() {
        return tempoEntry;
    }


    @SuppressWarnings(value={"unchecked"})
    private void createTempoEntry(MOFactory moFactory) {
        // Index definition
        tempoEntryIndexes =
                new MOTableSubIndex[] {
                        moFactory.createSubIndex(oidIndex,
                                SMIConstants.SYNTAX_INTEGER, 1, 1)
                };

        tempoEntryIndex =
                moFactory.createIndex(tempoEntryIndexes,
                        false,
                        new MOTableIndexValidator() {
                            public boolean isValidIndex(OID index) {
                                boolean isValidIndex = true;
                                //--AgentGen BEGIN=tempoEntry::isValidIndex
                                //--AgentGen END
                                return isValidIndex;
                            }
                        });

        // Columns
        MOColumn<?>[] tempoEntryColumns = new MOColumn<?>[6];
        tempoEntryColumns[idxAnos] =
                moFactory.createColumn(colAnos,
                        SMIConstants.SYNTAX_INTEGER,
                        moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
        tempoEntryColumns[idxMeses] =
                moFactory.createColumn(colMeses,
                        SMIConstants.SYNTAX_INTEGER,
                        moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
        tempoEntryColumns[idxSemanas] =
                moFactory.createColumn(colSemanas,
                        SMIConstants.SYNTAX_INTEGER,
                        moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
        tempoEntryColumns[idxDias] =
                moFactory.createColumn(colDias,
                        SMIConstants.SYNTAX_INTEGER,
                        moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
        tempoEntryColumns[idxHoras] =
                moFactory.createColumn(colHoras,
                        SMIConstants.SYNTAX_INTEGER,
                        moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
        tempoEntryColumns[idxMinutos] =
                moFactory.createColumn(colMinutos,
                        SMIConstants.SYNTAX_INTEGER,
                        moFactory.createAccess(MOAccessImpl.ACCESSIBLE_FOR_READ_ONLY));
        // Table model
        tempoEntryModel =
                moFactory.createTableModel(oidTempoEntry,
                        tempoEntryIndex,
                        tempoEntryColumns);
        ((MOMutableTableModel<TempoEntryRow>)tempoEntryModel).setRowFactory(
                new TempoEntryRowFactory());
        tempoEntry =
                moFactory.createTable(oidTempoEntry,
                        tempoEntryIndex,
                        tempoEntryColumns,
                        tempoEntryModel);
    }



    public void registerMOs(MOServer server, OctetString context)
            throws DuplicateRegistrationException
    {
        // Scalar Objects
        server.register(this.grEventosEntry, context);
        server.register(this.tempoEntry, context);
//--AgentGen BEGIN=_registerMOs
//--AgentGen END
    }

    public void unregisterMOs(MOServer server, OctetString context) {
        // Scalar Objects
        server.unregister(this.grEventosEntry, context);
        server.unregister(this.tempoEntry, context);
//--AgentGen BEGIN=_unregisterMOs
//--AgentGen END
    }

    // Notifications

    // Scalars

    // Value Validators


    // Rows and Factories

    public class GrEventosEntryRow extends DefaultMOMutableRow2PC {

        //--AgentGen BEGIN=grEventosEntry::RowMembers
        //--AgentGen END

        public GrEventosEntryRow(OID index, Variable[] values) {
            super(index, values);
            //--AgentGen BEGIN=grEventosEntry::RowConstructor
            //--AgentGen END
        }

        public Counter32 getIndexRow() {
            //--AgentGen BEGIN=grEventosEntry::getIndex
            //--AgentGen END
            return (Counter32) super.getValue(idxIndex);
        }

        public void setIndex(Counter32 newColValue) {
            //--AgentGen BEGIN=grEventosEntry::setIndex
            //--AgentGen END
            super.setValue(idxIndex, newColValue);
        }

        public OctetString getDescricao() {
            //--AgentGen BEGIN=grEventosEntry::getDescricao
            //--AgentGen END
            return (OctetString) super.getValue(idxDescricao);
        }

        public void setDescricao(OctetString newColValue) {
            //--AgentGen BEGIN=grEventosEntry::setDescricao
            //--AgentGen END
            super.setValue(idxDescricao, newColValue);
        }

        public OctetString getMensagem() {
            //--AgentGen BEGIN=grEventosEntry::getMensagem
            //--AgentGen END
            return (OctetString) super.getValue(idxMensagem);
        }

        public void setMensagem(OctetString newColValue) {
            //--AgentGen BEGIN=grEventosEntry::setMensagem
            //--AgentGen END
            super.setValue(idxMensagem, newColValue);
        }

        public Variable getValue(int column) {
            //--AgentGen BEGIN=grEventosEntry::RowGetValue
            //--AgentGen END
            switch(column) {
                case idxIndex:
                    return getIndexRow();
                case idxDescricao:
                    return getDescricao();
                case idxMensagem:
                    return getMensagem();
                default:
                    return super.getValue(column);
            }
        }

        public void setValue(int column, Variable value) {
            //--AgentGen BEGIN=grEventosEntry::RowSetValue
            //--AgentGen END
            switch(column) {
                case idxIndex:
                    setIndex((Counter32)value);
                    break;
                case idxDescricao:
                    setDescricao((OctetString)value);
                    break;
                case idxMensagem:
                    setMensagem((OctetString) value);
                    break;
                default:
                    super.setValue(column, value);
            }
        }

        //--AgentGen BEGIN=grEventosEntry::Row
        //--AgentGen END
    }

    public class GrEventosEntryRowFactory implements MOTableRowFactory<GrEventosEntryRow>
    {
        public synchronized GrEventosEntryRow createRow(OID index, Variable[] values)
                throws UnsupportedOperationException
        {
            GrEventosEntryRow row = new GrEventosEntryRow(index, values);
            //--AgentGen BEGIN=grEventosEntry::createRow
            //--AgentGen END
            return row;
        }

        public synchronized void freeRow(GrEventosEntryRow row) {
            //--AgentGen BEGIN=grEventosEntry::freeRow
            //--AgentGen END
        }

        //--AgentGen BEGIN=grEventosEntry::RowFactory
        //--AgentGen END
    }

    public class TempoEntryRow extends DefaultMOMutableRow2PC {

        //--AgentGen BEGIN=tempoEntry::RowMembers
        //--AgentGen END

        public TempoEntryRow(OID index, Variable[] values) {
            super(index, values);
            //--AgentGen BEGIN=tempoEntry::RowConstructor
            //--AgentGen END
        }

        public Integer32 getAnos() {
            //--AgentGen BEGIN=tempoEntry::getAnos
            //--AgentGen END
            return (Integer32) super.getValue(idxAnos);
        }

        public void setAnos(Integer32 newColValue) {
            //--AgentGen BEGIN=tempoEntry::setAnos
            //--AgentGen END
            super.setValue(idxAnos, newColValue);
        }

        public Integer32 getMeses() {
            //--AgentGen BEGIN=tempoEntry::getMeses
            //--AgentGen END
            return (Integer32) super.getValue(idxMeses);
        }

        public void setMeses(Integer32 newColValue) {
            //--AgentGen BEGIN=tempoEntry::setMeses
            //--AgentGen END
            super.setValue(idxMeses, newColValue);
        }

        public Integer32 getSemanas() {
            //--AgentGen BEGIN=tempoEntry::getSemanas
            //--AgentGen END
            return (Integer32) super.getValue(idxSemanas);
        }

        public void setSemanas(Integer32 newColValue) {
            //--AgentGen BEGIN=tempoEntry::setSemanas
            //--AgentGen END
            super.setValue(idxSemanas, newColValue);
        }

        public Integer32 getDias() {
            //--AgentGen BEGIN=tempoEntry::getDias
            //--AgentGen END
            return (Integer32) super.getValue(idxDias);
        }

        public void setDias(Integer32 newColValue) {
            //--AgentGen BEGIN=tempoEntry::setDias
            //--AgentGen END
            super.setValue(idxDias, newColValue);
        }

        public Integer32 getHoras() {
            //--AgentGen BEGIN=tempoEntry::getHoras
            //--AgentGen END
            return (Integer32) super.getValue(idxHoras);
        }

        public void setHoras(Integer32 newColValue) {
            //--AgentGen BEGIN=tempoEntry::setHoras
            //--AgentGen END
            super.setValue(idxHoras, newColValue);
        }

        public Integer32 getMinutos() {
            //--AgentGen BEGIN=tempoEntry::getMinutos
            //--AgentGen END
            return (Integer32) super.getValue(idxMinutos);
        }

        public void setMinutos(Integer32 newColValue) {
            //--AgentGen BEGIN=tempoEntry::setMinutos
            //--AgentGen END
            super.setValue(idxMinutos, newColValue);
        }

        public Variable getValue(int column) {
            //--AgentGen BEGIN=tempoEntry::RowGetValue
            //--AgentGen END
            switch(column) {
                case idxAnos:
                    return getAnos();
                case idxMeses:
                    return getMeses();
                case idxSemanas:
                    return getSemanas();
                case idxDias:
                    return getDias();
                case idxHoras:
                    return getHoras();
                case idxMinutos:
                    return getMinutos();
                default:
                    return super.getValue(column);
            }
        }

        public void setValue(int column, Variable value) {
            //--AgentGen BEGIN=tempoEntry::RowSetValue
            //--AgentGen END
            switch(column) {
                case idxAnos:
                    setAnos((Integer32)value);
                    break;
                case idxMeses:
                    setMeses((Integer32)value);
                    break;
                case idxSemanas:
                    setSemanas((Integer32)value);
                    break;
                case idxDias:
                    setDias((Integer32)value);
                    break;
                case idxHoras:
                    setHoras((Integer32)value);
                    break;
                case idxMinutos:
                    setMinutos((Integer32)value);
                    break;
                default:
                    super.setValue(column, value);
            }
        }

        //--AgentGen BEGIN=tempoEntry::Row
        //--AgentGen END
    }

    public class TempoEntryRowFactory implements MOTableRowFactory<TempoEntryRow>
    {
        public synchronized TempoEntryRow createRow(OID index, Variable[] values)
                throws UnsupportedOperationException
        {
            TempoEntryRow row = new TempoEntryRow(index, values);
            //--AgentGen BEGIN=tempoEntry::createRow
            //--AgentGen END
            return row;
        }

        public synchronized void freeRow(TempoEntryRow row) {
            //--AgentGen BEGIN=tempoEntry::freeRow
            //--AgentGen END
        }

        //--AgentGen BEGIN=tempoEntry::RowFactory
        //--AgentGen END
    }


//--AgentGen BEGIN=_METHODS
//--AgentGen END

    // Textual Definitions of MIB module EventosMib
    protected void addTCsToFactory(MOFactory moFactory) {
    }


//--AgentGen BEGIN=_TC_CLASSES_IMPORTED_MODULES_BEGIN
//--AgentGen END

    // Textual Definitions of other MIB modules
    public void addImportedTCsToFactory(MOFactory moFactory) {
    }


//--AgentGen BEGIN=_TC_CLASSES_IMPORTED_MODULES_END
//--AgentGen END

//--AgentGen BEGIN=_CLASSES
//--AgentGen END

//--AgentGen BEGIN=_END
//--AgentGen END
}

