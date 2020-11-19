package xwc.xwcjava.builder;

import xwc.xwcjava.asset.Asset;
import xwc.xwcjava.asset.AssetUtil;
import xwc.xwcjava.config.Constants;
import xwc.xwcjava.crypto.CryptoUtil;
import xwc.xwcjava.exceptions.PubKeyInvalidException;
import xwc.xwcjava.exceptions.TransactionException;
import xwc.xwcjava.operation.*;
import xwc.xwcjava.pubkey.PubKeyUtil;
import xwc.xwcjava.serializer.TransactionSerializer;
import xwc.xwcjava.transaction.Memo;
import xwc.xwcjava.transaction.MemoUtil;
import xwc.xwcjava.transaction.RefBlockInfo;
import xwc.xwcjava.transaction.Transaction;
import xwc.xwcjava.utils.Numeric;
import xwc.xwcjava.utils.SignatureUtil;
import xwc.xwcjava.utils.StringUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransactionBuilder {
    private static final Logger log = LoggerFactory.getLogger(TransactionBuilder.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    static {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static Transaction createContractTransferTransaction(String refInfo, String callerAddr, String callerPubKey, String contractId,
                                                                BigDecimal transferAmount,
                                                                String assetId, int assetPrecision, String transferMemo, BigDecimal fee, Long gasLimit, Long gasPrice, String guaranteeId) throws TransactionException {
        long transferAmountFull = transferAmount.multiply(new BigDecimal(10).pow(assetPrecision)).longValue();

        Asset amountAsset = AssetUtil.defaultAsset();
        amountAsset.setAssetId(assetId);
        amountAsset.setAmount(transferAmountFull);

        long feeFull = fee.multiply(new BigDecimal(10).pow(Constants.xwcPrecision)).longValue();

        Asset feeAsset = AssetUtil.defaultAsset();
        feeAsset.setAmount(feeFull);

        ContractTransferOperation operation = OperationsUtil.defaultContractTransferOperation();
        operation.setFee(feeAsset);
        operation.setCallerAddr(callerAddr);
        if(callerPubKey!=null && callerPubKey.startsWith(PubKeyUtil.PUBKEY_STRING_PREFIX)) {
            // 把公钥从base58格式转换成hex格式
            try {
                callerPubKey = PubKeyUtil.base58PubKeyToHex(callerPubKey);
            } catch (PubKeyInvalidException e) {
                throw new TransactionException(e);
            }
        }
        operation.setCallerPubkey(callerPubKey);
        operation.setContractId(contractId);
        operation.setAmount(amountAsset);
        operation.setParam(transferMemo!=null?transferMemo:"");
        if(gasLimit!=null) {
            operation.setInvokeCost(gasLimit);
        }
        if(gasPrice!=null) {
            operation.setGasPrice(gasPrice);
        }

        if(!StringUtil.isEmpty(guaranteeId)) {
            operation.setGuaranteeId(guaranteeId);
        }

        long expireSec = (System.currentTimeMillis() / 1000) + Constants.expireTimeout;
        String expireStr = sdf.format(new Date(expireSec * 1000)); // expire_str := "2018-09-26T09:14:40"

        RefBlockInfo refBlockInfo = RefBlockInfo.decodeFromInfoString(refInfo);
        int refBlockNum = refBlockInfo.getRefBlockNum();
        long refBlockPrefix = refBlockInfo.getRefBlockPrefix();
        Transaction tx = new Transaction();
        tx.setRefBlockNum(refBlockNum);
        tx.setRefBlockPrefix(refBlockPrefix);
        tx.setExpiration(expireStr);
        tx.setTransientExpiration(expireSec);
        tx.setOperations(Collections.singletonList(Arrays.asList(operation.getOperationType(), operation)));
        tx.setExtensions(new ArrayList<>());
        tx.setSignatures(new ArrayList<String>());
        tx.setTransientOperations(Collections.singletonList((IOperation) operation));
        return tx;
    }

    public static Transaction createContractInvokeTransaction(String refInfo, String callerAddr, String callerPubKey, String contractId,
                                                              String contractApi, String contractArg, BigDecimal fee, Long gasLimit, Long gasPrice, String guaranteeId) throws TransactionException {
        long feeFull = fee.multiply(new BigDecimal(10).pow(Constants.xwcPrecision)).longValue();

        Asset feeAsset = AssetUtil.defaultAsset();
        feeAsset.setAmount(feeFull);

        ContractInvokeOperation operation = OperationsUtil.defaultContractInvokeOperation();
        operation.setFee(feeAsset);
        operation.setCallerAddr(callerAddr);
        if(callerPubKey!=null && callerPubKey.startsWith(PubKeyUtil.PUBKEY_STRING_PREFIX)) {
            // 把公钥从base58格式转换成hex格式
            try {
                callerPubKey = PubKeyUtil.base58PubKeyToHex(callerPubKey);
            } catch (PubKeyInvalidException e) {
                throw new TransactionException(e);
            }
        }
        operation.setCallerPubkey(callerPubKey);
        operation.setContractId(contractId);
        operation.setContractApi(contractApi);
        operation.setContractArg(contractArg);
        if(gasLimit!=null) {
            operation.setInvokeCost(gasLimit);
        }
        if(gasPrice!=null) {
            operation.setGasPrice(gasPrice);
        }

        if(!StringUtil.isEmpty(guaranteeId)) {
            operation.setGuaranteeId(guaranteeId);
        }

        long expireSec = (System.currentTimeMillis() / 1000) + Constants.expireTimeout;
        String expireStr = sdf.format(new Date(expireSec * 1000)); // expire_str := "2018-09-26T09:14:40"

        RefBlockInfo refBlockInfo = RefBlockInfo.decodeFromInfoString(refInfo);
        int refBlockNum = refBlockInfo.getRefBlockNum();
        long refBlockPrefix = refBlockInfo.getRefBlockPrefix();
        Transaction tx = new Transaction();
        tx.setRefBlockNum(refBlockNum);
        tx.setRefBlockPrefix(refBlockPrefix);
        tx.setExpiration(expireStr);
        tx.setTransientExpiration(expireSec);
        tx.setOperations(Collections.singletonList(Arrays.asList(operation.getOperationType(), operation)));
        tx.setExtensions(new ArrayList<>());
        tx.setSignatures(new ArrayList<String>());
        tx.setTransientOperations(Collections.singletonList((IOperation) operation));
        return tx;
    }

    public static Transaction createTransferTransaction(String refInfo, String fromAddr, String toAddr, BigDecimal transferAmount,
                                                        String assetId, int assetPrecision, BigDecimal fee, String memo,
                                                        String guaranteeId) throws TransactionException {
        long transferAmountFull = transferAmount.multiply(new BigDecimal(10).pow(assetPrecision)).longValue();
        long feeFull = fee.multiply(new BigDecimal(10).pow(Constants.xwcPrecision)).longValue();

        Asset amountAsset = AssetUtil.defaultAsset();
        amountAsset.setAssetId(assetId);
        amountAsset.setAmount(transferAmountFull);

        Asset feeAsset = AssetUtil.defaultAsset();
        feeAsset.setAmount(feeFull);

        TransferOperation transferOperation = OperationsUtil.defaultTransferOperation();
        transferOperation.setFee(feeAsset);
        transferOperation.setAmount(amountAsset);
        transferOperation.setFromAddr(fromAddr);
        transferOperation.setToAddr(toAddr);

        if(StringUtil.isEmpty(memo)) {
            transferOperation.setMemo(null);
        } else {
            Memo memoObj = MemoUtil.defaultMemo();
            memoObj.setTransientMessage(memo);
            memoObj.setEmpty(false);
            byte[] zero4 = new byte[4];
            CryptoUtil.setBytesZero(zero4);
            byte[] memoBytes = CryptoUtil.bytesMerge(zero4, memo.getBytes());
            memoObj.setMessage(Numeric.toHexStringNoPrefix(memoBytes));
            transferOperation.setMemo(memoObj);
        }

        if(!StringUtil.isEmpty(guaranteeId)) {
            transferOperation.setGuaranteeId(guaranteeId);
        }

        long expireSec = (System.currentTimeMillis() / 1000) + Constants.expireTimeout;
        String expireStr = sdf.format(new Date(expireSec * 1000)); // expire_str := "2018-09-26T09:14:40"

        RefBlockInfo refBlockInfo = RefBlockInfo.decodeFromInfoString(refInfo);
        int refBlockNum = refBlockInfo.getRefBlockNum();
        long refBlockPrefix = refBlockInfo.getRefBlockPrefix();
        Transaction tx = new Transaction();
        tx.setRefBlockNum(refBlockNum);
        tx.setRefBlockPrefix(refBlockPrefix);
        tx.setExpiration(expireStr);
        tx.setTransientExpiration(expireSec);
        tx.setOperations(Collections.singletonList(Arrays.asList(transferOperation.getOperationType(), transferOperation)));
        tx.setExtensions(new ArrayList<>());
        tx.setSignatures(new ArrayList<String>());
        tx.setTransientOperations(Collections.singletonList((IOperation) transferOperation));
        return tx;
    }

    public static String signTransaction(Transaction transaction, String wifStr, String chainId, String addressPrefix)
            throws TransactionException {
        TransactionSerializer txSerializer = new TransactionSerializer(addressPrefix);
        try {
            byte[] txBytes = txSerializer.serialize(transaction);
            log.debug("tx hex: {}", Numeric.toHexStringNoPrefix(txBytes));
            byte[] chainIdBytes = Numeric.hexStringToByteArray(chainId);
            byte[] toSignBytes = CryptoUtil.bytesMerge(chainIdBytes, txBytes);
            byte[] sig = SignatureUtil.getSignature(wifStr, toSignBytes);
            List<String> signatures = transaction.getSignatures();
            if(signatures==null) {
                signatures = new ArrayList<>();
            }
            signatures.add(Numeric.toHexStringNoPrefix(sig));
            transaction.setSignatures(signatures);
            return JSON.toJSONString(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            throw new TransactionException(e);
        }
    }
}
