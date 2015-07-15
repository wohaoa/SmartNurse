package com.magicare.smartnurse.ble;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.magicare.smartnurse.bean.UserBean;

public class FatScaleDataUtil {
	@SuppressWarnings("unused")
	private static DateFormat mDateCompareFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

	public static byte[] copyOfRange(byte[] original, int from, int to) {
		int newLength = to - from;
		if (newLength < 0)
			throw new IllegalArgumentException(from + " > " + to);
		byte[] copy = new byte[newLength];
		System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
		return copy;
	}

	/**
	 * 求byte字节的最高位
	 * 
	 * @param b
	 * @return
	 */
	public static int getByteHightstBit(byte b) {
		int a = b & 0x80;
		String str = Integer.toBinaryString(a);
		return Integer.parseInt("" + str.charAt(0));
	}

	/**
	 * 求字节b的0~6位bit之和
	 * 
	 * @param b
	 * @return
	 */
	public static int getByteLower6Bit(byte b) {
		int a = b & 0x7F;
		return a;
	}

	public static String byteToHexString(byte b) {
		int i = b & 0xFF;
		String hi = Integer.toHexString(i);
		return hi;
	}

	/**
	 * 求高低字节之和
	 * 
	 * @param bhigh
	 * @param blow
	 * @return
	 */
	public static int getHightAndLowSum(byte bhigh, byte blow) {
		// int result = Integer.parseInt(byteToHexString(bhigh)
		// + byteToHexString(blow), 16);
		int blowed = Integer.parseInt(byteToHexString(blow), 16);
		// System.out.println("#######blowed:" + blowed);
		int result;
		if (blowed < 16) {
			result = Integer.parseInt(byteToHexString(bhigh) + "0" + byteToHexString(blow), 16);
		} else {
			result = Integer.parseInt(byteToHexString(bhigh) + byteToHexString(blow), 16);
		}
		return result;
	}

	public static int getByteHight(byte b) {
		int a = b & 0xF0;
		return a;
	}

	public static int getByteLower(byte b) {
		int a = b & 0x0F;
		return a;
	}

