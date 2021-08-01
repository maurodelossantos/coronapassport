package es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts;

import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.MinerStartResponse;

import java.util.Arrays;

public class CustomMiner extends JsonRpc2_0Web3j{
    public CustomMiner(Web3jService web3jService) {
        super(web3jService);
    }

    public Request<?, MinerStartResponse> minerStart(int threadCount) {
        return new Request<>(
                "miner_start",
                Arrays.asList(threadCount),
                web3jService,
                MinerStartResponse.class);
    }

}
