pragma solidity >=0.5.0 <0.7.0;

import "./StandardToken.sol";
import "./Ownable.sol";

contract Coronacoin is StandardToken, Ownable {

    string public constant name = "Coronacoin"; // solium-disable-line uppercase
    string public constant symbol = "CVD"; // solium-disable-line uppercase
    uint8 public constant decimals = 32; // solium-disable-line uppercase

    //uint256 public constant INITIAL_SUPPLY = 10000 * (10 ** uint256(decimals));
    uint256 public constant INITIAL_SUPPLY = 16e10;

    constructor() public {
        totalSupply_ = INITIAL_SUPPLY;
        balances[msg.sender] = INITIAL_SUPPLY;
        emit Transfer(address(0x0), msg.sender, INITIAL_SUPPLY);
    }
}