	public static String byteToHexString(byte[] b) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < b.length; i++) {
			sb.append(byteToHexString(b[i]));
		}
		return sb.toString();
	}

	public static String byteToHexStringFormat(byte[] b) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < b.length; i++) {
			String s = byteToHexString(b[i]);
			if (s.length() == 1) {
				s = "0" + s;
			}
			sb.append(s);
			sb.append(" ");
		}
		return sb.toString();
	}

	public static byte[] hexStringToByte(String hexString) {
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 只取低位
	 * 
	 * @param hexString
	 * @return
	 */
	public static String getHexStringSum(String hexString) {
		String hexStringSum = "";
		long hexIntSum = 0;
		for (int i = 0; i < hexString.length() / 2; i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(hexString.substring(2 * i, 2 * i + 1));
			sb.append(hexString.substring(2 * i + 1, 2 * i + 2));
			BigInteger bi2 = new BigInteger(sb.toString(), 16);
			hexIntSum += bi2.longValue();
		}

		hexStringSum = Long.toHexString(hexIntSum);
		int a = Integer.parseInt(hexStringSum, 16);
		int b = (a & 0xFF);
		hexStringSum = Integer.toHexString(b);
		StringBuilder sb = new StringBuilder();
		sb.append(hexString);
		sb.append(hexStringSum);
		// ToastUtil.showToast("The HexStringSum is:" + sb.toString());
		return sb.toString();
	}

	public static String getHexStringCheckSum(String hexString) {
		String hexStringSum = "";
		long hexIntSum = 0;
		for (int i = 0; i < hexString.length() / 2; i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(hexString.substring(2 * i, 2 * i + 1));
			sb.append(hexString.substring(2 * i + 1, 2 * i + 2));
			BigInteger bi2 = new BigInteger(sb.toString(), 16);
			hexIntSum += bi2.longValue();
		}
		hexStringSum = Long.toHexString(hexIntSum);
		// 取低8位
		int a = Integer.parseInt(hexStringSum, 16);
		int b = (a & 0xFF);
		return Integer.toHexString(b);
	}

	public static byte[] getInverseByte(int start, int end, byte[] payload) {
		int length = end - start;
		byte[] b = new byte[length];

		for (int i = 0; i < length; i++) {
			int j = end - i - 1;
			if (j >= 0) {
				b[i] = payload[j];
			}
		}
		return b;
	}

	/*************************************** 数据发送部分 *****************************************/
	/**
	 * 
	 * Function:需要发送的数据
	 * 
	 * @param bean
	 * @return
	 * 
	 */
	public static byte[] sendData(UserBean bean) {
		byte[] datas = new byte[18];
		// 起始位
		datas[0] = 0X68;
		datas[1] = 0X05;
		datas[2] = 0X0E;
//		// 组号
//		datas[3] = (byte) (bean.getGroupId() & 0XFF);
//		// 性别
//		datas[4] = (byte) (bean.getSex() & 0XFF);
//		// 运动员级别
//		datas[5] = (byte) (bean.getMotionLevel() & 0XFF);
//		// 身高
//		datas[6] = (byte) (bean.getHeight() & 0XFF);
//		// 年龄
//		datas[7] = (byte) (bean.getAge() & 0XFF);
//		// 单位
//		datas[8] = (byte) (bean.getUnit() & 0XFF);
		// 保留位
		datas[9] = 0X00;
		datas[10] = 0X00;
		datas[11] = 0X00;
		datas[12] = 0X00;
		datas[13] = 0X00;
		datas[14] = 0X00;
		datas[15] = 0X00;
		datas[16] = 0X00;
		// 校验位
//		int chenksum = 0X68 + 0X05 + 0X0E + bean.getCheckSum();
//		datas[17] = (byte) (chenksum & 0xFF);
		return datas;
	}

	// public static void writeDataToFile(int filePathType, String data) {
	// try {
	// if (StorageUtil.isSDCardExist()) {
	// // make sure file created
	// final File file = new File(
	// StorageUtil.getDirByType(filePathType),
	// StorageUtil.getFileName(filePathType));
	// if (!file.exists()) {
	// file.getParentFile().mkdirs();
	// file.createNewFile();
	// }
	//
	// FileWriter fw = null;
	// try {
	// fw = new FileWriter(file, true);
	// fw.write(data);
	// fw.write("\n");
	// fw.flush();
	// } catch (IOException e) {
	// Log.e("test", "write file log fail1", e);
	// } finally {
	// if (fw != null) {
	// try {
	// fw.close();
	// } catch (IOException e) {
	// Log.e("test", "write file log fail2", e);
	// }
	// }
	// }
	// }
	//
	// } catch (Exception e) {
	// Log.e("test", "write file log fail3", e);
	// }
	// }
	//
	// public static void writeDebugInfoToFile(int filePathType, String data) {
	// try {
	// if (StorageUtil.isSDCardExist()) {
	// // make sure file created
	// final File file = new File(
	// StorageUtil.getDirByType(filePathType),
	// StorageUtil.getFileName(filePathType));
	// if (!file.exists()) {
	// file.getParentFile().mkdirs();
	// file.createNewFile();
	// }
	//
	// FileWriter fw = null;
	// try {
	// fw = new FileWriter(file, true);
	// fw.write(mDateCompareFormat.format(System
	// .currentTimeMillis()) + "===");
	// fw.write(data);
	// fw.write("\n");
	// fw.flush();
	// } catch (IOException e) {
	// Log.e("test", "write file log fail1", e);
	// } finally {
	// if (fw != null) {
	// try {
	// fw.close();
	// } catch (IOException e) {
	// Log.e("test", "write file log fail2", e);
	// }
	// }
	// }
	// }
	//
	// } catch (Exception e) {
	// Log.e("test", "write file log fail3", e);
	// }
	// }

}