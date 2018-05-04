# Commons OS API

## Environment

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

Proxy API and Web to corresponding servers
```
<VirtualHost *:80>
        DocumentRoot /path/to/folder/containing/index.html

        <Directory /path/to/folder/containing/index.html/>
          Require all granted
         	Allow from all
        </Directory>

        ProxyPreserveHost On

        <Location /api>
          ProxyPass http://localhost:4567 retry=0
          ProxyPassReverse http://localhost:4567
        </Location>

        ErrorLog ${APACHE_LOG_DIR}/error.log
        CustomLog ${APACHE_LOG_DIR}/access.log combined
</VirtualHost>
```

## Building

```
./build.sh
```

## Running

```
./start.sh
./stop.sh

```