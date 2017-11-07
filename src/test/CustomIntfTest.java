package test;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sap.conn.jco.JCoException;

import util.KHXMLUtil;
import util.SAPUtil;

public class CustomIntfTest {
	static String functionName = "RoutingTOSap";
	static String tableName = "ZPLM_ROUTING";

	public static void main(String[] args) throws Exception {
		Date date = new Date();
		testReturnTable2();
		System.out.println("================================");
		 testReturnTable();
		// testReturnTable3();
		// testXMLUtil2();
		Date date2 = new Date();
		System.out.println("用时:" + ((date2.getTime() - date.getTime()) / 1000) + "秒");
	}

	private static void testXMLUtil2() throws Exception {
		KHXMLUtil xUtil = new KHXMLUtil();
		xUtil.autoLoadXMLFunctionNode(new File("src/util/SAPIntegrationConfig4.xml").toURL(), functionName);
		Map<String, String> funcResult = xUtil.getFunctionConfigs();
		Map<String, Map<String, String>> inputParams = xUtil.getInputParam();
		Map<String, Map<String, String>> tableResult = xUtil.getTableConfigs();
		Map<String, Map<String, String>> filedResult = xUtil.getFieldConfigs();
		for (String key : funcResult.keySet()) {
			System.out.println(key + ":" + funcResult.get(key));
		}
		System.out.println("============================");
		for (String key : inputParams.keySet()) {
			System.out.println(key + ":");
			for (String value : inputParams.get(key).values()) {
				System.out.println("----" + value);
			}
		}
		System.out.println("============================");
		for (String key : tableResult.keySet()) {
			System.out.println(key + ":");
			for (String value : tableResult.get(key).values()) {
				System.out.println("----" + value);
			}
		}
		System.out.println("============================");
		for (String key : filedResult.keySet()) {
			System.out.println(key + ":");
			for (String value : filedResult.get(key).values()) {
				System.out.println("-----" + value);
			}
		}
	}

	private static void testXMLUtil() throws Exception {
		KHXMLUtil xUtil = new KHXMLUtil();
		Document document = xUtil.parse(new File("src/util/SAPIntegrationConfig4.xml").toURL());
		Map<String, String> functionConfig = xUtil.getFunctionElementConfigs(document, functionName);
		Element root = xUtil.getEleFunction();
		for (String str : functionConfig.keySet()) {
			System.out.println(str + "----" + functionConfig.get(str));
		}
		xUtil.searchFieldConfigs(root);
		Map<String, Map<String, String>> fieldConfig = xUtil.getFieldConfigs();
		for (String index : fieldConfig.keySet()) {
			System.out.println(index + "----");
			for (String filedValue : fieldConfig.get(index).values()) {
				System.out.println("\t\t" + filedValue);
			}
		}
	}

	private static void testReturnTable() throws MalformedURLException, Exception {
		Map<String, String> inputParams = new HashMap<String, String>();
		inputParams.put("MODEL", "I");
		Map<String, String> inputTable = new HashMap<String, String>();
		inputTable.put("PLM_MATNR", "110214100");
		// 固定值
		inputTable.put("PLM_MBRSH", "M");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_VTWEG", "11");
		// 固定值
		inputTable.put("PLM_groupcounter", "");
		inputTable.put("PLM_Task_desp", "锌白铜弹簧插芯组合件XHP2148-D1450/N0");
		inputTable.put("PLM_usage", "1");
		inputTable.put("PLM_status", "4");
		inputTable.put("PLM_VALID_FROM", "");
		inputTable.put("PLM_ACTIVITY", "7001");
		inputTable.put("PLM_SEQUENCE_NO", "");
		inputTable.put("PLM_work_cntr", "110841A");
		inputTable.put("PLM_control_key", "ZP04");
		inputTable.put("PLM_description1", "清洗/手工");
		inputTable.put("PLM_base_quantity", "10000");
		inputTable.put("PLM_BREAK_TIME", "");
		inputTable.put("BREAK_UNIT", "");
		inputTable.put("PLM_machine", "1000");
		inputTable.put("PLM_labor", "1200");
		inputTable.put("PLM_VGW03", "1000");
		inputTable.put("PLM_VGW04", "1000");
		inputTable.put("PLM_VGW05", "1000");
		inputTable.put("PLM_VGW06", "1000");
		inputTable.put("PLM_SPLIT", "");
		inputTable.put("PLM_SPLIT_NUM", "");
		inputTable.put("PLM_KEYWORD", "");
		inputTable.put("PLM_WORKSHOP", "");
		inputTable.put("PLM_CLASS1", "");
		inputTable.put("PLM_CLASS2", "");
		inputTable.put("PLM_CLASS3", "");
		inputTable.put("PLM_CONVERS", "");
		inputTable.put("PLM_CONVERS_U", "");
		boolean result = SAPUtil.sendDataToSAP("src/util/SAPIntegrationConfig6.xml", "RoutingTOSap", inputParams,
				inputTable);
		if (result) {
			System.out.println("数据传递成功");
		} else {
			System.out.println("数据传递失败");
		}
	}

