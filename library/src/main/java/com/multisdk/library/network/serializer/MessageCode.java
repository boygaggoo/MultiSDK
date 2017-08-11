package com.multisdk.library.network.serializer;

import com.multisdk.library.network.utils.DESUtil;
import com.multisdk.library.network.config.HttpConfig;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessageCode {

  private byte[] mKey;

  public MessageCode() {
    mKey = HttpConfig.MESSAGE_CODE_KEY.getBytes();
  }

  /***
   * 编码包
   *
   * @param msg
   * @return
   * @throws Exception
   */
  public byte[] serializeMessage(CommMessage msg) throws Exception {
    try {
      msg.head.length = HttpConfig.PROTOCOL_HEAD_LENGTH;

      if (msg.message == null) {
        throw new Exception("msg body is null.");
      }
      byte[] bodyMessageArray = serializeObject(msg.message);

      SignalCode attribute = AttributeUtil.getMessageAttribute(msg.message);
      if (attribute != null && attribute.encrypt()) {
        bodyMessageArray = DESUtil.encrypt(bodyMessageArray, mKey);
      }
      msg.head.length += bodyMessageArray.length;
      byte[] headMessageArray = serializeObject(msg.head);
      byte[] wholeMessageArray = new byte[headMessageArray.length + bodyMessageArray.length];
      System.arraycopy(headMessageArray, 0, wholeMessageArray, 0, headMessageArray.length);
      System.arraycopy(bodyMessageArray, 0, wholeMessageArray, headMessageArray.length,
          bodyMessageArray.length);
      return wholeMessageArray;
    } catch (Exception e) {
      throw new Exception(" " + msg.message.getClass() + "    " + e.getLocalizedMessage());
    }
  }

  /***
   * 解码包头
   *
   * @param buff
   * @param offset
   * @throws Exception
   */
  public CommMessageHead deserializeHead(byte[] buff, int offset) throws Exception {
    CommMessageHead head = null;
    try {
      if (offset + HttpConfig.PROTOCOL_HEAD_LENGTH > buff.length) {
        throw new Exception("head len error.");
      }
      TempFieldResult result = deserializeObject(buff, offset, CommMessageHead.class);
      head = (CommMessageHead) result.value;
    } catch (Exception e) {
      throw new Exception("de head error." + e.getLocalizedMessage());
    }
    return head;
  }

  /***
   * 解码包体
   *
   * @param buff
   * @param offset
   * @param size
   * @param messageCode
   * @throws Exception
   */
  public Object deserializeBody(byte[] buff, int offset, int size, int messageCode)
      throws Exception {
    TempFieldResult result = null;
    try {
      byte[] bodyMessageArray = new byte[size];
      System.arraycopy(buff, offset, bodyMessageArray, 0, size);
      Class<?> cls = MessageRecognizer.getClassByCode(messageCode);
      if (cls == null) {
        int i = 8031, j = 8032;
        throw new Exception(i + "    " + messageCode + "     " + j);
      }
      SignalCode m = AttributeUtil.getMessageAttribute(cls);
      if (m != null && m.encrypt()) {
        bodyMessageArray = DESUtil.decrypt(bodyMessageArray, mKey);
      }
      result = deserializeObject(bodyMessageArray, 0, cls);
    } catch (Exception e) {
      int i = 8033;
      throw new Exception(i + "    " + e.getLocalizedMessage());
    }
    return result.value;
  }

  public byte[] serializeObject(Object obj) throws Exception {
    List<TempField> fieldArray = getFieldList(obj.getClass());
    List<Byte> serializedList = new ArrayList<Byte>();
    for (int i = 0; i < fieldArray.size(); i++) {
      TempField tempfield = fieldArray.get(i);
      tempfield.fieldInfo.setAccessible(true);
      if (tempfield.fieldInfo.getType().isPrimitive()
          || isWrapClass(tempfield.fieldInfo.getType())
          || tempfield.fieldInfo.getType() == String.class) {
        byte[] array =
            primitiveObject2Buffer(tempfield.fieldInfo.getType(), tempfield.fieldInfo.get(obj));
        /*
         * Logger.debug(tempfield.fieldInfo.getName() + "=" +
         * HTTPConnection.getByteArrayStr(array));
         */

        addByteArray2List(serializedList, array, 0, array.length);
      } else if (tempfield.fieldInfo.getType().isArray()) {
        Object arrayObj = tempfield.fieldInfo.get(obj);
        int arrayLen = Array.getLength(arrayObj);

        // 集合数据需要添加一个长度字段
        byte[] countBytes = new byte[4];
        ByteUtil.putInt(countBytes, arrayLen, 0);
        addByteArray2List(serializedList, countBytes, 0, countBytes.length);

        if (tempfield.fieldInfo.getType().getComponentType().isPrimitive()
            || isWrapClass(tempfield.fieldInfo.getType().getComponentType())
            || tempfield.fieldInfo.getType().getComponentType() == String.class) {
          for (int l = 0; l < arrayLen; l++) {
            byte[] array = primitiveObject2Buffer(tempfield.fieldInfo.getType().getComponentType(),
                Array.get(arrayObj, l));
            addByteArray2List(serializedList, array, 0, array.length);
          }
        } else {
          int m = 0000;
          throw new Exception(tempfield.fieldInfo.getName() + "     " + m);
        }
      } else if (List.class.isAssignableFrom(tempfield.fieldInfo.getType())) {
        List<?> arrayObj = (List<?>) tempfield.fieldInfo.get(obj);
        int arrayLen = arrayObj.size();
        // 集合数据需要添加一个长度字段
        byte[] countBytes = new byte[4];
        ByteUtil.putInt(countBytes, arrayLen, 0);
        addByteArray2List(serializedList, countBytes, 0, countBytes.length);

        Type fc = tempfield.fieldInfo.getGenericType();
        if (fc instanceof ParameterizedType) {
          ParameterizedType pt = (ParameterizedType) fc;
          Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];

          if (genericClazz.isPrimitive()
              || isWrapClass(genericClazz)
              || genericClazz == String.class) {
            for (int l = 0; l < arrayLen; l++) {
              byte[] array = primitiveObject2Buffer(genericClazz, arrayObj.get(l));
              addByteArray2List(serializedList, array, 0, array.length);
            }
          } else {
            for (int l = 0; l < arrayLen; l++) {
              byte[] array = serializeObject(arrayObj.get(l));
              addByteArray2List(serializedList, array, 0, array.length);
            }
          }
        }
      } else {
        byte[] buff = serializeObject(tempfield.fieldInfo.get(obj));
        addByteArray2List(serializedList, buff, 0, buff.length);
      }
    }
    return toByteArray(serializedList);
  }

  private byte[] primitiveObject2Buffer(Class<?> cls, Object obj)
      throws UnsupportedEncodingException {
    byte[] array = null;
    if (cls == byte.class || cls == Byte.class) {
      byte value = (Byte) obj;
      array = new byte[1];
      array[0] = value;
    }
    if (cls == char.class || cls == Character.class) {
      char value = (Character) obj;
      array = new byte[1];
      array[0] = (byte) value;
    } else if (cls == short.class || cls == Short.class) {
      short value = (Short) obj;
      array = new byte[2];
      ByteUtil.putShort(array, value, 0);
    } else if (cls == int.class || cls == Integer.class) {
      int value = (Integer) obj;
      array = new byte[4];
      ByteUtil.putInt(array, value, 0);
    } else if (cls == long.class || cls == Long.class) {
      long value = (Long) obj;
      array = new byte[8];
      ByteUtil.putLong(array, value, 0);
    } else if (cls == float.class || cls == Float.class) {
      float value = (Float) obj;
      array = new byte[4];
      ByteUtil.putFloat(array, value, 0);
    } else if (cls == double.class || cls == Double.class) {
      double value = (Double) obj;
      array = new byte[8];
      ByteUtil.putDouble(array, value, 0);
    } else if (cls == boolean.class || cls == Boolean.class) {
      boolean value = (Boolean) obj;
      array = new byte[1];
      if (value) {
        array[0] = 0x1;
      } else {
        array[0] = 0x0;
      }
    } else if (cls == String.class) {
      if (obj == null) {
        obj = "";
      }
      byte[] t = ((String) obj).getBytes("UTF-8");
      array = new byte[t.length + 1];
      System.arraycopy(t, 0, array, 0, t.length);
      array[t.length] = '\0';
    }
    return array;
  }

  public TempFieldResult deserializeObject(byte[] array, int offset, Class<?> cls)
      throws Exception {
    TempFieldResult ret = new TempFieldResult();
    ret.value = cls.newInstance();

    List<TempField> fieldArray = getFieldList(cls);
    for (int i = 0; i < fieldArray.size(); i++) {
      TempField tempfield = fieldArray.get(i);
      tempfield.fieldInfo.setAccessible(true);
      if (tempfield.fieldInfo.getType().isPrimitive()
          || isWrapClass(tempfield.fieldInfo.getType())
          || tempfield.fieldInfo.getType() == String.class) {
        TempFieldResult result =
            buffer2PrimitiveObject(array, offset, tempfield.fieldInfo.getType(), tempfield);
        offset += result.length;
        tempfield.fieldInfo.set(ret.value, result.value);
      } else if (tempfield.fieldInfo.getType().isArray()) {
        // 集合长度
        int arrayLen = ByteUtil.getInt(array, offset);
        offset += 4;

        if (tempfield.fieldInfo.getType().getComponentType().isPrimitive()
            || isWrapClass(tempfield.fieldInfo.getType().getComponentType())
            || tempfield.fieldInfo.getType().getComponentType() == String.class) {
          for (int l = 0; l < arrayLen; l++) {
            TempFieldResult result = buffer2PrimitiveObject(array, offset,
                tempfield.fieldInfo.getType().getComponentType(), tempfield);
            offset += result.length;
            Array.set(tempfield.fieldInfo.get(ret.value), l, result.value);
          }
        } else {
          int n = 9999;
          throw new Exception(tempfield.fieldInfo.getName() + "     " + n);
        }

        System.out.print("\n");
      } else if (List.class.isAssignableFrom(tempfield.fieldInfo.getType())) {
        // 集合长度
        int arrayLen = ByteUtil.getInt(array, offset);
        offset += 4;

        Type fc = tempfield.fieldInfo.getGenericType();
        if (fc instanceof ParameterizedType) {
          ParameterizedType pt = (ParameterizedType) fc;
          Class<?> genericClazz = (Class) pt.getActualTypeArguments()[0];

          List objList = new ArrayList();
          if (genericClazz.isPrimitive()
              || isWrapClass(genericClazz)
              || genericClazz == String.class) {
            for (int l = 0; l < arrayLen; l++) {
              TempFieldResult result =
                  buffer2PrimitiveObject(array, offset, genericClazz, tempfield);
              offset += result.length;
              objList.add(result.value);
            }
          } else {
            for (int l = 0; l < arrayLen; l++) {
              TempFieldResult result = deserializeObject(array, offset, genericClazz);
              offset = result.length;
              objList.add(result.value);
            }
          }
          tempfield.fieldInfo.set(ret.value, objList);
        }
      } else {

        TempFieldResult result = deserializeObject(array, offset, tempfield.fieldInfo.getType());
        offset = result.length;
        tempfield.fieldInfo.set(ret.value, result.value);
      }
    }
    ret.length = offset;
    return ret;
  }

  private TempFieldResult buffer2PrimitiveObject(byte[] valueBytes, int offset, Class<?> cls,
      TempField tempfield) throws Exception {
    TempFieldResult result = new TempFieldResult();
    if (cls == byte.class || cls == Byte.class) {
      result.length = 1;
      result.value = valueBytes[offset];
    }
    if (cls == char.class || cls == Character.class) {
      result.length = 1;
      result.value = (char) (valueBytes[offset]);
    } else if (cls == short.class || cls == Short.class) {
      result.length = 2;
      result.value = ByteUtil.getShort(valueBytes, offset);
    } else if (cls == int.class || cls == Integer.class) {
      if (tempfield.attrib.bytes() == 1) {
        result.length = 1;
        result.value = ByteUtil.b2ui(valueBytes[offset]);
      } else {
        result.length = 4;
        result.value = ByteUtil.getInt(valueBytes, offset);
      }
    } else if (cls == long.class || cls == Long.class) {
      result.length = 8;
      result.value = ByteUtil.getLong(valueBytes, offset);
    } else if (cls == float.class || cls == Float.class) {
      result.length = 4;
      result.value = ByteUtil.getFloat(valueBytes, offset);
    } else if (cls == double.class || cls == Double.class) {
      result.length = 8;
      result.value = ByteUtil.getDouble(valueBytes, offset);
    } else if (cls == boolean.class || cls == Boolean.class) {
      result.length = 1;
      result.value = valueBytes[offset] == 0x1;
    } else if (cls == String.class) {
      List<Byte> blist = new ArrayList<Byte>();
      for (int l = offset; l < valueBytes.length; l++) {
        result.length++;
        if (valueBytes[l] == '\0') {
          break;
        }
        blist.add(valueBytes[l]);
      }
      byte[] b = toByteArray(blist);
      result.value = new String(b, "UTF-8");
    }
    return result;
  }

  private List<TempField> getFieldList(Class<?> cls) {
    List<TempField> fieldArray = new ArrayList<TempField>();
    ArrayList<Field> fields = getAllField(cls);
    for (int i = 0; i < fields.size(); i++) {
      ByteField attrib = AttributeUtil.getFieldAttribute(fields.get(i));
      if (attrib == null) {
        continue;
      }
      TempField tempfield = new TempField();
      tempfield.attrib = attrib;
      tempfield.fieldInfo = fields.get(i);
      fieldArray.add(tempfield);
    }
    Comparator comp = new TempFieldComparer();
    Collections.sort(fieldArray, comp);
    return fieldArray;
  }

  private ArrayList<Field> getAllField(Class cls) {
    ArrayList<Field> list = new ArrayList<Field>();
    Field[] fields = cls.getDeclaredFields();
    Collections.addAll(list, fields);
    if (cls.getSuperclass() != Object.class) {
      list.addAll(getAllField(cls.getSuperclass()));
    }
    return list;
  }

  private class TempFieldResult {
    int length;
    public Object value;
  }

  private class TempField {
    ByteField attrib;
    Field fieldInfo;
  }

  private class TempFieldComparer implements Comparator {
    public int compare(Object o1, Object o2) {
      TempField p1 = (TempField) o1;
      TempField p2 = (TempField) o2;
      if (p1.attrib.index() > p2.attrib.index()) {
        return 1;
      } else {
        return -1;
      }
    }
  }

  private boolean isWrapClass(Class<?> clz) {
    try {
      return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();
    } catch (Exception e) {
      return false;
    }
  }

  private void addByteArray2List(List<Byte> list, byte[] array, int offset, int len) {
    for (int i = offset; i < array.length && i < len; i++) {
      list.add(array[i]);
    }
  }

  private byte[] toByteArray(List in) {
    final int n = in.size();
    byte ret[] = new byte[n];
    for (int i = 0; i < n; i++) {
      ret[i] = (Byte) in.get(i);
    }
    return ret;
  }
}
