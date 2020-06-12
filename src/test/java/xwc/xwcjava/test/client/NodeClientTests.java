package xwc.xwcjava.test.client;

import xwc.xwcjava.client.NodeClient;
import xwc.xwcjava.client.response.AmountInfoResponse;
import xwc.xwcjava.client.response.AssetInfoResponse;
import xwc.xwcjava.client.response.TxOperationReceiptResponse;
import xwc.xwcjava.operation.NodeException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NodeClientTests {
    private static final Logger log = LoggerFactory.getLogger(NodeClientTests.class);

    @Test
    public void testNodeClientGetInfo() throws NodeException {
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
            String refInfoFromNode = nodeClient.getRefInfo();
            log.info("refInfoFromNode: {}", refInfoFromNode);
            JSONObject blockInfo = nodeClient.getBlock(100);
            log.info("blockInfo: {}", JSON.toJSONString(blockInfo));
            List<TxOperationReceiptResponse> txReceipts = nodeClient.getContractTxReceipts(contractTxId);
            log.info("txReceipts: {}", JSON.toJSONString(txReceipts));
        } finally {
            nodeClient.close();
        }
    }
}
