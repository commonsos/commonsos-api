# Installation to AWS

NB! Select region close to customer

- Create VPC with single public subnet CIDR=10.0.0.0/16

## Create EC2 instance for API and WEB
- Choose Ubuntu Server 16.04 LTS (HVM)
- Enable 'Protect against accidental termination'
- Disable 'Auto-assign Public IP'
- Create new Security Group as a part of ec2 creation wizard
- Add HTTP and HTTPS access rules (SSH is enabled by default)
- Select to create new key/pair for remote access
- After download, extract public key from downloaded .pem
```
chmod 600 [KEY_NAME.PEM]
ssh-keygen -y -f [KEY_NAME.PEM] > [KEY_NAME].pub
``` 

- Add ssh config for management
```
Host [SSH_CONFIG_NAME_FOR_MANAGEMENT]
  Hostname [CREATED_ELASTIC_IP]
  User ubuntu
  IdentityFile /home/xp/.ssh/[KEY_NAME]
  IdentitiesOnly yes
```

- Allocate Elastic IP and associate it with created instance

- Now you can log in to server as root user:
```
 ssh [SSH_CONFIG_NAME_FOR_MANAGEMENT]
```

- Create user for running application
```
sudo adduser commonsos --disabled-password
```

- Add ssh config for app user
```
Host [SSH_CONFIG_NAME_FOR_APP]
  Hostname [CREATED_ELASTIC_IP]
  User commonsos
  IdentityFile /home/xp/.ssh/[KEY_NAME]
  IdentitiesOnly yes
```


## Install WEB and API to server
- Update package info
```
sudo apt-get update
```

- Install utilities
```
sudo apt install unzip
```

- Install java and gradle
```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer 

sudo apt-get install gradle 
```

- Install apache
```
sudo apt-get install apache2

sudo a2enmod rewrite
sudo a2enmod ssl
sudo a2enmod unique_id
sudo a2enmod proxy
sudo a2enmod proxy_http
sudo a2enmod headers
```

- Add Apache config
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


- Copy api deploy script 
```
scp deploy.sh [SSH_CONFIG_NAME_FOR_APP]:.
```

- Add application configuration to file /home/commonsos/.local_environment
```
export DATABASE_URL=jdbc:postgresql://commonsos-test...rds.amazonaws.com:5432/commonsos
export DATABASE_USERNAME=commonsos
export DATABASE_PASSWORD=[password]

export COMMONSOS_WALLET_FILE=[miner-wallet-file-path]
export COMMONSOS_WALLET_PASSWORD=[password]

export AWS_S3_BUCKET_NAME=commonsos-app-[environment]
export AWS_ACCESS_KEY=[access_key]
export AWS_SECRET_KEY=[secret_key]

export ETHEREUM_URL=[url to ethereum network RPC port]
```

## Create database (TEST environment)

### Create additional subnet in some other availability zone
name tag - commonsos_xxx2
Availability Zone - no preference
IPv4 CIDR - 10.0.1.0/24

### Add Postgres RDS
- AWS -> RDS -> Dashboard (Getting Started Now for first-timer)
- Select PostgreSQL
- profile Dev/Test
- DB Instance Identifier=commonsos-test
- Enable only RDS Free Tier options
- Allocated Storage = 20GB
- Master Username=commonsos
- Password=[GENERATE]
- Database name=commonsos

NB! Update local_environment with db url

### Install PostgreSQL client on APP node
```
sudo apt install postgresql-client-common
sudo apt install postgresql-client
```

## Create Ethereum network (TEST environment)
- Create EC2 node, use same key pair as for APP and WEB node

NB! Update local_environment with eth node url

- Update [SSH_CONFIG_NAME_FOR_APP] so that forwarding of private key is enabled
```
Host [SSH_CONFIG_NAME_FOR_APP]
...
  ForwardAgent yes
  ...
```
- In order to ssh to eth node add key
```
ssh-add [KEY_NAME]
```
- Now you can ssh to APP node and from there ssh to ETH node (which has private ip only)
```
ssh [SSH_CONFIG_NAME_FOR_APP]
ssh [ETH_NODE_IP_OR_HOSTNAME]
```

- Setup Ethereum network using instructions from README.md '_Setup Ethereum network_' section 