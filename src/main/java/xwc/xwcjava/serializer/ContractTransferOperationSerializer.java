package xwc.xwcjava.serializer;

import xwc.xwcjava.address.Address;
import xwc.xwcjava.exceptions.DeserializeException;
import xwc.xwcjava.exceptions.SerializeException;
import xwc.xwcjava.operation.ContractTransferOperation;
import xwc.xwcjava.pubkey.PubKeyBytes;
import xwc.xwcjava.pubkey.PubKeyUtil;
import xwc.xwcjava.utils.IdUtil;
import xwc.xwcjava.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

public class ContractTransferOperationSerializer implements ISerializer<ContractTransferOperation> {
    private static final Logger log = LoggerFactory.getLogger(ContractTransferOperationSerializer.class);

    private final String addressPrefix;

    public ContractTransferOperationSerializer(String addressPrefix) {
        this.addressPrefix = addressPrefix;
    }

    @Override
    public byte[] serialize(ContractTransferOperation instance) throws SerializeException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            AssetSerializer assetSerializer = new AssetSerializer();
            Uint64Serializer uint64Serializer = Uint64Serializer.defaultInstance();
            StringSerializer stringSerializer = StringSerializer.defaultInstance();
            UnsignedVarIntSerializer unsignedVarIntSerializer = new UnsignedVarIntSerializer();
            PubkeySerializer pubkeySerializer = new PubkeySerializer();
            // fee
            bos.write(assetSerializer.serialize(instance.getFee()));
            // invoke_cost
            bos.write(uint64Serializer.serialize(instance.getInvokeCost()));
            // gas_price
            bos.write(uint64Serializer.serialize(instance.getGasPrice()));
            // write caller_addr
            Address callerAddr = Address.fromString(instance.getCallerAddr(), addressPrefix);
            bos.write(callerAddr.getAddyWithVersion());
            // write caller_pubkey
            byte[] callerPubkeyBytes = PubKeyUtil.getPubKeyBytes(instance.getCallerPubkey());
            if(callerPubkeyBytes.length != PubKeyUtil.PUBKEY_BYTES_SIZE) {
                throw new SerializeException("invalid caller pubKey hex");
            }
            bos.write(pubkeySerializer.serialize(new PubKeyBytes(callerPubkeyBytes)));
            // write contract_id
            Address contractId = Address.fromString(instance.getContractId(), addressPrefix);
            bos.write(contractId.getAddyWithVersion());
            // write amount
            bos.write(assetSerializer.serialize(instance.getAmount()));
            // write param
            bos.write(stringSerializer.serialize(instance.getParam()));
            // write guaranteeId
            if(!StringUtil.isEmpty(instance.getGuaranteeId())) {
                bos.write(1);
                int guaranteeIdValue = IdUtil.getId(instance.getGuaranteeId());
                byte[] guaranteeIdBytes = unsignedVarIntSerializer.serialize(guaranteeIdValue);
                bos.write(guaranteeIdBytes);
            } else {
                bos.write(0);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public ContractTransferOperation deserialize(byte[] bytes) throws DeserializeException {
        return null;
    }
}