	private static void testReturnTable3() throws MalformedURLException, Exception {

		Map<String, String> inputParams = new HashMap<String, String>();
		inputParams.put("kh3_MODEL", "U");
		Map<String, String> inputTable = new HashMap<String, String>();
		inputTable.put("item_id", "120000026");
		// 固定值
		inputTable.put("kh3_gc", "M");
		inputTable.put("kh3_base_quan", "11");
		inputTable.put("kh3_BASE_UNIT", "11");
		inputTable.put("kh3_wlms", "11");
		inputTable.put("kh3_DATUV", "11");
		inputTable.put("item_id", "11");
		inputTable.put("bl_occ_int_item_no", "11");
		inputTable.put("bl_child_id", "11");
		inputTable.put("KH3_item_categ", "11");
		inputTable.put("bl_occ_int_order_no", "11");
		inputTable.put("bl_quantity", "11");
		inputTable.put("kh3_BASE_UNIT", "11");
		boolean result = SAPUtil.sendDataToSAP("src/util/SAPIntegrationConfig7.xml", "BomTOSap", inputParams,
				inputTable);
		if (result) {
			System.out.println("数据传递成功");
		} else {
			System.out.println("数据传递失败");
		}
	}

	private static void testReturnTable2() throws MalformedURLException, Exception {
		Map<String, String> inputParams = new HashMap<String, String>();
		inputParams.put("MODEL", "U");
		Map<String, String> inputTable = new HashMap<String, String>();
		// 固定值
		inputTable.put("PLM_MATNR", "110100071");
		inputTable.put("PLM_MBRSH", "M");

		inputTable.put("PLM_MTART", "Z12");
		inputTable.put("PLM_WERKS", "1100");
		inputTable.put("PLM_VKORG", "1000");
		inputTable.put("PLM_VTWEG", "11");
		inputTable.put("PLM_MAKTE", "无铅塑铰XA3097-A62/W1H100");
		inputTable.put("PLM_MEINS", "EA");
		inputTable.put("PLM_BISMT", "190002536");
		inputTable.put("PLM_MATKL", "130");
		inputTable.put("PLM_VTWEG", "101");
		inputTable.put("PLM_SPART", "25");

		inputTable.put("PLM_NTGEW", "1");
		inputTable.put("PLM_GEWEI", "G");
		inputTable.put("PLM_UMREN", "1");
		inputTable.put("PLM_MEINH", "SET");
		inputTable.put("PLM_UMREZ", "4");
		inputTable.put("PLM_ALAND", "CN");
		inputTable.put("PLM_TATYP", "MWST");
		inputTable.put("PLM_TAXKM", "1");
		inputTable.put("PLM_KTGRM", "01");
		inputTable.put("PLM_MTPOS", "NORM");

		inputTable.put("PLM_TRAGR", "1");
		inputTable.put("PLM_LADGR", "0001");
		inputTable.put("PLM_EKGRP", "111");
		inputTable.put("PLM_MMSTA", "Z1");
		inputTable.put("PLM_DISMM", "PD");
		inputTable.put("PLM_DISPO", "106");
		inputTable.put("PLM_DISLS", "EX");
		inputTable.put("PLM_AUMNG", "0");
		inputTable.put("PLM_BESKZ", "E");
		inputTable.put("PLM_PLIFZ", "3");

		inputTable.put("PLM_WEBAZ", "3");
		inputTable.put("PLM_FHORI", "000");
		inputTable.put("PLM_LGPRO", "1201");
		inputTable.put("PLM_LGFSB", "1800");
		inputTable.put("PLM_STRGR", "40");
		inputTable.put("PLM_VRMOD", "2");
		inputTable.put("PLM_VINT1", "999");
		inputTable.put("PLM_VINT2", "999");
		inputTable.put("PLM_MTVFP", "02");
		inputTable.put("PLM_FEVOR", "206");
		inputTable.put("PLM_SFCPF", "000001");

		inputTable.put("PLM_UEETK", "5");
		inputTable.put("PLM_DZEIT", "10");
		inputTable.put("PLM_MHDRZ", "10");
		inputTable.put("PLM_BKLAS", "1000");
		inputTable.put("PLM_VPRSV", "S");
		inputTable.put("PLM_PEINH", "10000");
		inputTable.put("PLM_HKMAT", "X");
		inputTable.put("PLM_LOSGR", "10000");
		inputTable.put("PLM_AWSLS", "000001");
		inputTable.put("PLM_PRCTR", "1000");

		inputTable.put("PLM_MTPOV", "1");
		inputTable.put("PLM_BSTMI", "0");
		inputTable.put("PLM_EISBE", "0");
		inputTable.put("PLM_MTVFM", "02");
		inputTable.put("PLM_UEETO", "0");

		inputTable.put("PLM_EISLO", "0");
		inputTable.put("PLM_MAKTC", "测试短文本");
		// 固定值
		boolean result = SAPUtil.sendDataToSAP("src/util/SAPIntegrationConfig6.xml", "PartTOSap", inputParams,
				inputTable);
		if (result) {
			System.out.println("数据传递成功");
		} else {
			System.out.println("数据传递失败");
		}
	}
}
