# Environment

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

###Create user and database
```
sudo -u postgres createuser -D -A -P commonsos
sudo -u postgres createdb -O commonsos commonsos
```

## Apache configuration

### install
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

## Ethereum network

- Install binary
```bash
sudo add-apt-repository ppa:ethereum/ethereum
sudo apt-get update
sudo apt install ethereum
```

### Setup boot node key

- Generate key for boot node 
```bash
bootnode --genkey=boot.key
```

#### or use ready one
```bash
echo 'd388c8346f156c9be0baee8a95abb3c3e755335d955d87b1f2164ff5a5b13767' > boot.key
```

### Start boot node
```bash
bootnode --nodekey=boot.key
```

### Generate initial state for member node
e.g. from blockchain/genesis.json
```bash
geth init --datadir=member genesis.json
```


### Create or import administrator/miner wallet

#### create new wallet 
```bash
geth --datadir=[node-folder] account new
```

#### import existing wallet
//e.g. from file UTC--2018-05-08T11-02-19.418361911Z--14063fb2a2e24cf80081a946953159d86e88c36c
```bash
cp [PATH_TO_YOUR_WALLET_FILE] member1/keystore/
```

### Start node with RPC to support REMIX and unlock main account
geth --datadir=member1 --port 30303 --rpc --rpcport 8545 --rpccorsdomain "*" --rpcapi "eth,net,web3,personal" --ipcdisable --bootnodes=enode://4ac77627e4236535c8778b66b0e1c440b190642eb7bd01a18c49f6a0893ebc8009f3d1712d7a5f61322c26be2f888e3baf5c713a394f7b6d7bfd67fa2f6e1dfd@[127.0.0.1]:30301 --verbosity 1 --unlock 0x14063fb2a2e24cf80081a946953159d86e88c36c console
### Start miner node
geth --datadir=miner1  --port 30399 --mine --minerthreads=1 --etherbase=0x14063fb2a2e24cf80081a946953159d86e88c36c --ipcdisable --bootnodes=enode://4ac77627e4236535c8778b66b0e1c440b190642eb7bd01a18c49f6a0893ebc8009f3d1712d7a5f61322c26be2f888e3baf5c713a394f7b6d7bfd67fa2f6e1dfd@[127.0.0.1]:30301

### geth cheat sheet

```javascript
var account1 = '0x3c4bdc4464a35e34a3f330aaa697cfba7b94f566'
var account2 = '0xed115362cd24de70293dc3650f211eae0f39d8ea'
var accounts = [user1, user2]

accounts.forEach(function(account){ 
  eth.sendTransaction({from:eth.coinbase, to:account, value: 48600000000000000})
})

var abi = [ { "constant": true, "inputs": [], "name": "name", "outputs": [ { "name": "", "type": "string" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "_spender", "type": "address" }, { "name": "_value", "type": "uint256" } ], "name": "approve", "outputs": [ { "name": "success", "type": "bool" } ], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [], "name": "totalSupply", "outputs": [ { "name": "", "type": "uint256" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "_from", "type": "address" }, { "name": "_to", "type": "address" }, { "name": "_value", "type": "uint256" } ], "name": "transferFrom", "outputs": [ { "name": "success", "type": "bool" } ], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [], "name": "decimals", "outputs": [ { "name": "", "type": "uint8" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "_value", "type": "uint256" } ], "name": "burn", "outputs": [ { "name": "success", "type": "bool" } ], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "", "type": "address" } ], "name": "balanceOf", "outputs": [ { "name": "", "type": "uint256" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "_from", "type": "address" }, { "name": "_value", "type": "uint256" } ], "name": "burnFrom", "outputs": [ { "name": "success", "type": "bool" } ], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [], "name": "symbol", "outputs": [ { "name": "", "type": "string" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "constant": false, "inputs": [ { "name": "_to", "type": "address" }, { "name": "_value", "type": "uint256" } ], "name": "transfer", "outputs": [], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": false, "inputs": [ { "name": "_spender", "type": "address" }, { "name": "_value", "type": "uint256" }, { "name": "_extraData", "type": "bytes" } ], "name": "approveAndCall", "outputs": [ { "name": "success", "type": "bool" } ], "payable": false, "stateMutability": "nonpayable", "type": "function" }, { "constant": true, "inputs": [ { "name": "", "type": "address" }, { "name": "", "type": "address" } ], "name": "allowance", "outputs": [ { "name": "", "type": "uint256" } ], "payable": false, "stateMutability": "view", "type": "function" }, { "inputs": [ { "name": "initialSupply", "type": "uint256" }, { "name": "tokenName", "type": "string" }, { "name": "tokenSymbol", "type": "string" } ], "payable": false, "stateMutability": "nonpayable", "type": "constructor" }, { "anonymous": false, "inputs": [ { "indexed": true, "name": "from", "type": "address" }, { "indexed": true, "name": "to", "type": "address" }, { "indexed": false, "name": "value", "type": "uint256" } ], "name": "Transfer", "type": "event" }, { "anonymous": false, "inputs": [ { "indexed": true, "name": "from", "type": "address" }, { "indexed": false, "name": "value", "type": "uint256" } ], "name": "Burn", "type": "event" } ]
var contract = eth.contract(abi).at('0xcb89e20da9edc83bc56b5f1b540c8b3b08a38a59')

contract.balanceOf('account1')
``` 

# Building

```
./build.sh
```

# Running

```
./start.sh
./stop.sh

```

# Development

## Token Contract

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