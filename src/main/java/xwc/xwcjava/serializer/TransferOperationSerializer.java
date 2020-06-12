package xwc.xwcjava.serializer;

import xwc.xwcjava.address.Address;
import xwc.xwcjava.crypto.CryptoUtil;
import xwc.xwcjava.exceptions.DeserializeException;
import xwc.xwcjava.exceptions.SerializeException;
import xwc.xwcjava.operation.TransferOperation;
import xwc.xwcjava.utils.IdUtil;
import xwc.xwcjava.utils.StringUtil;

import java.io.ByteArrayOutputStream;

public class TransferOperationSerializer implements ISerializer<TransferOperation> {
    private final String addressPrefix;

    public TransferOperationSerializer(String addressPrefix) {
        this.addressPrefix = addressPrefix;
    }

    @Override
    public byte[] serialize(TransferOperation instance) throws SerializeException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            AssetSerializer assetSerializer = new AssetSerializer();
            MemoSerializer memoSerializer = new MemoSerializer();
            UnsignedVarIntSerializer unsignedVarIntSerializer = new UnsignedVarIntSerializer();
            byte[] feeBytes = assetSerializer.serialize(instance.getFee());
            bos.write(feeBytes);
            if(!StringUtil.isEmpty(instance.getGuaranteeId())) {
                bos.write(1);
                int guaranteeIdValue = IdUtil.getId(instance.getGuaranteeId());
                byte[] guaranteeIdBytes = unsignedVarIntSerializer.serialize(guaranteeIdValue);
                bos.write(guaranteeIdBytes);
                byte[] length2Bytes = new byte[2];
                CryptoUtil.setBytesZero(length2Bytes);
                bos.write(length2Bytes);
            } else {
                byte[] length3Bytes = new byte[3];
                CryptoUtil.setBytesZero(length3Bytes);
                bos.write(length3Bytes);
            }
            Address fromAddr = Address.fromString(instance.getFromAddr(), addressPrefix);
            Address toAddr = Address.fromString(instance.getToAddr(), addressPrefix);
            bos.write(fromAddr.getAddyWithVersion());
            bos.write(toAddr.getAddyWithVersion());
            bos.write(assetSerializer.serialize(instance.getAmount()));
            bos.write(memoSerializer.serialize(instance.getMemo()));
            bos.write(0);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public TransferOperation deserialize(byte[] bytes) throws DeserializeException {
        return null;
    }
}
