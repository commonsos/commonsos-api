# Environment

## Install java and gradle
```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer 

sudo apt-get install gradle 
```

## Database installation

```
sudo apt-get install postgresql
``` 

### Create user and database
```
sudo -u postgres createuser -D -A -P commonsos
sudo -u postgres createdb -O commonsos commonsos
```

## Apache configuration

### Install
```bash
sudo apt-get install apache2

sudo a2enmod rewrite
sudo a2enmod ssl
sudo a2enmod unique_id
sudo a2enmod proxy
sudo a2enmod proxy_http
sudo a2enmod headers
```

### Site configuration
```
<VirtualHost _default_:80>
	Redirect permanent / https://[COMMONSOS_APP_URL]/
</VirtualHost>

<VirtualHost *:443>
    DocumentRoot [PATH_TO_FOLDER_CONTAINING_INDEX_HTML]

    <Directory [PATH_TO_FOLDER_CONTAINING_INDEX_HTML]>
      Require all granted
      Allow from all
    </Directory>

    ProxyPreserveHost On

    <Location /api>
      ProxyPass http://localhost:4567 retry=0
      ProxyPassReverse http://localhost:4567
    </Location>

    SSLEngine on
    SSLCertificateKeyFile [PATH_TO_CERTIFICATE_KEY_FILE]

    Header edit Set-Cookie ^(.*)$ $1;Secure
    RequestHeader set X-Request-Id %{UNIQUE_ID}e

    ErrorLog ${APACHE_LOG_DIR}/error.log
    CustomLog ${APACHE_LOG_DIR}/access.log combined
</VirtualHost>
```

## Setup Ethereum network

### Install
```
sudo add-apt-repository ppa:ethereum/ethereum
sudo apt-get update
sudo apt install ethereum
```

### Setup boot node key
It's easier to use ready one
- blockchain/scripts/boot.key

or generate new key for boot node and update scripts to use correct bootnode uri enode://... 
```
bootnode --genkey=boot.key
```

### Generate initial state for member and miner nodes
e.g. from blockchain/scripts/genesis.json
```
geth init --datadir=member genesis.json
geth init --datadir=miner genesis.json
```
### Create or import administrator/miner wallet

```
geth --datadir=miner account new
```
NB! Change --etherbase param in blockchain/scripts/miner.sh to use miner wallet address 

### Start network
Use `blockchain/scripts/*`

- `blockchain/scripts/startall.sh` to start boot, miner and member nodes
- `blockchain/scripts/stop.sh` to stop everything

### geth console usage examples

```javascript
var account1 = '0x3c4bdc4464a35e34a3f330aaa697cfba7b94f566'
var account2 = '0xed115362cd24de70293dc3650f211eae0f39d8ea'
var accounts = [user1, user2]

accounts.forEach(function(account){ 
  eth.sendTransaction({from:eth.coinbase, to:account, value: 48600000000000000})
})

````
#### Access contract

```
var abi = [ { "constant": true, "inputs": [], "name": "name", "outputs": [ { "name": "", "type": "string" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "_spender", "type": "address" }, { "name": "_value", "type": "uint256" } ], "name": "approve", "outputs": [ { "name": "success", "type": "bool" } ], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [], "name": "totalSupply", "outputs": [ { "name": "", "type": "uint256" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "_from", "type": "address" }, { "name": "_to", "type": "address" }, { "name": "_value", "type": "uint256" } ], "name": "transferFrom", "outputs": [ { "name": "success", "type": "bool" } ], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [], "name": "decimals", "outputs": [ { "name": "", "type": "uint8" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "_value", "type": "uint256" } ], "name": "burn", "outputs": [ { "name": "success", "type": "bool" } ], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "", "type": "address" } ], "name": "balanceOf", "outputs": [ { "name": "", "type": "uint256" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "_from", "type": "address" }, { "name": "_value", "type": "uint256" } ], "name": "burnFrom", "outputs": [ { "name": "success", "type": "bool" } ], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [], "name": "symbol", "outputs": [ { "name": "", "type": "string" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "_to", "type": "address" }, { "name": "_value", "type": "uint256" } ], "name": "transfer", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": false, "inputs": [ { "name": "_spender", "type": "address" }, { "name": "_value", "type": "uint256" }, { "name": "_extraData", "type": "bytes" } ], "name": "approveAndCall", "outputs": [ { "name": "success", "type": "bool" } ], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "", "type": "address" }, { "name": "", "type": "address" } ], "name": "allowance", "outputs": [ { "name": "", "type": "uint256" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "inputs": [ { "name": "initialSupply", "type": "uint256" }, { "name": "tokenName", "type": "string" }, { "name": "tokenSymbol", "type": "string" } ], "payable": false, "stateMutability": "nonpayable", "type": "constructor" }, { "anonymous": false, "inputs": [ { "indexed": true, "name": "from", "type": "address" }, { "indexed": true, "name": "to", "type": "address" }, { "indexed": false, "name": "value", "type": "uint256" } ], "name": "Transfer", "type": "event" }, { "anonymous": false, "inputs": [ { "indexed": true, "name": "from", "type": "address" }, { "indexed": false, "name": "value", "type": "uint256" } ], "name": "Burn", "type": "event" } ]
var contract = eth.contract(abi).at('0xcb89e20da9edc83bc56b5f1b540c8b3b08a38a59')

contract.balanceOf('account1')
``` 

# Building, deployment and running

- Build commons-api.zip
```
./build.sh
```

- Copy commons-api.zip to target server
- Copy deploy.sh to target server and execute it with version and git revision parameters

This will redeploy app into versioned folder and restart it.
 
# Development

## Ethereum Token Contract

### Compile Solidity contract code 

- Install solidity compiler
```
sudo add-apt-repository ppa:ethereum/ethereum
sudo apt-get update
sudo apt-get install solc 
```

- Compile Token smart contract
```
cd [project root]
mkdir build/token
solc blockchain/TokenERC20.sol --abi --bin --overwrite --optimize -o build/token/
```

### Generate Java wrapper class

- Install web3j command line tools
Download latest release as ```.tar``` file from https://github.com/web3j/web3j/releases/

- Generate Java wrapper for Token smart contract
```
web3j solidity generate --javaTypes build/token/TokenERC20.bin build/token/TokenERC20.abi -o src/ -p commonsos.domain.blockchain
```