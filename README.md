<h1 align="center">SDK Java para APIs Efí Pay</h1>

![Banner APIs Efí Pay](https://gnetbr.com/BJgSIUhlYs)

<p align="center">
  <span><b>Português</b></span> |
  <a href="https://github.com/efipay/sdk-java-apis-efi/blob/master/README-en.md">Inglês</a>
</p>

SDK em JAVA para integração com as APIs Efí para emissão de Pix, boletos, carnês, cartão de crédito, assinatura, link de pagamento, marketplance, Pix via Open Finance, pagamento de boletos, dentre outras funcionalidades.
Para mais informações sobre [parâmetros](http://sejaefi.com.br/api) e [valores/tarifas](http://sejaefi.com.br/tarifas) consulte nosso site.




## Requisitos
* Java >= 7.0

## Testado com
> java 7.0, 8.0, 13.0 e 18.0

## Instalação
**Via gradle:**

```gradle
implementation 'br.com.efipay.efisdk:sdk-java-apis-efi:1.0.5'
```

**Via maven:**

```xml
<dependency>
    <groupId>br.com.efipay.efisdk</groupId>
	  <artifactId>sdk-java-apis-efi</artifactId>
	  <version>1.0.6</version>
</dependency>
```

## Começando
Requisite os módulos e pacotes:
```java
import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;

```

Embora as respostas dos serviços da Web estejam no formato json, a sdk converterá qualquer resposta do servidor em um JSONObject ou um Map<String, Object>. O código deve estar dentro de um try-catch e as exceções podem ser tratadas da seguinte forma:

```java
try {
  /* code */
} catch(EfiPayException e) {
  /* EfiPay's api errors will come here */
} catch(Exception ex) {
  /* Other errors will come here */
}
```

### Para ambiente de homologação
Instancie os parâmetros do módulo usando `client_id`, `client_secret`, `sandbox` igual a **true** e `certificate` com o nome do certificado de homologação:
```java
JSONObject options = new JSONObject();
options.put("client_id", "client_id");
options.put("client_secret", "client_secret");
options.put("certificate", "./certs/developmentCertificate.p12");
options.put("sandbox", true);

EfiPay efi = new EfiPay(options);
```

Ou

```java
Map<String, Object> options = new HashMap<String, Object>();
options.put("client_id", "client_id");
options.put("client_secret", "client_secret");
options.put("certificate", "./certs/developmentCertificate.p12");
options.put("sandbox", true);

EfiPay efi = new EfiPay(options);
```

### Para ambiente de produção
Instancie os parâmetros do módulo usando `client_id`, `client_secret`, `sandbox` igual a *false* e `certificate` com o nome do certificado de produção:
```java
JSONObject options = new JSONObject();
options.put("client_id", "client_id");
options.put("client_secret", "client_secret");
options.put("certificate", "./certs/productionCertificate.p12");
options.put("sandbox", false);

EfiPay efi = new EfiPay(options);
```
Ou

```java
Map<String, Object> options = new HashMap<String, Object>();
options.put("client_id", "client_id");
options.put("client_secret", "client_secret");
options.put("certificate", "./certs/productionCertificate.p12");
options.put("sandbox", false);

EfiPay efi = new EfiPay(options);
```

## Executando testes

Para executar o conjunto de testes:

```bash
mvn clean test jacoco:report
```
## Execução de exemplos
Para executar alguns exemplos existentes, siga as etapas descritas em [sdk-java-examples-apis-efi](https://github.com/efipay/sdk-java-examples-apis-efi).

## Documentação Adicional

A documentação completa com todos os endpoints disponíveis está em https://dev.efipay.com.br.

## Changelog

[CHANGELOG](CHANGELOG.md)

<!-- ## License ##
[MIT](LICENSE) -->