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
    LogFormat "%t[%{UNIQUE_ID}e][%{X-AUTHENTICATED-USER}o] %h - %>s \"%r\" \"%{User-agent}i\" %b %{ms}T" request
    CustomLog /[YOUR_HOME_FOLDER]/logs/request.log request
</VirtualHost>
```

## Monitoring and Mail sending configuration
```
sudo apt install monit

```

Example monit config to monitor/alert/restart app:
```
SET mail-format {
        from: monit@app.test.commons.love
        subject: monit alert: $SERVICE $EVENT $DATE
        message: $DESCRIPTION
}

SET ALERT [email1@example.com]
SET ALERT [email2@example.com]

CHECK PROCESS geth_miner WITH PIDFILE /home/ubuntu/eth/miner/pid
        START PROGRAM = "/bin/bash -c 'cd /home/ubuntu/eth && ./miner.sh'" AS UID ubuntu AND GID ubuntu
        if failed
                unixsocket /home/ubuntu/eth/miner/geth.ipc
                WITH TIMEOUT 10 SECONDS
                FOR 2 CYCLES
                then RESTART


```

Install mailutils that will install postfix as well 
```
sudo apt-get update
sudo apt-get install mailutils
```

Modify /etc/postfix/main.cf line `inet_interfaces = all` to be `inet_interfaces = loopback-only` 

Restart postfix 
```
sudo systemctl restart postfix
```

Test email sending
```
echo "test mail body" | mail -s "test mail subject" your@email.address
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

- Install gradle
```
sudo apt-get install gradle
``` 

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

## Mobile app development

***Cordova*** (https://cordova.apache.org/) is used to build multi-platform mobile app. Minimum version for platforms:
- android 7.1.0
- ios 4.5.4


### Android specific setup

- Install Android Studio containing Android SDK

https://developer.android.com/studio/

```
cordova build android

```

### iOS specific setup

https://cordova.apache.org/docs/en/latest/guide/platforms/ios/
- Install Xcode
- Install CocoaPods


## Install Cordova 

```
sudo npm install -g cordova
```

Determine missing dependencies / requirements. In Cordova project root folder execute 
```
cordova requirements 
```

### Push notifications

On mobile app Cordova plugin ***phonegap-plugin-push*** (https://github.com/phonegap/phonegap-plugin-push) is used to support 
push notification functionality.

See `https://github.com/phonegap/phonegap-plugin-push/blob/master/docs/INSTALLATION.md` for more details

To set up mobile app project locally it's required to add Firebase configuration files to Cordova project root folder.

Firebase configuration is platform dependent. 
- google-services.json for Android
- GoogleService-Info.plist for iOS

Download configuration files from Firebase console https://console.firebase.google.com. 
- Firebase project must have separate apps defined for Android and iOS. 
- Select desired app (platform), find download link having required file name as title. 


On server side Firebase Cloud Messaging (https://console.firebase.google.com) is used to communicate with Apple APN and Google Push Notifications.
To setup server locally you need to download and configure Firebase service account key. Open your project on Firebase, go to Settings, select Service Accounts tab.
On Firebase Admin SDK selection click on *Generate new private key* and download it. Define environment variable FIREBASE_CREDENTIALS with value pointing to absolute path of credentials file.

It's possible to test push notification directly on Firebase console. Goto Firebase, open your project, 
select Grow -> Cloud Messaging on the left menu. To send message to concrete device you need to know it's push notification token. You can grab it from server logs.

### Building application

Check out commonsos-web project to the folder containing commonsos-mobile. Make sure it's build script executes well.
```
npm run build-mobile
```

- On Cordova project root folder execute ***build.sh***. It copies latest web resources into Cordova project and builds mobile platforms.
If you like to build specific platform, execute ***build.sh ios*** or ***build.sh android***

- Run built target in emulator or physical device

List available targets
```
cordova run --list
adb devices   # android only 
```

```
cordova run ios                  # concrete platform on any available target
cordova run android --device     # concrete platform on physical device
cordova run --device             # current platform on physical device
cordova run --emulator           # current platform on available emulator
cordova run --target target_id   # current platform on specific device or emulator
```
  
#### iOS specific 
XCode -> open Commons OS.xcworkspace (NOT .Commons OS.xcodeproj)

Configure code signing cert -> run app

Also, configure Firebase to proxy push messages to Apple APN
- Create APN authenticationkey https://developer.clevertap.com/docs/how-to-create-an-ios-apns-auth-key
- Upload it to Firebase project iOS app section https://firebase.google.com/docs/cloud-messaging/ios/certs

 
# SSL with LetsEncrypt

- install
```
yum install python27-devel git
git clone https://github.com/letsencrypt/letsencrypt /opt/letsencrypt
/etc/letsencrypt$ /opt/letsencrypt/letsencrypt-auto --debug
```

- automatic certificate renewal
include into /etc/crontab
```
27  19  *  *  *  root     /opt/letsencrypt/letsencrypt-auto --no-bootstrap renew
57  19  *  *  *  root     apachectl -k restart > /dev/null 2>&1
```