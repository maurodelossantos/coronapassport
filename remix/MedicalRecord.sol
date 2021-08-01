// SPDX-License-Identifier: MIT

pragma solidity >=0.5.0 <0.7.0;
pragma experimental ABIEncoderV2;

import "./Coronacoin.sol";
//import "./SafeMath.sol";

contract MedicalRecord is Ownable{
    
    //EVENTS
    event PatientRecordAccesed(uint32 _idNacional,
            uint32 _idMedico,
            string _nombre,
            string _apellido1,
            string _apellido2,
            string _fechaNacimiento,
            string _genero,
            string _pais,
            string _contacto,
            Dosis[] _dosisRecibidas,
            Prueba[] _pruebasRealizadas,
            string[] _reaccionesAdversas);
    event PatientRecordAdded(uint256 recordID, 
            address patientAddress);
    event CdcAdded(address cdcAddress);
    event DoseAdded(uint256 recordID, 
            address patientAddress,
            Dosis newDose);
    event TestAdded(uint256 recordID, 
            address patientAddress,
            Prueba newTest);
    event ReactionAdded(uint256 recordID, 
            address patientAddress, 
            string reaccion);
    event RewardPaid(address patientAddress);
    event TokenRewardSet(uint256 _tokenRewardSmall, 
            uint256 _tokenRewardMedium, 
            uint256 _tokenRewardBig);
    
    //STORAGE
    struct Dosis {
        uint256 nLote;
        string proveedor;
        string lugar;
        string timestamp;
        address cdc;
    }
    
    struct Prueba {
        uint256 idPrueba;
        string tipo;
        string resultado;
        string timestamp;
        string lugar;
        address cdc;
    }
    
    struct Record {
        uint32 idNacional;
        uint32 idMedico;
        address direccionPaciente;
        string nombre;
        string apellido1;
        string apellido2;
        string fechaNacimiento;
        string genero;
        string pais;
        string contacto;
        Dosis[] dosisRecibidas;
        Prueba[] pruebasRealizadas;
        string[] reaccionesAdversas; 
    }
    
    struct Cdc{
        string nombre;
        string direccion;
        string contacto;
    }

    Coronacoin public coronacoin;
    address public tokenAddress;
    uint256 public tokenRewardBigAmount;
    uint256 public tokenRewardMediumAmount;
    uint256 public tokenRewardSmallAmount;
    
    uint256 public recordCount = 0;
    uint256 public patientCount = 0;
    uint256 public cdcCount = 0;
    
    //para acceder ao recordCount dun paciente
    mapping (address => uint256) dirToRecord;
    //mapping (address => Record) records;
    mapping (uint256 => mapping (address => Record)) records;
    mapping (address => Cdc) cdcs;
    mapping (address => bool) public isPatient; 
    mapping (address => bool) public isCdc; 
    
    //ETHER FUNCS
    fallback() external payable {
        //when no other function matches
        //if (msg.value > 0) {
            //emit Deposit(msg.sender, msg.value);
        //}
    }
    
    receive() external payable {
        // react to receiving ether
    }
    
    modifier patientExist(address patient) {
        require(isPatient[patient]);
        _;
    }
    
    modifier cdcExist(address cdc) {
        require(isCdc[cdc]);
        _;
    }
    
    modifier patientNonExist(address patient) {
        require(!isPatient[patient]);
        _;
    }
    
    modifier cdcNonExist(address cdc) {
        require(!isCdc[cdc]);
        _;
    }
    
    modifier notNull(address _address) {
        require(_address != address(0x0));
        _;
    }
    
    modifier higherThanZero(uint256 _uint) {
        require(_uint > 0);
        _;
    }
    
    modifier dosisRecibida(address _direccionPaciente) {
        uint256 recordID = dirToRecord[_direccionPaciente];
        require(records[recordID][_direccionPaciente].dosisRecibidas.length > 0);
        _;
    }
    
    //CONSTRUTOR
    constructor()
        public
    {
        setCoronacoin(address(new Coronacoin()));
        uint256 initialSmallReward = 10;
        uint256 initialMediumReward = 100;
        uint256 initialBigReward = 1000;
        setCoronacoinRewards(initialSmallReward, initialMediumReward, initialBigReward);
    }
    
    //CUSTOM FUNCTIONS
    
    //PARTE REXISTRO MÉDICO
    function addCdc (
        address _direccionCdc,
        string memory _nombre,
        string memory _direccionPostal,
        string memory _contacto)
        public
        cdcNonExist(_direccionCdc)
        //modifier extra¿?
    {
        cdcs[_direccionCdc].nombre = _nombre;
        cdcs[_direccionCdc].direccion = _direccionPostal;
        cdcs[_direccionCdc].contacto = _contacto;

        isCdc[_direccionCdc] = true;
        
        emit CdcAdded(_direccionCdc);

        cdcCount += 1;
    }
    
    //Get free CDC info
    function getCdcInfo(address _direccionCdc)
        public
        view
        cdcExist(_direccionCdc)
        returns (
            string memory _nombre,
            string memory _direccionPostal,
            string memory _contacto) //wip
    {
        _nombre = cdcs[_direccionCdc].nombre;
        _direccionPostal = cdcs[_direccionCdc].direccion;
        _contacto = cdcs[_direccionCdc].contacto;
    }
    
    //PARTE REGISTRO MÉDICO
    function addRecord (
        uint32 _idNacional,
        uint32 _idMedico,
        address _direccionPaciente,
        string memory _nombre,
        string memory _apellido1,
        string memory _apellido2,
        string memory _fechaNacimiento,
        string memory _genero,
        string memory _pais,
        string memory _contacto)
        public
        patientNonExist(_direccionPaciente)
        cdcExist(msg.sender)
    {
        records[recordCount][_direccionPaciente].idNacional = _idNacional;
        records[recordCount][_direccionPaciente].idMedico = _idMedico;
        records[recordCount][_direccionPaciente].direccionPaciente = _direccionPaciente;
        records[recordCount][_direccionPaciente].nombre = _nombre;
        records[recordCount][_direccionPaciente].apellido1 = _apellido1;
        records[recordCount][_direccionPaciente].apellido2 = _apellido2;
        records[recordCount][_direccionPaciente].fechaNacimiento = _fechaNacimiento;
        records[recordCount][_direccionPaciente].genero = _genero;
        records[recordCount][_direccionPaciente].pais = _pais;
        records[recordCount][_direccionPaciente].contacto = _contacto;
        
        
        isPatient[_direccionPaciente] = true;
        dirToRecord[_direccionPaciente] = recordCount;
        
        emit PatientRecordAdded(recordCount, _direccionPaciente);

        patientCount += 1;
        recordCount += 1;
        
        payReward(_direccionPaciente, 3); 
    }
    
    function knownPatient(address _direccionPaciente) 
        public
        view
        returns (bool patient){
            return isPatient[_direccionPaciente];
        }
    
    function getRecord(address _direccionPaciente)
        public
        returns (
            uint32 _idNacional,
            uint32 _idMedico,
            string memory _nombre,
            string memory _apellido1,
            string memory _apellido2,
            string memory _fechaNacimiento,
            string memory _genero,
            string memory _pais,
            string memory _contacto,
            Dosis[] memory _dosisRecibidas,
            Prueba[] memory _pruebasRealizadas,
            string[] memory _reaccionesAdversas) 
    {
        uint256 recordID = dirToRecord[_direccionPaciente];
        
        _idNacional = records[recordID][_direccionPaciente].idNacional;
        _idMedico = records[recordID][_direccionPaciente].idMedico;
        _nombre = records[recordID][_direccionPaciente].nombre;
        _apellido1 = records[recordID][_direccionPaciente].apellido1;
        _apellido2 = records[recordID][_direccionPaciente].apellido2;
        _fechaNacimiento = records[recordID][_direccionPaciente].fechaNacimiento;
        _genero = records[recordID][_direccionPaciente].genero;
        _pais = records[recordID][_direccionPaciente].pais;
        _contacto = records[recordID][_direccionPaciente].contacto;
        _dosisRecibidas = records[recordID][_direccionPaciente].dosisRecibidas;
        _pruebasRealizadas = records[recordID][_direccionPaciente].pruebasRealizadas;
        _reaccionesAdversas = records[recordID][_direccionPaciente].reaccionesAdversas;
        
        emit PatientRecordAccesed(_idNacional, _idMedico, _nombre, _apellido1, _apellido2, _fechaNacimiento, _genero, _pais, _contacto, _dosisRecibidas , _pruebasRealizadas , _reaccionesAdversas);
        payReward(_direccionPaciente, 1); //pagamos reward máis baixo pola lectura do pasaporte
    }
    
    function getFreeRecord(address _direccionPaciente)
        public
        view
        returns (
            uint32 _idNacional,
            uint32 _idMedico,
            string memory _nombre,
            string memory _apellido1,
            string memory _apellido2,
            string memory _fechaNacimiento,
            string memory _genero,
            string memory _pais,
            string memory _contacto,
            Dosis[] memory _dosisRecibidas,
            Prueba[] memory _pruebasRealizadas,
            string[] memory _reaccionesAdversas) //wip
    {
        uint256 recordID = dirToRecord[_direccionPaciente];
        _idNacional = records[recordID][_direccionPaciente].idNacional;
        _idMedico = records[recordID][_direccionPaciente].idMedico;
        _nombre = records[recordID][_direccionPaciente].nombre;
        _apellido1 = records[recordID][_direccionPaciente].apellido1;
        _apellido2 = records[recordID][_direccionPaciente].apellido2;
        _fechaNacimiento = records[recordID][_direccionPaciente].fechaNacimiento;
        _genero = records[recordID][_direccionPaciente].genero;
        _pais = records[recordID][_direccionPaciente].pais;
        _contacto = records[recordID][_direccionPaciente].contacto;
        _dosisRecibidas = records[recordID][_direccionPaciente].dosisRecibidas;
        _pruebasRealizadas = records[recordID][_direccionPaciente].pruebasRealizadas;
        _reaccionesAdversas = records[recordID][_direccionPaciente].reaccionesAdversas;
    }
    
    function addDosis(address _direccionPaciente, uint256 _nLote, string memory _proveedor, string memory _lugar, string memory _timestamp)
        public
        patientExist(_direccionPaciente)
        //+ modifiers ¿?
    {
        uint256 recordID = dirToRecord[_direccionPaciente];
        uint lengthDoses = records[recordID][_direccionPaciente].dosisRecibidas.length;
        
        Dosis memory tmp;
        tmp.nLote = _nLote;
        tmp.proveedor = _proveedor;
        tmp.lugar = _lugar;
        tmp.timestamp = _timestamp;
        tmp.cdc = msg.sender;
        
        records[recordID][_direccionPaciente].dosisRecibidas.push(tmp);

        payReward(_direccionPaciente, 2); //pago reward medio por añadir dosis
        emit DoseAdded(recordID, _direccionPaciente, records[recordID][_direccionPaciente].dosisRecibidas[lengthDoses]);
        
    }
    
        function addTest(address _direccionPaciente, uint256 _idPrueba, string memory _tipo, string memory _lugar, string memory _timestamp, string memory _resultado)
        public
        patientExist(_direccionPaciente)
        //+ modifiers ¿?
    {
        uint256 recordID = dirToRecord[_direccionPaciente];
        uint lengthPruebas = records[recordID][_direccionPaciente].pruebasRealizadas.length;
        
        Prueba memory tmp;
        tmp.idPrueba = _idPrueba;
        tmp.tipo = _tipo;
        tmp.lugar = _lugar;
        tmp.timestamp = _timestamp;
        tmp.resultado = _resultado;
        tmp.cdc = msg.sender;
        
        records[recordID][_direccionPaciente].pruebasRealizadas.push(tmp);
        
        payReward(_direccionPaciente, 2); //pago reward medio por añadir test
        emit TestAdded(recordID, _direccionPaciente, records[recordID][_direccionPaciente].pruebasRealizadas[lengthPruebas]);
    }
    
        function addReaccion(address _direccionPaciente, string memory _reaccion)
        public
        patientExist(_direccionPaciente)
        //+ modifiers ¿?
    {
        uint256 recordID = dirToRecord[_direccionPaciente];
        uint lengthReacciones = records[recordID][_direccionPaciente].reaccionesAdversas.length;
        records[recordID][_direccionPaciente].reaccionesAdversas.push(_reaccion);

        payReward(_direccionPaciente, 2);
        emit ReactionAdded(recordID, _direccionPaciente, records[recordID][_direccionPaciente].reaccionesAdversas[lengthReacciones]);
    }
    
    //PARTE CRIPTOMOEDA PERSONALIZADA
    function setCoronacoin(address _newCoronacoin)
        internal
        onlyOwner
        notNull(_newCoronacoin)
    {
        coronacoin = Coronacoin(_newCoronacoin);
        tokenAddress = address(coronacoin);
    }
    
    function setCoronacoinRewards(uint256 _tokenRewardSmall, uint256 _tokenRewardMedium, uint256 _tokenRewardBig)
        public
        onlyOwner
        higherThanZero(_tokenRewardSmall)
        higherThanZero(_tokenRewardMedium)
        higherThanZero(_tokenRewardBig)
    {
        tokenRewardSmallAmount = _tokenRewardSmall;
        tokenRewardMediumAmount = _tokenRewardMedium;
        tokenRewardBigAmount = _tokenRewardBig;
        emit TokenRewardSet(_tokenRewardSmall, _tokenRewardMedium, _tokenRewardBig);
    }
    
    function payReward(address _patientAddress, int number)
        private
        notNull(_patientAddress)
    {
        if(number == 3){
            coronacoin.transfer(_patientAddress, tokenRewardBigAmount);
        }else if(number == 2){
            coronacoin.transfer(_patientAddress, tokenRewardMediumAmount);
        }else{
            coronacoin.transfer(_patientAddress, tokenRewardSmallAmount);
        }
        emit RewardPaid(_patientAddress);
    }
    
    function balaceOfCoronacoins(address _patientAddress)
        public
        view
        notNull(_patientAddress)
        returns (uint256)
    {
        return coronacoin.balanceOf(_patientAddress);
    }
}