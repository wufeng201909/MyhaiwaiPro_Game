package com.sdk.mysdklibrary.Tools;

import com.sdk.mysdklibrary.MyApplication;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密类
 * 
 */
public class AESSecurity {


	private static String iv= "1234567890123456";
	/**
	 * AES加密
	 * @param key 加密需要的KEY
	 * @param data 需要加密的数据
	 * @return
	 *
	 */


	public static String encrypt(String data, String key) {
		byte[] encrypted = {};
		byte[] enCodeFormat = key.getBytes();
		SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat,"AES");

		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

			cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec,new IvParameterSpec(iv.getBytes()));

			int blockSize = cipher.getBlockSize();
			System.out.println(data.length());
			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}
			System.out.println(plaintextLength);
			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
			encrypted = cipher.doFinal(plaintext);

		} catch (Exception e) {
			e.printStackTrace();
			PhoneTool.submitErrorEvent(Configs.getAppErrorCode(),"AES:"+ e.getMessage());
		}

		return new String(Base64.encode(encrypted));
	}

	//public static String decrypt(String input, String key)
	// (String key,String iv,byte[] data)
	public static String decrypt(String input, String key)  {

		String content = input;

		byte[] enCodeFormat = key.getBytes();
		SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = null;// 创建密码器
		try {
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec,new IvParameterSpec(iv.getBytes()));// 初始化
			//			output= cipher.doFinal(Base64.decode(input));
			byte[] result = cipher.doFinal(Base64.decode(input));
			content = new String(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return content; // 加密
	}



//	public static String encrypt(String input, String key) {
//		byte[] crypted = null;
//		try {
//			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
//			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//			cipher.init(Cipher.ENCRYPT_MODE, skey);
//			crypted = cipher.doFinal(input.getBytes());
//
////			// 获取Cipher
////			Cipher cipher = Cipher.getInstance("AES");
////			// 生成密钥
////			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
////			// 创建初始化向量
////			IvParameterSpec iv = new IvParameterSpec(key.getBytes());
////			cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
////			crypted = cipher.doFinal(input.getBytes());
//
////			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
////			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, key.getBytes());
////			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
////			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
////			crypted= cipher.doFinal(input.getBytes());
//
//
//
//
//		} catch (Exception e) {
//			MLog.b(e.toString());
//		}
//		return new String(Base64.encode(crypted));
//	}

//	public static String decrypt(String input, String key) {
//		byte[] output = null;
//		try {
////			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
////			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
////			cipher.init(Cipher.DECRYPT_MODE, skey);
////			output = cipher.doFinal(Base64.decode(input));
//
////			// 获取Cipher
////			Cipher cipher = Cipher.getInstance("AES");
////			// 生成密钥
////			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
////			// 指定模式(解密)和密钥
////			// 创建初始化向量
////			IvParameterSpec iv = new IvParameterSpec(key.getBytes());
////			cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
////			// cipher.init(Cipher.DECRYPT_MODE, keySpec);
////			// 解密
////			output= cipher.doFinal(Base64.decode(input));
//
//			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
//			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, key.getBytes());
//			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
//			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
//			output= cipher.doFinal(Base64.decode(input));
//
//
//		} catch (Exception e) {
//			MLog.b(e.toString());
//			return "";
//		}
//		return new String(output);
//	}

	// 加密结果
	public static String encryptionResult(String data) {
		data = AESSecurity.encrypt(data, getKey());

		return data;
	}

	// 固定加密
	public static String constantEncryptionResult(String data, String key) {
		data = AESSecurity.encrypt(data, key);
		return data;

	}

	// 解密结果
	public static String decryptResult(String data) {
		try {
			data = AESSecurity.decrypt(data, getKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		MLog.a(data + "<------ keykey ------> " + getKey());
		return data;
	}

	// 固定解密
	public static String constantdecryptResult(String data, String key) {
		//处理初始化偶尔返回的垃圾信息
		try{
			if(data.contains("/>")){
				int i = data.lastIndexOf(">");
				data = data.substring(i+1);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		try {
			data = AESSecurity.decrypt(data, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		MLog.a(data + "<------ keykey ------> " + key);
		return data;
	}

	public static String getKey() {
		return MyApplication.getAppContext().getGameArgs().getKey();
	}

	public static byte[] encryptGCM(byte[] data, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, key);
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
		byte[] result = cipher.doFinal(data);
		return result;
	}

	public static byte[] decryptGCM(byte[] data, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, key);
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
		byte[] result = cipher.doFinal(data);
		return result;
	}


}
