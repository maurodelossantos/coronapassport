pragma solidity >=0.5.0 <0.7.0;

import "./MedicalRecord.sol";

contract CoronaFaucet{
    
    //EVENTS
    event etherMininum(uint etherRemaining);
    event etherGiven(address patientAddress);
    event etherReceived(uint amount);
    event etherOutOfStock(uint etherRemaining);
    
    //ETHER FUNCS
    fallback() external payable {
        //when no other function matches
        //if (msg.value > 0) {
            //emit Deposit(msg.sender, msg.value);
        //}
    }
    receive() external payable {
        // react to receiving ether
        emit etherReceived(msg.value);
    }
    
    //FUNCTIONS
    //PRIVATE FAUCET
    function giveMeEther() public
    //modifiers ¿?
    {
        uint balance = address(this).balance;
        if (balance < 1 ether){ //if there is no Ether
            emit etherOutOfStock(balance);
        }else if (balance < 100000 ether){ //if SC balance lower than 100k
            uint percentage = balance * 0.001 ether; //send the 0.1% of Ether
            msg.sender.transfer(percentage);
            emit etherGiven(msg.sender);
            emit etherMininum(balance);
        }else{
            msg.sender.transfer(100 ether); //else send 100 Ether
            emit etherGiven(msg.sender);
        }
    }
    
    //SIMPLE VERSION
    function giveMeEtherSimple() public
    //modifiers ¿?
    {
            msg.sender.transfer(100 ether); //send 100 Ether
    }
}