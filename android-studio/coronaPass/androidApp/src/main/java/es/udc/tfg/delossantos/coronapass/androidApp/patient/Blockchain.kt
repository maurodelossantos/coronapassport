package es.udc.tfg.delossantos.coronapass.androidApp.patient

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import es.udc.tfg.delossantos.coronapass.android.R
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord3
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.*
import org.web3j.protocol.http.HttpService
import org.web3j.tuples.generated.Tuple12
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.utils.Convert
import java.io.File
import java.math.BigInteger
import java.security.Provider
import java.security.Security
import java.util.concurrent.Future


class Blockchain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blockchain)
        try{
            val tv_infoBlockchain: TextView = findViewById(R.id.tv_infoBlockchain)
            Log.d("cospeito", "Connecting to the Blockchain...")
            //CONNECT TO BLOCKCHAIN NODE
            var web3: Web3j =
                Web3j.build(HttpService("http://192.168.8.189:9545"))
            //TEST THE CONNECTION
            try {
                val clientVersion: Web3ClientVersion = web3.web3ClientVersion().sendAsync().get()
                if (!clientVersion.hasError()) {
                    //Connected
                    tv_infoBlockchain.setText("Connected to the Blockchain")
                    Log.d("cospeito", "Connected to the Blockchain!")
                } else {
                    //Show Error
                    Log.d("cospeito", "Error ocurred during the connection attempt.")
                }
            } catch (e: Exception) {
                //Show Error
                Log.d("cospeito", "Exception thrown in connection attempt: " + e.message)
            }

            Log.d("cospeito", "Setting up Bouncy Castle")
            setupBouncyCastle();

            //GLOBAL VARS - wip
            val password = "tfg"
            var fileName = ""
            val walletPath = filesDir.absolutePath
            var walletDir = File(walletPath)
            var createdWallet = ""

            //CREATE WALLET AND GET ETHER
            val bt_createWallet: Button = findViewById(R.id.bt_createWallet);
            bt_createWallet.setOnClickListener() {
                Log.d("cospeito", "Creating new wallet and retrieving Ether")
                try {
                    //wip: path where to create, where to store it, reuse without device dependency...
                    fileName =
                        WalletUtils.generateLightNewWalletFile(password, walletDir)
                    walletDir = File("$walletPath/$fileName")
                    var credentials: Credentials = WalletUtils.loadCredentials(password, walletDir)
                    tv_infoBlockchain.setText("Created wallet with address: " + credentials.address + " private key: "
                            + credentials.ecKeyPair.privateKey + " public key: " +
                            credentials.ecKeyPair.publicKey + " and wallet path: " + walletDir.absolutePath)
                    createdWallet = credentials.address
                    Log.d("cospeito", "Created new Wallet!")
                    Log.d("cospeito", "address: " + credentials.address)
                    Log.d("cospeito", "private key: " + credentials.ecKeyPair.privateKey)
                    Log.d("cospeito", "public key: " + credentials.ecKeyPair.publicKey)
                    Log.d("cospeito", "wallet absolute path: " + walletDir.absolutePath)

                    //GET ETHER
                    Log.d("cospeito", "Getting Ether for account: " + createdWallet)
                    val contractAddress = "0x95450372d28e8b79CE11c7422315567dDDc463B2"

                    //gasProvider with ZERO 0 gasPrice
                    val gasProvider = StaticGasProvider(Convert.toWei("0", Convert.Unit.WEI).toBigInteger(), BigInteger("3000000"))

                    //Check deprecacted
                    val medicalRecord = MedicalRecord3.load(
                        contractAddress,
                        web3,
                        credentials,
                        gasProvider
                    )

                    //Transfer 100 ETHER to the new created wallet from smart contract
                    val transactionReceipt0: TransactionReceipt? = medicalRecord.giveMeEther().sendAsync().get()
                    Log.d("cospeito", "Ether transferred to newly created account: " + credentials.address)
                    tv_infoBlockchain.setText("Ether transferred to newly created account: " + credentials.address)
                    val result = "Successful transaction. Hash:  ${transactionReceipt0?.transactionHash} . Block number: ${transactionReceipt0?.blockNumber}" +
                            "Gas used: ${transactionReceipt0?.gasUsed}"
                    Log.d("cospeito", result)

                } catch (e: java.lang.Exception) {
                    //Display an Error
                    Log.d("cospeito", "Error during wallet creation or either ether transaction: " + e.message)
                }
            }

            //GETTING BALANCES
            val bt_getBalance: Button = findViewById(R.id.bt_getBalance);
            bt_getBalance.setOnClickListener() {
                Log.d("cospeito", "Getting Balances of account 0x2514922833095a95818a8c9f208145c8023e423f")
                try {
                    //SAME AGAIN
                    walletDir = File("$walletPath/" + "UTC--2021-04-30T18-50-47.882000000Z--7ed24a763f33126f54538cbfe7f447284acdafd9.json")
                    //val credentials: Credentials = WalletUtils.loadCredentials(password, walletDir)
                    val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger("4291399530281885922742480569620411710199837584241190132277204729862147062324")))
                    Log.d("cospeito", "ACCOUNT: " + credentials.address)
                    Log.d("cospeito", "PUBK: " + credentials.ecKeyPair.publicKey)
                    Log.d("cospeito", "PRIVK: " + credentials.ecKeyPair.privateKey)
                    val contractAddress = "0x56c7507CE7B95a40A48D17cbdE776cD9d4DEc57d"
                    val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L)
                    val gasPrice: BigInteger = BigInteger.valueOf(4300000)
                    val medicalRecord = MedicalRecord.load(
                        contractAddress,
                        web3,
                        credentials,
                        gasLimit,
                        gasPrice
                    )

                    //ETHER BALANCE
                    val balance: EthGetBalance = web3.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).sendAsync().get()
                    Log.d("cospeito", "Balance of Ether: " + Convert.fromWei(balance.balance.toString(), Convert.Unit.ETHER))

                    //CORONACOIN BALANCE
                    val transactionReceipt2: BigInteger? = medicalRecord.balaceOfCoronacoins(credentials.address).sendAsync().get()
                    Log.d("cospeito", "Balance of Coronacoins: " + transactionReceipt2.toString())

                    tv_infoBlockchain.setText("Account: " + credentials.address + ". Balance of Ether: " + balance.balance.toString() + " and balance of Coronacoins: " + transactionReceipt2.toString())

                } catch (e: java.lang.Exception) {
                    //Display an Error
                    Log.d("cospeito", "Error during wallet balances response: " + e.message)
                }
            }

        //ADDING RECORD
        val bt_addRecord: Button = findViewById(R.id.bt_addRecord)
        bt_addRecord.setOnClickListener() {
            Log.d("cospeito", "Adding medical record of account: 0x2514922833095a95818a8c9f208145c8023e423f to the system. Sending transaction from master account: 0x2514922833095a95818a8c9f208145c8023e423f")
            try {
                //SAME
                val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger("4291399530281885922742480569620411710199837584241190132277204729862147062324")))
                Log.d("cospeito", "ACCOUNT: " + credentials.address.toString())
                val contractAddress = "0x56c7507CE7B95a40A48D17cbdE776cD9d4DEc57d"
                val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L)
                val gasPrice: BigInteger = BigInteger.valueOf(4300000)
                val medicalRecord = MedicalRecord.load(
                    contractAddress,
                    web3,
                    credentials,
                    gasLimit,
                    gasPrice
                )

                // id's casted to BigIntegers
                val idNac: BigInteger = BigInteger.valueOf(11111111)
                val idMed: BigInteger = BigInteger.valueOf(11111111)

                val transactionReceipt0: Future<TransactionReceipt>? = medicalRecord.addRecord(idNac, idMed, "0x2514922833095a95818a8c9f208145c8023e423f",
                    "Prueba0", "Suárez", "Zero", "15091998", "Masc", "FR", "611111111").sendAsync()
                val result = "Successful transaction. Hash:  ${transactionReceipt0?.get()?.transactionHash} . Block number: ${transactionReceipt0?.get()?.blockNumber}" +
                    "Gas used: ${transactionReceipt0?.get()?.gasUsed}"
                Log.d("cospeito", result)
                tv_infoBlockchain.setText("Added new record of " + "0x2514922833095a95818a8c9f208145c8023e423f" + "to the system. " + result)
            }catch (e: java.lang.Exception){
            Log.d("cospeito", "Failed adding record: " + e.message)
        }
        }

        //GETTING RECORD
        val bt_getRecord: Button = findViewById(R.id.bt_getRecord)
        bt_getRecord.setOnClickListener() {
            Log.d("cospeito", "Getting record information about account 91a4adc566dd4e170126cda1185a3f36680c06a2")
            try {
                walletDir = File("$walletPath/" + "UTC--2021-04-25T11-41-32.554000000Z--91a4adc566dd4e170126cda1185a3f36680c06a2.json")
                //val credentials: Credentials = WalletUtils.loadCredentials("tfg", walletDir)
                val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger("72106788576205927544203651753172087622193005660103383160580060886155842906708")))
                Log.d("cospeito", "ACCOUNT: " + credentials.address.toString())
                val contractAddress = "0x56c7507CE7B95a40A48D17cbdE776cD9d4DEc57d"
                val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L)
                val gasPrice: BigInteger = BigInteger.valueOf(4300000)

                val medicalRecord = MedicalRecord.load(
                    contractAddress,
                    web3,
                    credentials,
                    gasLimit,
                    gasPrice
                )

                // id's casted to BigIntegers
                val idNac: BigInteger = BigInteger.valueOf(11111111)
                val idMed: BigInteger = BigInteger.valueOf(11111111)

                val transactionReceipt0: Future<TransactionReceipt>? = medicalRecord.getRecord("91a4adc566dd4e170126cda1185a3f36680c06a2").sendAsync()
                val result = "Successful transaction. Hash:  ${transactionReceipt0?.get()?.transactionHash} . Block number: ${transactionReceipt0?.get()?.blockNumber}" +
                        "Gas used: ${transactionReceipt0?.get()?.gasUsed}"
                Log.d("cospeito", result)
                tv_infoBlockchain.setText("Passport of account: 91a4adc566dd4e170126cda1185a3f36680c06a2 gotten. " + result)

                Log.d("cospeito", "Getting free record info")
                val transactionReceipt: Tuple12<BigInteger, BigInteger, String, String, String, String, String, String, String, MutableList<MedicalRecord.Dosis>, MutableList<MedicalRecord.Prueba>, MutableList<String>>? = medicalRecord.getFreeRecord("91a4adc566dd4e170126cda1185a3f36680c06a2").sendAsync().get()
                if (transactionReceipt != null) {
                    Log.d("cospeito", transactionReceipt.component1().toString())
                    Log.d("cospeito", transactionReceipt.component2().toString())
                    Log.d("cospeito", transactionReceipt.component3())
                    Log.d("cospeito", transactionReceipt.component4())
                    Log.d("cospeito", transactionReceipt.component5())
                    Log.d("cospeito", transactionReceipt.component6())
                    Log.d("cospeito", transactionReceipt.component7())
                    Log.d("cospeito", transactionReceipt.component8())
                    Log.d("cospeito", transactionReceipt.component9())

                    var print10 = ""
                    if (transactionReceipt.component10().size > 0){
                    print10 = "Recibida dosis de " + transactionReceipt.component10()[0].proveedor + " el " + transactionReceipt.component10()[0].timestamp
                    }else{print10 ="No hay dosis todavía."}
                    Log.d("cospeito", print10)

                    var print11 = ""
                    if (transactionReceipt.component10().size > 0){
                        print11 = "Hecha prueba tipo " + transactionReceipt.component11()[0].tipo + " con resultado " + transactionReceipt.component11()[0].resultado + " el dia " + transactionReceipt.component11()[0].timestamp
                    }else{print11 ="No hay pruebas todavía."}
                    Log.d("cospeito", print11)

                    var print12 = ""
                    if (transactionReceipt.component10().size > 0){
                        print12 = "Reacciones adversas :" + transactionReceipt.component12()
                    }else{print12 ="No ha habido reacciones adversas."}
                    Log.d("cospeito", print12)

                    //CHECK 10,11,12 NOT NULL!!!
                    tv_infoBlockchain.setText(
                        transactionReceipt.component1().toString() + transactionReceipt.component2().toString() +
                        transactionReceipt.component3() + transactionReceipt.component4()
                        + transactionReceipt.component5() + transactionReceipt.component6() +
                        transactionReceipt.component7() + transactionReceipt.component8() + transactionReceipt.component9()
                        + print10 + print11 + print12)
                }
            }catch (e: java.lang.Exception){
                Log.d("cospeito", "Failed reading record: " + e.message)
            }
        }

        //ADD DOSIS
        val bt_addDosis: Button = findViewById(R.id.bt_addDosis)
        bt_addDosis.setOnClickListener() {
            Log.d("cospeito", "Adding new dose to account: 0xa6c11348c80e523a564e672c5a8870a0e3306f9d")
            try {
                walletDir = File("$walletPath/" + "UTC--2021-04-17T10-32-01.3Z--3de1f667c12aa14e3d74727630c48cb02e482a7f.json")
                val credentials: Credentials = WalletUtils.loadCredentials("tfg", walletDir)
                val contractAddress = "0x56c7507CE7B95a40A48D17cbdE776cD9d4DEc57d"
                val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L)
                val gasPrice: BigInteger = BigInteger.valueOf(4300000)

                val medicalRecord = MedicalRecord.load(
                    contractAddress,
                    web3,
                    credentials,
                    gasLimit,
                    gasPrice
                )

                // id's casted to BigIntegers
                val idNac: BigInteger = BigInteger.valueOf(11111111)
                val idMed: BigInteger = BigInteger.valueOf(11111111)

                val transactionReceipt0: Future<TransactionReceipt>? = medicalRecord.addDosis("0xa6c11348c80e523a564e672c5a8870a0e3306f9d", idMed,
                    "Pfizer", "Cospeito", "21042021").sendAsync()
                val result = "Successful transaction. Hash:  ${transactionReceipt0?.get()?.transactionHash} . Block number: ${transactionReceipt0?.get()?.blockNumber}" +
                        "Gas used: ${transactionReceipt0?.get()?.gasUsed}"
                Log.d("cospeito", result)
                Log.d("cospeito", "Added new dose to the system, in account: " + "0xa6c11348c80e523a564e672c5a8870a0e3306f9d" + ".")
                tv_infoBlockchain.setText("Added new dose to the system, in account: " + "0xa6c11348c80e523a564e672c5a8870a0e3306f9d" + ". " + result)
            }catch (e: java.lang.Exception){
                Log.d("cospeito", "Failed adding dose: " + e.message)
            }
        }

        //ADDING TEST
        val bt_addTest: Button = findViewById(R.id.bt_addTest)
        bt_addTest.setOnClickListener() {
            Log.d("cospeito", "Adding new test to account: 0xa6c11348c80e523a564e672c5a8870a0e3306f9d")
            try {
                walletDir = File("$walletPath/" + "UTC--2021-04-17T10-32-01.3Z--3de1f667c12aa14e3d74727630c48cb02e482a7f.json")
                val credentials: Credentials = WalletUtils.loadCredentials("tfg", walletDir)
                val contractAddress = "0x56c7507CE7B95a40A48D17cbdE776cD9d4DEc57d"
                val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L)
                val gasPrice: BigInteger = BigInteger.valueOf(4300000)

                val medicalRecord = MedicalRecord.load(
                    contractAddress,
                    web3,
                    credentials,
                    gasLimit,
                    gasPrice
                )

                // id's casted to BigIntegers
                val idNac: BigInteger = BigInteger.valueOf(11111111)
                val idMed: BigInteger = BigInteger.valueOf(11111111)

                //METHODS CALLS
                val transactionReceipt0: Future<TransactionReceipt>? = medicalRecord.addTest("0xa6c11348c80e523a564e672c5a8870a0e3306f9d",
                    idMed, "PCR", "A Coruña", "210421", "Negativo").sendAsync()
                val result = "Successful transaction. Hash:  ${transactionReceipt0?.get()?.transactionHash} . Block number: ${transactionReceipt0?.get()?.blockNumber}" +
                        "Gas used: ${transactionReceipt0?.get()?.gasUsed}"
                Log.d("cospeito", result)
                Log.d("cospeito", "Added new test to the system, in account: " + "0xa6c11348c80e523a564e672c5a8870a0e3306f9d" + ".")
                tv_infoBlockchain.setText("Added new test to the system, in account: " + "0xa6c11348c80e523a564e672c5a8870a0e3306f9d" + ". " + result)
            }catch (e: java.lang.Exception){
                Log.d("cospeito", "Failed adding test: " + e.message)
            }
        }

        //ADDING REACTION
        val bt_addReaction: Button = findViewById(R.id.bt_addReaction)
        bt_addReaction.setOnClickListener() {
            Log.d("cospeito", "Adding reaction to account: 0xa6c11348c80e523a564e672c5a8870a0e3306f9d")
            try {
                walletDir = File("$walletPath/" + "UTC--2021-04-17T10-32-01.3Z--3de1f667c12aa14e3d74727630c48cb02e482a7f.json")
                val credentials: Credentials = WalletUtils .loadCredentials("tfg", walletDir)
                val contractAddress = "0x56c7507CE7B95a40A48D17cbdE776cD9d4DEc57d"
                val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L)
                val gasPrice: BigInteger = BigInteger.valueOf(4300000)

                val medicalRecord = MedicalRecord.load(
                    contractAddress,
                    web3,
                    credentials,
                    gasLimit,
                    gasPrice
                )

                // id's casted to BigIntegers
                val idNac: BigInteger = BigInteger.valueOf(11111111)
                val idMed: BigInteger = BigInteger.valueOf(11111111)

                val transactionReceipt0: Future<TransactionReceipt>? = medicalRecord.addReaccion("0xa6c11348c80e523a564e672c5a8870a0e3306f9d",
                    "Algo de dolor de cabeza el día siguiente a la inoculación").sendAsync()
                val result = "Successful transaction. Hash:  ${transactionReceipt0?.get()?.transactionHash} . Block number: ${transactionReceipt0?.get()?.blockNumber}" +
                        "Gas used: ${transactionReceipt0?.get()?.gasUsed}"
                Log.d("cospeito", result)
                Log.d("cospeito", "Added new reaction to the system, in account: " + "0xa6c11348c80e523a564e672c5a8870a0e3306f9d" + ".")
                tv_infoBlockchain.setText("Added new reaction to the system, in account: " + "0xa6c11348c80e523a564e672c5a8870a0e3306f9d" + ". " + result)
            }catch (e: java.lang.Exception){
                Log.d("cospeito", "Failed adding reaction: " + e.message)
            }
        }

    /*
    //MINING FROM FIRST ACCOUNT - WIP mining from others (igual n hace falta)
    /*
    try {
        var web3jservice: Web3jService = HttpService("http://192.168.8.189:9545") //wip with exceptions: timeouts

        val miner: MyWeb3jImpl = MyWeb3jImpl(web3jservice)

        val mining: MinerStartResponse? = miner.minerStart(4).sendAsync().get()

        if (mining != null) {
            Log.d(
                "cospeito",
                "STARTED MINING BRO: " + mining.id.toString())
        }
    }catch (e: java.lang.Exception){
        Log.d("cospeito", "Failed mining: " + e.message)
    }*/

    //WORKING WITH EVENTS - WIP
    DONE: THIS WORKING
    var aux2 = medicalRecord.getRecord("0x91a4adc566dd4e170126cda1185a3f36680c06a2"
                    ).sendAsync().get()

                    for (event in medicalRecord.getPatientRecordAccesedEvents(aux2)){
                        Log.d("cospeito", "AAAAAAAAAA" + event.patientAddress)
                    }

}*/
        }catch(e: java.lang.Exception){
        //Show Error
        Log.d("cospeito", "Something failed during all the process. Debug for details.")
        }
        Log.d("cospeito", "Ending connection with the Blockchain.")
}

    //CRYPTOGRAPHIC REQUIRED FUNCTION
    private fun setupBouncyCastle() {
        val provider: Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
            ?: // Web3j will set up a provider  when it's used for the first time.
            return
        if (provider.javaClass.equals(BouncyCastleProvider::class.java)) {
            return
        }
        //There is a possibility  the bouncy castle registered by android may not have all ciphers
        //so we  substitute with the one bundled in the app.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }
}