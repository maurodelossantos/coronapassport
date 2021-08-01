package es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts;

import android.os.Parcel;
import android.os.Parcelable;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple12;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class MedicalRecord extends Contract{
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_ADDDOSIS = "addDosis";

    public static final String FUNC_ADDREACCION = "addReaccion";

    public static final String FUNC_ADDRECORD = "addRecord";

    public static final String FUNC_ADDTEST = "addTest";

    public static final String FUNC_BALACEOFCORONACOINS = "balaceOfCoronacoins";

    public static final String FUNC_CORONACOIN = "coronacoin";

    public static final String FUNC_GETFREERECORD = "getFreeRecord";

    public static final String FUNC_GETRECORD = "getRecord";

    public static final String FUNC_ISPATIENT = "isPatient";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PATIENTCOUNT = "patientCount";

    public static final String FUNC_RECORDCOUNT = "recordCount";

    public static final String FUNC_SETCORONACOINREWARDS = "setCoronacoinRewards";

    public static final String FUNC_TOKENADDRESS = "tokenAddress";

    public static final String FUNC_TOKENREWARDBIGAMOUNT = "tokenRewardBigAmount";

    public static final String FUNC_TOKENREWARDMEDIUMAMOUNT = "tokenRewardMediumAmount";

    public static final String FUNC_TOKENREWARDSMALLAMOUNT = "tokenRewardSmallAmount";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event DOSEADDED_EVENT = new Event("DoseAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Dosis>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event PATIENTRECORDACCESED_EVENT = new Event("PatientRecordAccesed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event PATIENTRECORDADDED_EVENT = new Event("PatientRecordAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event REACTIONADDED_EVENT = new Event("ReactionAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event REWARDPAID_EVENT = new Event("RewardPaid", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event TESTADDED_EVENT = new Event("TestAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Prueba>() {}));
    ;

    @Deprecated
    protected MedicalRecord(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MedicalRecord(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected MedicalRecord(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected MedicalRecord(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<DoseAddedEventResponse> getDoseAddedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DOSEADDED_EVENT, transactionReceipt);
        ArrayList<DoseAddedEventResponse> responses = new ArrayList<DoseAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DoseAddedEventResponse typedResponse = new DoseAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.recordID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.newDose = (Dosis) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DoseAddedEventResponse> doseAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DoseAddedEventResponse>() {
            @Override
            public DoseAddedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DOSEADDED_EVENT, log);
                DoseAddedEventResponse typedResponse = new DoseAddedEventResponse();
                typedResponse.log = log;
                typedResponse.recordID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.newDose = (Dosis) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<DoseAddedEventResponse> doseAddedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DOSEADDED_EVENT));
        return doseAddedEventFlowable(filter);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public List<PatientRecordAccesedEventResponse> getPatientRecordAccesedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PATIENTRECORDACCESED_EVENT, transactionReceipt);
        ArrayList<PatientRecordAccesedEventResponse> responses = new ArrayList<PatientRecordAccesedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PatientRecordAccesedEventResponse typedResponse = new PatientRecordAccesedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.recordID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<PatientRecordAccesedEventResponse> patientRecordAccesedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, PatientRecordAccesedEventResponse>() {
            @Override
            public PatientRecordAccesedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PATIENTRECORDACCESED_EVENT, log);
                PatientRecordAccesedEventResponse typedResponse = new PatientRecordAccesedEventResponse();
                typedResponse.log = log;
                typedResponse.recordID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<PatientRecordAccesedEventResponse> patientRecordAccesedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PATIENTRECORDACCESED_EVENT));
        return patientRecordAccesedEventFlowable(filter);
    }

    public List<PatientRecordAddedEventResponse> getPatientRecordAddedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PATIENTRECORDADDED_EVENT, transactionReceipt);
        ArrayList<PatientRecordAddedEventResponse> responses = new ArrayList<PatientRecordAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PatientRecordAddedEventResponse typedResponse = new PatientRecordAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.recordID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<PatientRecordAddedEventResponse> patientRecordAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, PatientRecordAddedEventResponse>() {
            @Override
            public PatientRecordAddedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PATIENTRECORDADDED_EVENT, log);
                PatientRecordAddedEventResponse typedResponse = new PatientRecordAddedEventResponse();
                typedResponse.log = log;
                typedResponse.recordID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<PatientRecordAddedEventResponse> patientRecordAddedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PATIENTRECORDADDED_EVENT));
        return patientRecordAddedEventFlowable(filter);
    }

    public List<ReactionAddedEventResponse> getReactionAddedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REACTIONADDED_EVENT, transactionReceipt);
        ArrayList<ReactionAddedEventResponse> responses = new ArrayList<ReactionAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ReactionAddedEventResponse typedResponse = new ReactionAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.recordID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.reaccion = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ReactionAddedEventResponse> reactionAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ReactionAddedEventResponse>() {
            @Override
            public ReactionAddedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REACTIONADDED_EVENT, log);
                ReactionAddedEventResponse typedResponse = new ReactionAddedEventResponse();
                typedResponse.log = log;
                typedResponse.recordID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.reaccion = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ReactionAddedEventResponse> reactionAddedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REACTIONADDED_EVENT));
        return reactionAddedEventFlowable(filter);
    }

    public List<RewardPaidEventResponse> getRewardPaidEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REWARDPAID_EVENT, transactionReceipt);
        ArrayList<RewardPaidEventResponse> responses = new ArrayList<RewardPaidEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RewardPaidEventResponse typedResponse = new RewardPaidEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RewardPaidEventResponse> rewardPaidEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RewardPaidEventResponse>() {
            @Override
            public RewardPaidEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(REWARDPAID_EVENT, log);
                RewardPaidEventResponse typedResponse = new RewardPaidEventResponse();
                typedResponse.log = log;
                typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RewardPaidEventResponse> rewardPaidEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REWARDPAID_EVENT));
        return rewardPaidEventFlowable(filter);
    }

    public List<TestAddedEventResponse> getTestAddedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TESTADDED_EVENT, transactionReceipt);
        ArrayList<TestAddedEventResponse> responses = new ArrayList<TestAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TestAddedEventResponse typedResponse = new TestAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.recordID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.newTest = (Prueba) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TestAddedEventResponse> testAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TestAddedEventResponse>() {
            @Override
            public TestAddedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TESTADDED_EVENT, log);
                TestAddedEventResponse typedResponse = new TestAddedEventResponse();
                typedResponse.log = log;
                typedResponse.recordID = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.newTest = (Prueba) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<TestAddedEventResponse> testAddedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TESTADDED_EVENT));
        return testAddedEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> addDosis(String _direccionPaciente, BigInteger _nLote, String _proveedor, String _lugar, String _timestamp) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDDOSIS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _direccionPaciente), 
                new org.web3j.abi.datatypes.generated.Uint256(_nLote), 
                new org.web3j.abi.datatypes.Utf8String(_proveedor), 
                new org.web3j.abi.datatypes.Utf8String(_lugar), 
                new org.web3j.abi.datatypes.Utf8String(_timestamp)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addReaccion(String _direccionPaciente, String _reaccion) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDREACCION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _direccionPaciente), 
                new org.web3j.abi.datatypes.Utf8String(_reaccion)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addRecord(BigInteger _idNacional, BigInteger _idMedico, String _direccionPaciente, String _nombre, String _apellido1, String _apellido2, String _fechaNacimiento, String _genero, String _pais, String _contacto) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDRECORD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint32(_idNacional), 
                new org.web3j.abi.datatypes.generated.Uint32(_idMedico), 
                new org.web3j.abi.datatypes.Address(160, _direccionPaciente), 
                new org.web3j.abi.datatypes.Utf8String(_nombre), 
                new org.web3j.abi.datatypes.Utf8String(_apellido1), 
                new org.web3j.abi.datatypes.Utf8String(_apellido2), 
                new org.web3j.abi.datatypes.Utf8String(_fechaNacimiento), 
                new org.web3j.abi.datatypes.Utf8String(_genero), 
                new org.web3j.abi.datatypes.Utf8String(_pais), 
                new org.web3j.abi.datatypes.Utf8String(_contacto)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addTest(String _direccionPaciente, BigInteger _idPrueba, String _tipo, String _lugar, String _timestamp, String _resultado) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDTEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _direccionPaciente), 
                new org.web3j.abi.datatypes.generated.Uint256(_idPrueba), 
                new org.web3j.abi.datatypes.Utf8String(_tipo), 
                new org.web3j.abi.datatypes.Utf8String(_lugar), 
                new org.web3j.abi.datatypes.Utf8String(_timestamp), 
                new org.web3j.abi.datatypes.Utf8String(_resultado)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> balaceOfCoronacoins(String _patientAddress) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BALACEOFCORONACOINS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _patientAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> coronacoin() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CORONACOIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple12<BigInteger, BigInteger, String, String, String, String, String, String, String, List<Dosis>, List<Prueba>, List<String>>> getFreeRecord(String _direccionPaciente) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETFREERECORD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _direccionPaciente)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}, new TypeReference<Uint32>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<DynamicArray<Dosis>>() {}, new TypeReference<DynamicArray<Prueba>>() {}, new TypeReference<DynamicArray<Utf8String>>() {}));
        return new RemoteFunctionCall<Tuple12<BigInteger, BigInteger, String, String, String, String, String, String, String, List<Dosis>, List<Prueba>, List<String>>>(function,
                new Callable<Tuple12<BigInteger, BigInteger, String, String, String, String, String, String, String, List<Dosis>, List<Prueba>, List<String>>>() {
                    @Override
                    public Tuple12<BigInteger, BigInteger, String, String, String, String, String, String, String, List<Dosis>, List<Prueba>, List<String>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple12<BigInteger, BigInteger, String, String, String, String, String, String, String, List<Dosis>, List<Prueba>, List<String>>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (String) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (String) results.get(5).getValue(), 
                                (String) results.get(6).getValue(), 
                                (String) results.get(7).getValue(), 
                                (String) results.get(8).getValue(), 
                                convertToNative((List<Dosis>) results.get(9).getValue()), 
                                convertToNative((List<Prueba>) results.get(10).getValue()), 
                                convertToNative((List<Utf8String>) results.get(11).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> getRecord(String _direccionPaciente) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GETRECORD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _direccionPaciente)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> isPatient(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ISPATIENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> patientCount() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_PATIENTCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> recordCount() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_RECORDCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setCoronacoinRewards(BigInteger _tokenRewardSmall, BigInteger _tokenRewardMedium, BigInteger _tokenRewardBig) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SETCORONACOINREWARDS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_tokenRewardSmall), 
                new org.web3j.abi.datatypes.generated.Uint256(_tokenRewardMedium), 
                new org.web3j.abi.datatypes.generated.Uint256(_tokenRewardBig)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> tokenAddress() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOKENADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> tokenRewardBigAmount() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOKENREWARDBIGAMOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> tokenRewardMediumAmount() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOKENREWARDMEDIUMAMOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> tokenRewardSmallAmount() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOKENREWARDSMALLAMOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static MedicalRecord load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MedicalRecord(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static MedicalRecord load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MedicalRecord(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static MedicalRecord load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new MedicalRecord(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static MedicalRecord load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new MedicalRecord(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class Dosis extends DynamicStruct implements Parcelable {
        public BigInteger nLote;

        public String proveedor;

        public String lugar;

        public String timestamp;

        public Dosis(BigInteger nLote, String proveedor, String lugar, String timestamp) {
            super(new org.web3j.abi.datatypes.generated.Uint256(nLote),new org.web3j.abi.datatypes.Utf8String(proveedor),new org.web3j.abi.datatypes.Utf8String(lugar),new org.web3j.abi.datatypes.Utf8String(timestamp));
            this.nLote = nLote;
            this.proveedor = proveedor;
            this.lugar = lugar;
            this.timestamp = timestamp;
        }

        public Dosis(Uint256 nLote, Utf8String proveedor, Utf8String lugar, Utf8String timestamp) {
            super(nLote,proveedor,lugar,timestamp);
            this.nLote = nLote.getValue();
            this.proveedor = proveedor.getValue();
            this.lugar = lugar.getValue();
            this.timestamp = timestamp.getValue();
        }

        protected Dosis(Parcel in) {
            proveedor = in.readString();
            lugar = in.readString();
            timestamp = in.readString();
            nLote = BigInteger.valueOf(in.readLong());
        }

        public static final Creator<Dosis> CREATOR = new Creator<Dosis>() {
            @Override
            public Dosis createFromParcel(Parcel in) {
                return new Dosis(in);
            }

            @Override
            public Dosis[] newArray(int size) {
                return new Dosis[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(proveedor);
            parcel.writeString(lugar);
            parcel.writeString(timestamp);
            parcel.writeLong((nLote.longValue()));
        }
    }

    public static class Prueba extends DynamicStruct implements Parcelable{
        public BigInteger idPrueba;

        public String tipo;

        public String resultado;

        public String timestamp;

        public String lugar;

        public Prueba(BigInteger idPrueba, String tipo, String resultado, String timestamp, String lugar) {
            super(new org.web3j.abi.datatypes.generated.Uint256(idPrueba),new org.web3j.abi.datatypes.Utf8String(tipo),new org.web3j.abi.datatypes.Utf8String(resultado),new org.web3j.abi.datatypes.Utf8String(timestamp),new org.web3j.abi.datatypes.Utf8String(lugar));
            this.idPrueba = idPrueba;
            this.tipo = tipo;
            this.resultado = resultado;
            this.timestamp = timestamp;
            this.lugar = lugar;
        }

        public Prueba(Uint256 idPrueba, Utf8String tipo, Utf8String resultado, Utf8String timestamp, Utf8String lugar) {
            super(idPrueba,tipo,resultado,timestamp,lugar);
            this.idPrueba = idPrueba.getValue();
            this.tipo = tipo.getValue();
            this.resultado = resultado.getValue();
            this.timestamp = timestamp.getValue();
            this.lugar = lugar.getValue();
        }

        protected Prueba(Parcel in) {
            tipo = in.readString();
            resultado = in.readString();
            timestamp = in.readString();
            lugar = in.readString();
            idPrueba = BigInteger.valueOf(in.readLong());
        }

        public static final Creator<Prueba> CREATOR = new Creator<Prueba>() {
            @Override
            public Prueba createFromParcel(Parcel in) {
                return new Prueba(in);
            }

            @Override
            public Prueba[] newArray(int size) {
                return new Prueba[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(tipo);
            parcel.writeString(resultado);
            parcel.writeString(timestamp);
            parcel.writeString(lugar);
            parcel.writeLong(idPrueba.longValue());
        }
    }

    public static class DoseAddedEventResponse extends BaseEventResponse {
        public BigInteger recordID;

        public String patientAddress;

        public Dosis newDose;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class PatientRecordAccesedEventResponse extends BaseEventResponse {
        public BigInteger recordID;

        public String patientAddress;
    }

    public static class PatientRecordAddedEventResponse extends BaseEventResponse {
        public BigInteger recordID;

        public String patientAddress;
    }

    public static class ReactionAddedEventResponse extends BaseEventResponse {
        public BigInteger recordID;

        public String patientAddress;

        public String reaccion;
    }

    public static class RewardPaidEventResponse extends BaseEventResponse {
        public String patientAddress;
    }

    public static class TestAddedEventResponse extends BaseEventResponse {
        public BigInteger recordID;

        public String patientAddress;

        public Prueba newTest;
    }
}
