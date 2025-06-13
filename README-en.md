<h1 align="center">Java SDK for APIs Efí Pay</h1>

![Banner APIs Efí Pay](https://gnetbr.com/BJgSIUhlYs)

<p align="center">
  <a href="https://github.com/efipay/sdk-java-apis-efi">Portuguese</a> |
  <span><b>English</b></span>  
</p>

SDK in JAVA for integration with Efí APIs for emission Pix, bank slips, carnet, credit card, subscription, payment link, marketplance, Pix through Open Finance, among other features.
For more informations about [parameters](http://dev.sejaefi.com.br) and [values](http://sejaefi.com.br/tarifas) see our website.



## Requirements
* Java >= 7.0

## Tested with
> java 7.0, 8.0, 13.0 and 18.0

## Installation
**Via gradle:**

```gradle
implementation 'br.com.efipay.efisdk:sdk-java-apis-efi:1.2.3'
```

**Via maven:**

```xml
<dependency>
    <groupId>br.com.efipay.efisdk</groupId>
	  <artifactId>sdk-java-apis-efi</artifactId>
	  <version>1.2.3</version>
</dependency>
```

## Getting started
Require the module and packages:
```java
import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;

```

Although the web services responses are in json format, the sdk will convert any server response to a JSONObject or a Map<String, Object>. The code must be within a try-catch and exceptions can be handled as follow:

```java
try {
  /* code */
} catch(EfiPayException e) {
  /* EfiPay's api errors will come here */
} catch(Exception ex) {
  /* Other errors will come here */
}
```


### For development environment
Instantiate the module parameters using `client_id`, `client_secret`, `sandbox` equal to **true** and `certificate` with the name of the approval certificate:
```java
JSONObject options = new JSONObject();
options.put("client_id", "client_id");
options.put("client_secret", "client_secret");
options.put("certificate", "./certs/developmentCertificate.p12");
options.put("sandbox", true);

EfiPay efi = new EfiPay(options);
```

Or

```java
Map<String, Object> options = new HashMap<String, Object>();
options.put("client_id", "client_id");
options.put("client_secret", "client_secret");
options.put("certificate", "./certs/developmentCertificate.p12");
options.put("sandbox", true);

EfiPay efi = new EfiPay(options);
```

### For production environment
Instantiate the module parameters using `client_id`, `client_secret`, `sandbox` equals *false* and `certificate` with the name of the production certificate:
```java
JSONObject options = new JSONObject();
options.put("client_id", "client_id");
options.put("client_secret", "client_secret");
options.put("certificate", "./certs/productionCertificate.p12");
options.put("sandbox", false);

EfiPay efi = new EfiPay(options);
```
Or

```java
Map<String, Object> options = new HashMap<String, Object>();
options.put("client_id", "client_id");
options.put("client_secret", "client_secret");
options.put("certificate", "./certs/productionCertificate.p12");
options.put("sandbox", false);

EfiPay efi = new EfiPay(options);
```

## Running tests

To run the test suite:

```bash
mvn clean test jacoco:report
```
## Running examples
To run some existing examples follow the steps described at [sdk-java-examples-apis-efi](https://github.com/efipay/sdk-java-examples-apis-efi).

## Additional Documentation

The full documentation with all available endpoints is in https://dev.efipay.com.br.

## Changelog

[CHANGELOG](CHANGELOG.md)

<!-- ## License ##
[MIT](LICENSE) -->
