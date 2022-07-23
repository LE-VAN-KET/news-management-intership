# Project build application post news

### Stack technology use:
* Mongodb atlas
* Spring boot
* Spring security
* Docker
* MinIo
* Redis cache

### Guides to Generating Private/Public Ket RSA Using keytool For Sign JWT
+ Using terminal command: keytool -genkeypair -alias test_auth -keyalg RSA -keypass mypass -keystore auth.jks -storepass mypass
+ There for, continue command: keytool -list -rfc --keystore test_auth.jks | openssl x509 -inform pem -pubkey

### Guides Build Docker Container
* Build container: docker-compose up -d
* Destroy container: docker-compose down
