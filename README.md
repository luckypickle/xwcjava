xwcjava
=========

xwc java sdk

# dependencies

* maven3
* jdk7+
* a xwc websocket rpc endpoint(start xwc_node with --rpc-endpoint=127.0.0.1:8090)
* if you want use public websocket rpc endpoint, you can use ws://8.210.70.217:10043

# Usage

* `mvn package install -Dmaven.test.skip=true` to install lib locally
* add dependency to pom.xml or build.gradle

```
<dependency>
    <groupId>xwc</groupId>
    <artifactId>xwcjava</artifactId>
    <version>1.0.0</version>
</dependency>
```

# Steps for send a transfer transaction

* generate new private key and address or get address from existed private key
* generate transfer or other transaction not signed
* use private key to sign the raw transaction
* use xwc websocket rpc endpoint to broadcast to signed transaction


# Demo

* generate private key and address

```
// example: src/test/java/xwc/xwcjava/test/address/AddressTests.java

ECKey ecKey = PrivateKeyGenerator.generate(); // generate a new private key-public key pair
String privateKeyHex = ecKey.getPrivateKeyAsHex(); // get the generated private key as hex format
String wif = PrivateKeyUtil.privateKeyToWif(ecKey); // get the generated private key as WIF format
log.info("privateKeyHex: {}", privateKeyHex);
log.info("privateKey wif: {}", wif);
Address address = Address.fromPubKey(ecKey.getPubKey(), AddressVersion.NORMAL); // get the XWC address from public key
log.info("address: {}", address);
```

* create and sign transfer transaction

```
// example: src/test/java/xwc/xwcjava/test/crypto/SignatureTests.java

String refInfo = "30375,575365464";
String chainId = Constants.mainnetChainId;
String wifStr = "";
String fromAddr = "";
String toAddr = "";
BigDecimal amount = new BigDecimal("0.001");
BigDecimal fee = new BigDecimal("0.0011");
String memo = "test";
Transaction tx = TransactionBuilder.createTransferTransaction(refInfo, fromAddr, toAddr, amount, Constants.XWC_ASSET_ID, 5, fee, memo, null);
String txJson = TransactionBuilder.signTransaction(tx, wifStr, chainId, Address.ADDRESS_PREFIX);
log.info("signed tx: {}", txJson);
// then you can use client rpc's lightwallet_broadcast or node rpc to broadcast this signed transaction json
```

* invoke contract transaction

```
// example: src/test/java/xwc/xwcjava/test/crypto/SignatureTests.java

String refInfo = "16573,2299525098";
String chainId = Constants.mainnetChainId;
String wifStr = "";
String callerAddr = "";
String callerPubKey = "";
String contractId = "XWCCHcRE3jsyHGrtoE2ZJZtpHsEiYTQ7VrHkb";
String contractApi = "balanceOf";
String contractArg = "";

long gasLimit = 10000;
long gasPrice = 1;

BigDecimal fee = new BigDecimal("0.003");

Transaction tx = TransactionBuilder.createContractInvokeTransaction(refInfo, callerAddr, callerPubKey,
        contractId, contractApi, contractArg, fee, gasLimit, gasPrice, null);
String txJson = TransactionBuilder.signTransaction(tx, wifStr, chainId, Address.ADDRESS_PREFIX);
log.info("signed tx: {}", txJson);
```

* transfer asset to contract transaction
```
// example: src/test/java/xwc/xwcjava/test/crypto/SignatureTests.java

String refInfo = getRefInfo();
String chainId = Constants.mainnetChainId;
String wifStr = "";
String callerAddr = "";
String callerPubKey = "";
String contractId = "XWCCHcRE3jsyHGrtoE2ZJZtpHsEiYTQ7VrHkb";
BigDecimal transferAmount = new BigDecimal("0.001");
String transferMemo = "hi";

long gasLimit = 10000;
long gasPrice = 1;

BigDecimal fee = new BigDecimal("0.003");

Transaction tx = TransactionBuilder.createContractTransferTransaction(refInfo, callerAddr, callerPubKey,
        contractId, transferAmount, Constants.XWC_ASSET_ID, Constants.xwcPrecision, transferMemo, fee, gasLimit, gasPrice, null);
String txJson = TransactionBuilder.signTransaction(tx, wifStr, chainId, Address.ADDRESS_PREFIX);
log.info("signed tx: {}", txJson);
```

* get data from network node rpc

```
String nodeRpcEndpoint = "ws://xwc-node-endpoint:port";
NodeClient nodeClient = new NodeClient(nodeRpcEndpoint);
try {
    nodeClient.open();
    nodeClient.sendLogin();
    String addr = "";
    String pubKeyStr = "";
    String contractId = "XWCCJb6BiDdSL9Duo9UtXaxncyRHcNsfQB7cV";
    String contractApi = "tokenName";
    String contractArg = "";
    String contractTxId = "";
    List<AmountInfoResponse> balances = nodeClient.getAddrBalances(addr);
    List<AssetInfoResponse> assets = nodeClient.listAssets();
    JSONObject testingResult = nodeClient.invokeContractTesting(pubKeyStr, contractId, contractApi, contractArg);
    String invokeResult = nodeClient.invokeContractOffline(pubKeyStr, contractId, contractApi, contractArg);
    log.info("balances: {}", JSON.toJSONString(balances));
    log.info("assets: {}", JSON.toJSONString(assets));
    log.info("testing result: {}", testingResult.toJSONString());
    log.info("invoke result: {}", invokeResult);
    String refInfo = nodeClient.constructRefInfo(6648413, "0065725d686b75c3d6576ecc17d5161c260d6dcd");
    log.info("example refInfo: {}", refInfo);
    JSONObject blockInfo = nodeClient.getBlock(100);
    log.info("blockInfo: {}", JSON.toJSONString(blockInfo));
    List<TxOperationReceiptResponse> txReceipts = nodeClient.getContractTxReceipts(contractTxId);
    log.info("txReceipts: {}", JSON.toJSONString(txReceipts));
} finally {
    nodeClient.close();
}
```