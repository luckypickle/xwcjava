package xwc.xwcjava.serializer;

import xwc.xwcjava.exceptions.DeserializeException;
import xwc.xwcjava.exceptions.SerializeException;

public interface ISerializer<T> {
    byte[] serialize(T instance) throws SerializeException;

    T deserialize(byte[] bytes) throws DeserializeException;
}
