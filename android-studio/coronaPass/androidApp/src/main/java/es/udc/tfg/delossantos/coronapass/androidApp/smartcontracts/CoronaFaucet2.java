package es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
public class CoronaFaucet2 extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_GIVEMEETHER = "giveMeEther";

    public static final String FUNC_GIVEMEETHERSIMPLE = "giveMeEtherSimple";

    public static final Event ETHERGIVEN_EVENT = new Event("etherGiven", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final Event ETHERMININUM_EVENT = new Event("etherMininum", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event ETHEROUTOFSTOCK_EVENT = new Event("etherOutOfStock", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event ETHERRECEIVED_EVENT = new Event("etherReceived", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected CoronaFaucet2(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CoronaFaucet2(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CoronaFaucet2(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CoronaFaucet2(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<EtherGivenEventResponse> getEtherGivenEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ETHERGIVEN_EVENT, transactionReceipt);
        ArrayList<EtherGivenEventResponse> responses = new ArrayList<EtherGivenEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EtherGivenEventResponse typedResponse = new EtherGivenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<EtherGivenEventResponse> etherGivenEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, EtherGivenEventResponse>() {
            @Override
            public EtherGivenEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ETHERGIVEN_EVENT, log);
                EtherGivenEventResponse typedResponse = new EtherGivenEventResponse();
                typedResponse.log = log;
                typedResponse.patientAddress = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<EtherGivenEventResponse> etherGivenEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ETHERGIVEN_EVENT));
        return etherGivenEventFlowable(filter);
    }

    public List<EtherMininumEventResponse> getEtherMininumEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ETHERMININUM_EVENT, transactionReceipt);
        ArrayList<EtherMininumEventResponse> responses = new ArrayList<EtherMininumEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EtherMininumEventResponse typedResponse = new EtherMininumEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.etherRemaining = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<EtherMininumEventResponse> etherMininumEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, EtherMininumEventResponse>() {
            @Override
            public EtherMininumEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ETHERMININUM_EVENT, log);
                EtherMininumEventResponse typedResponse = new EtherMininumEventResponse();
                typedResponse.log = log;
                typedResponse.etherRemaining = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<EtherMininumEventResponse> etherMininumEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ETHERMININUM_EVENT));
        return etherMininumEventFlowable(filter);
    }

    public List<EtherOutOfStockEventResponse> getEtherOutOfStockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ETHEROUTOFSTOCK_EVENT, transactionReceipt);
        ArrayList<EtherOutOfStockEventResponse> responses = new ArrayList<EtherOutOfStockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EtherOutOfStockEventResponse typedResponse = new EtherOutOfStockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.etherRemaining = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<EtherOutOfStockEventResponse> etherOutOfStockEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, EtherOutOfStockEventResponse>() {
            @Override
            public EtherOutOfStockEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ETHEROUTOFSTOCK_EVENT, log);
                EtherOutOfStockEventResponse typedResponse = new EtherOutOfStockEventResponse();
                typedResponse.log = log;
                typedResponse.etherRemaining = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<EtherOutOfStockEventResponse> etherOutOfStockEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ETHEROUTOFSTOCK_EVENT));
        return etherOutOfStockEventFlowable(filter);
    }

    public List<EtherReceivedEventResponse> getEtherReceivedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ETHERRECEIVED_EVENT, transactionReceipt);
        ArrayList<EtherReceivedEventResponse> responses = new ArrayList<EtherReceivedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EtherReceivedEventResponse typedResponse = new EtherReceivedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<EtherReceivedEventResponse> etherReceivedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, EtherReceivedEventResponse>() {
            @Override
            public EtherReceivedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ETHERRECEIVED_EVENT, log);
                EtherReceivedEventResponse typedResponse = new EtherReceivedEventResponse();
                typedResponse.log = log;
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<EtherReceivedEventResponse> etherReceivedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ETHERRECEIVED_EVENT));
        return etherReceivedEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> giveMeEther() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GIVEMEETHER, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> giveMeEtherSimple() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GIVEMEETHERSIMPLE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static CoronaFaucet2 load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CoronaFaucet2(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CoronaFaucet2 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CoronaFaucet2(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CoronaFaucet2 load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CoronaFaucet2(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CoronaFaucet2 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CoronaFaucet2(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class EtherGivenEventResponse extends BaseEventResponse {
        public String patientAddress;
    }

    public static class EtherMininumEventResponse extends BaseEventResponse {
        public BigInteger etherRemaining;
    }

    public static class EtherOutOfStockEventResponse extends BaseEventResponse {
        public BigInteger etherRemaining;
    }

    public static class EtherReceivedEventResponse extends BaseEventResponse {
        public BigInteger amount;
    }
}
