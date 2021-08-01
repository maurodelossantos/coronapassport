package es.udc.tfg.delossantos.coronapass.androidApp.welcomesliders

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import es.udc.tfg.delossantos.coronapass.androidApp.Extensions.toast
import es.udc.tfg.delossantos.coronapass.androidApp.MainActivity
import es.udc.tfg.delossantos.coronapass.androidApp.R
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.CoronaFaucet
import es.udc.tfg.delossantos.coronapass.androidApp.usersmanagement.FirebaseUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger
import java.security.Provider
import java.security.Security


class IntroSliderActivity : AppCompatActivity() {

    private val fragmentList = ArrayList<Fragment>()
    lateinit var vpIntroSlider: ViewPager2
    lateinit var indicatorLayout: IndicatorLayout
    lateinit var tvPrivKey: TextView
    lateinit var tvAccount: TextView
    lateinit var getStarted: TextView

    lateinit var btEther: Button
    lateinit var tvSendEmail: TextView

    var privKey: String = "default"
    var account: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_slider)

        vpIntroSlider = findViewById(R.id.vpIntroSlider)
        indicatorLayout = findViewById(R.id.indicatorLayout)
        getStarted = findViewById(R.id.tvNext)

        val intent: Intent = getIntent()
        privKey = intent.getStringExtra("privateKey")
        account = intent.getStringExtra("account")

        // making the status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        val adapter = IntroSliderAdapter(this)
        vpIntroSlider.adapter = adapter

        fragmentList.addAll(
            listOf(
                Intro1Fragment(), Intro2Fragment(), Intro3Fragment()
            )
        )
        adapter.setFragmentList(fragmentList)

        indicatorLayout.setIndicatorCount(adapter.itemCount)
        indicatorLayout.selectCurrentPosition(0)

        registerListeners(privKey, account)
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

    private fun giveMeEther(privKey: String){
        Log.d("cospeito", "Connecting to the Blockchain...")
        //CONNECT TO BLOCKCHAIN NODE
        var web3: Web3j =
            Web3j.build(HttpService("http://192.168.0.14:9545"))
        //TEST THE CONNECTION
        try {
            val clientVersion: Web3ClientVersion = web3.web3ClientVersion().sendAsync().get()
            if (!clientVersion.hasError()) {
                //Connected
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
        var createdWallet = ""
        //GET ETHER
        Log.d("cospeito", "Retrieving Ether")
        try {
            //wip: path where to create, where to store it, reuse without device dependency...
            val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger(privKey)))
            createdWallet = credentials.address
            Log.d("cospeito", "Created new Wallet!")
            Log.d("cospeito", "address: " + credentials.address)
            Log.d("cospeito", "private key: " + credentials.ecKeyPair.privateKey)
            Log.d("cospeito", "public key: " + credentials.ecKeyPair.publicKey)

            val balance: EthGetBalance = web3.ethGetBalance(
                credentials.address,
                DefaultBlockParameterName.LATEST
            ).sendAsync().get()
            Log.d(
                "cospeito", "Balance of Ether: " + Convert.fromWei(
                    balance.balance.toString(),
                    Convert.Unit.ETHER
                )
            )
            if  (Convert.fromWei(balance.balance.toString(), Convert.Unit.ETHER).toInt() > 1){
                Log.d(
                    "cospeito", "You already have Ether! This incident will be reported.")
                toast("You already have Ether!!!! This incident will be reported.")
                return
            }

            //GET ETHER
            Log.d("cospeito", "Getting Ether for account: " + createdWallet)
            val contractAddress = "0x62227410914108DBD53E94A4D4Af6e759d1AE3DF"

            //gasProvider with ZERO 0 gasPrice
            val gasProvider = StaticGasProvider(
                Convert.toWei("0", Convert.Unit.WEI).toBigInteger(), BigInteger(
                    "3000000"
                )
            )

            //Check deprecacted
            val faucet = CoronaFaucet.load(
                contractAddress,
                web3,
                credentials,
                gasProvider
            )

            //Transfer 100 ETHER to the new created wallet from smart contract
            val transactionReceipt0: TransactionReceipt? = faucet.giveMeEther().sendAsync().get()
            Log.d("cospeito", "Ether transferred to newly created account: " + credentials.address)
            val result = "Successful transaction -> Hash: ${transactionReceipt0?.transactionHash}. Block number: ${transactionReceipt0?.blockNumber}." +
                    " Gas used: ${transactionReceipt0?.gasUsed}."
            Log.d("cospeito", result)
        } catch (e: java.lang.Exception) {
            //Display an Error
            Log.d(
                "cospeito",
                "Error during wallet creation or either ether transaction: " + e.message
            )
        }
    }

    private fun registerListeners(privKey: String, account: String) {
        vpIntroSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                indicatorLayout.selectCurrentPosition(position)

                if (position == 0) {
                    getStarted.visibility = View.INVISIBLE
                    tvPrivKey = findViewById(R.id.tv_fr1_privKey)
                    tvAccount = findViewById(R.id.tv_fr1_account)
                    tvPrivKey.setText(privKey)
                    tvAccount.setText(account)
                }

                if (position == 1) {
                    getStarted.visibility = View.INVISIBLE
                    btEther = findViewById(R.id.bt_giveMeEther)
                    btEther.setOnClickListener() {
                        FirebaseAuth.getInstance().currentUser.reload()
                        val user = FirebaseAuth.getInstance().currentUser
                        user.reload()
                            if (user.isEmailVerified == true) {
                                giveMeEther(privKey)
                                toast("verified!!!!!!")
                            } else {
                                toast("Verify your email account to get Ether!!!")
                            }
                    }
                    Log.d("cospeito", "pulso desde out")
                }

                if (position == 2) {
                    getStarted.visibility = View.VISIBLE
                    getStarted.setOnClickListener() {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }
                }
            }
        })

        //Improve the Next Button
        getStarted.setOnClickListener {
            val position = vpIntroSlider.currentItem
            if (position < fragmentList.lastIndex) {
                vpIntroSlider.currentItem = position + 1
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}