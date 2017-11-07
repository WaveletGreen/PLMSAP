package util;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

/**
 * �ù�������ô�����static����ֻ�����ڴ���TC�����ϵ����ݵ�SAPϵͳ�У�����������������������κε��߼��ж��Լ����ڴ渺����
 * <strong style="color:red">�κ��߼��ж������ⲿ����</strong>
 * 
 * @author connor
 *
 */
public class SAPUtil {
	/**
	 * ����function�����������sendDataToSAP(String functionName, Map<String, String>
	 * TCInputParams, Map<String, String> TCInputTable)����function��������
	 */
	private static Map<String, String> Cache = new HashMap<String, String>();
	/** ֻ���������֮��Ĵ�ӳ���ϵ */
	private static Map<String, String> InputParamsToSAP = new HashMap<String, String>();
	/** ֻ�洢������TC�����ӳ���ϵ */
	private static Map<String, String> InputTableToSAP = new HashMap<String, String>();
	/** ���������ӳ���� */
	private static Map<String, String> inputParamsResult = new HashMap<String, String>();
	/** ����table��ӳ���� */
	private static Map<String, String> inputTableResult = new HashMap<String, String>();
	public static JCoDestination dest;
	// �趨Ĭ��XML�ĵ�·��
	private static String filePath = "src/util/SAPIntegrationConfig4.xml";
	private static boolean isCache = false;
	private static String functionName = null;
	private static String tableName = null;

	/**
	 * �ṩ��Ҫ������XML�ļ�·���������ṩȷ����XML�ļ�·��
	 * 
	 * @param filePath
	 *            ȷ����XML�ļ�·��
	 * @return
	 */
	public static SAPUtil getSAPUtilInstance(String filePathx) {
		filePath = filePathx;
		return new SAPUtil();
	}

	static {
		MyDestinationDataProvider myProvider = new MyDestinationDataProvider();
		// catch IllegalStateException if an instance is already registered
		// register the provider with the JCo environment;
		try {
			com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(myProvider);
		} catch (IllegalStateException providerAlreadyRegisteredException) {
			throw new Error(providerAlreadyRegisteredException);
		}
		String destName = "DEFUALT_DEST";
		myProvider.changeProperties(destName, ConnectPooled.getDefaultProperties());
		try {
			dest = JCoDestinationManager.getDestination(destName);
		} catch (JCoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��class��ʾ�������еģ�ֱ����������
	 * 
	 * @author connor
	 *
	 */
	private static class MyDestinationDataProvider implements DestinationDataProvider {
		private DestinationDataEventListener eL;
		private HashMap<String, Properties> secureDBStorage = new HashMap<String, Properties>();

		public Properties getDestinationProperties(String destinationName) {
			try {
				Properties p = secureDBStorage.get(destinationName);
				if (p != null) {
					if (p.isEmpty())
						throw new DataProviderException(DataProviderException.Reason.INVALID_CONFIGURATION,
								"destination configuration is incorrect", null);
					return p;
				}
				return null;
			} catch (RuntimeException re) {
				throw new DataProviderException(DataProviderException.Reason.INTERNAL_ERROR, re);
			}
		}

		// An implementation supporting events has to retain the eventListener
		// instance provided
		// by the JCo runtime. This listener instance shall be used to notify
		// the JCo runtime
		// about all changes in destination configurations.
		public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
			this.eL = eventListener;
		}

		public boolean supportsEvents() {
			return true;
		}

		// implementation that saves the properties in a very secure way
		void changeProperties(String destName, Properties properties) {
			synchronized (secureDBStorage) {
				if (properties == null) {
					if (secureDBStorage.remove(destName) != null)
						eL.deleted(destName);
				} else {
					secureDBStorage.put(destName, properties);
					eL.updated(destName); // create or updated
				}
			}
		}
	}

	/**
	 * ��ȡ���ӳ����ӣ��������Ѿ���
	 * 
	 * @return ���ش����õ�����`
	 */
	public static JCoDestination getDestination() {
		return dest;
	}

	/**
	 * ���ݴ�����ļ�·����������Organize�ڵ㣬��ȡ����SAPϵͳ������
	 * <strong>ÿ����һ�θ÷������������´���һ��JCoDestination��</strong>
	 * ���ǵ�Ч�ʣ��������ø÷��������JCoDestinationδ�ı䣬 ����
	 * <strong>����getDestination()���������ظ�����</strong>
	 * 
	 * @param filePath
	 *            �ļ���·�������贫��ȷ�����ļ�·����
	 * @param OrganizeName
	 *            Organize�ڵ������
	 * @throws JCoException
	 */
	public static void getCustomDestination(String filePath, String OrganizeName) throws JCoException {
		MyDestinationDataProvider myProvider = new MyDestinationDataProvider();
		try {
			com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(myProvider);
		} catch (IllegalStateException providerAlreadyRegisteredException) {
			throw new Error(providerAlreadyRegisteredException);
		}
		String destName = "ABAP_AS_" + OrganizeName;
		myProvider.changeProperties(destName, ConnectPooled.changeProperties(filePath, OrganizeName));
		// ��ȡ���Ӷ���
		dest = JCoDestinationManager.getDestination(destName);
	}

	/**
	 * �����ֶζ����ϵ��ӳ�䣬 ����TC��������Ժ�XML�ϵ�SAPϵͳ���ֶ���ӳ�䣬���������������ӳ�䣬��������Table�����Ա��TC�����ӳ��
	 * 
	 * @param TCInputParams
	 *            ���䵽SAP�����������key��ӳ��ֵ��value�Ǵ���ֵ
	 * 
	 * @param TCInputTable
	 *            ��TC�д��뵽SAP�е�����Table��key�洢��SAPӳ���ֵ��value����Ҫ���ݵ�SAPϵͳ������ ��
	 * @param table
	 *            XML�е�table
	 * @param reflection
	 *            ӳ��ؼ��ֶΣ�һ����SapComponent
	 * @param fieldConfigs
	 *            XML�е�fieldConfigs�ֶ�
	 * @param inputParams
	 *            XML�е���������ֶ�
	 */
	private static void reflection(Map<String, String> TCInputParams, Map<String, String> TCInputTable,
			String functionName, String reflection, Map<String, Map<String, String>> table,
			Map<String, Map<String, String>> inputParams, Map<String, Map<String, String>> fieldConfigs) {
		InputParamsToSAP.clear();
		InputTableToSAP.clear();
		// ��ȡ���������ֵ�������������һ��
		for (String key : TCInputParams.keySet()) {
			// ��ȡ�����TCֵ
			// String value = TCInputParams.get(key);
			for (Map<String, String> NodeFromXML : inputParams.values()) {
				for (String value_property : NodeFromXML.values()) {
					if (key.equals(value_property)) {
						InputParamsToSAP.put(key, NodeFromXML.get(reflection));
					}
				}
			}
		}
		// ��ȡ�������������ֻ����һ��
		for (String key : TCInputTable.keySet()) {
			// ��ȡ�����TCֵ
			// String value = TCInputTable.get(key);
			for (Map<String, String> NodeFromXML : fieldConfigs.values()) {
				for (String value_property : NodeFromXML.values()) {
					if (key.equalsIgnoreCase(value_property)) {
						InputTableToSAP.put(key, NodeFromXML.get(reflection).toUpperCase());
					}
				}
			}
		}
		return;
	}

	/**
	 * ��ȡTC�����SAPϵͳ�����������ӳ���ϵ��keyΪTC�����ϵ����ԣ�value��SAPϵͳ��������������������
	 * 
	 * @return
	 */
	public static Map<String, String> getInputParamsToSAP() {
		return InputParamsToSAP;
	}

	/**
	 * ��ȡTC�����SAPϵͳ������table��ӳ���ϵ��keyΪTC�����ϵ����ԣ�value��SAPϵͳ������table�����������
	 * 
	 * @return
	 */
	public static Map<String, String> getInputTableToSAP() {
		return InputTableToSAP;
	}

	public static boolean sendDataToSAP(String functionName, Map<String, String> TCInputParams,
			Map<String, String> TCInputTable) throws MalformedURLException, Exception {
		return sendDataToSAP(filePath, functionName, TCInputParams, TCInputTable);

	}

	/**
	 * �ṩ��Ҫִ�е�SAP����������������������Զ���ȡӳ���ϵ���������ݵ�SAPϵͳ�С�
	 * <strong>ӳ���ϵ����Ҫ��ģ������ṩ������ֶβ��ܴ���SAPϵͳ��</strong>
	 * 
	 * @param RelFunctionName
	 *            RFC������
	 * @param TCInputParams
	 *            �ṩ�����������Ŀǰ�����������ΪMODEL��ֵΪI/U(����������/������������)����ֵ����TC�е��ֶλ�ȡ������
	 * @param TCInputTable
	 *            �ṩ�Ĳ����б���Ҫ��SAP�е��ֶ���ӳ�䣬keyΪTC�е��ֶ�����value��TC�����л�ȡ��
	 * 
	 * @return ����SAPϵͳ���ص�RETURN����ȡTYPE�ֶ����ж������"S"�򷵻�true������"E"�򷵻�false
	 * @throws MalformedURLException
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static boolean sendDataToSAP(String filePathx, String RelFunctionName, Map<String, String> TCInputParams,
			Map<String, String> TCInputTable) throws MalformedURLException, Exception {

		if (Cache.containsKey(RelFunctionName) && Cache.containsKey(filePathx)) {
			// ����ӳ���ϵ����ֵ
			for (String key : TCInputParams.keySet()) {
				inputParamsResult.put(InputParamsToSAP.get(key), TCInputParams.get(key));
			}
			for (String key : TCInputTable.keySet()) {
				inputTableResult.put(InputTableToSAP.get(key), TCInputTable.get(key));
			}
		} else {
			// û�л��棬����ӳ���ϵ�ٸ�ֵ
			isCache = false;
			filePath = filePathx;
			inputTableResult.clear();
			inputParamsResult.clear();
			/** �������һ��д��----Start */
			KHXMLUtil xutil = new KHXMLUtil();
			xutil.autoLoadXMLFunctionNode(new File(filePath).toURL(), RelFunctionName);
			Map<String, String> function = xutil.getFunctionConfigs();
			// ��ȡӳ���ֶ�
			String reflection = function.get("ReflectionKey");
			functionName = function.get("FunctionName");
			tableName = function.get("ReturenTable");
			Map<String, Map<String, String>> inputParams = xutil.getInputParam();
			Map<String, Map<String, String>> table = xutil.getTableConfigs();
			Map<String, Map<String, String>> fieldConfigs = xutil.getFieldConfigs();
			/** �������һ��д��----End */
			reflection(TCInputParams, TCInputTable, RelFunctionName, reflection, table, inputParams, fieldConfigs);
			// ��������������������ֵ��Ϊ�ж�ӳ���Ƿ񻺴�Ŀ���
			Cache.put(RelFunctionName, RelFunctionName);
			// ��������
			Cache.put(filePathx, filePathx);

			return sendDataToSAP(filePath, RelFunctionName, TCInputParams, TCInputTable);

		}

		JCoFunction sapFunction = dest.getRepository().getFunction(functionName);
		if (null == sapFunction) {
			throw new RuntimeException("XML�ĵ���û���ҵ��ӿں�������Function�ڵ�û��FunctionName��һ����"
					+ "������ӿں�������Destination�Ƿ���ȷ�������ڵ��ø÷���֮ǰ�����getCustomDestination(String"
					+ "fileName, String OrganizeName)��������ȷ��Destination");
		}
		// �趨������״̬����
		JCoContext.begin(dest);
		// ���ݴ�TC�ﴫ�������������������SAP����������У�Ŀǰ�Ǵ���SAP������MODEL��
		for (String key : inputParamsResult.keySet()) {
			sapFunction.getImportParameterList().setValue(key, inputParamsResult.get(key));
		}
		JCoTable importTable = sapFunction.getTableParameterList().getTable(tableName);
		// ������
		System.out.println(importTable.getNumColumns());
		importTable.appendRow();
		importTable.lastRow();
		for (String key : inputTableResult.keySet()) {
			if (key == null)
				continue;
			importTable.setValue(key, inputTableResult.get(key));
		}

		System.out.println(importTable.getValue("matnr".toUpperCase()));
		System.out.println(importTable);
		sapFunction.execute(dest);
		JCoTable exportTable = sapFunction.getTableParameterList().getTable("RETURN");
		System.out.println("MESSAGE_V1:"+exportTable.getString("MESSAGE_V1") + "\t");
		System.out.println("MESSAGE_V2:"+exportTable.getString("MESSAGE_V2") + "\t");
		System.out.println("MESSAGE_V3:"+exportTable.getString("MESSAGE_V3") + "\t");
		System.out.println("MESSAGE_V4:"+exportTable.getString("MESSAGE_V4") + "\t");
		System.out.println("MESSAGE:"+exportTable.getString("MESSAGE") + "\t");
		System.out.println("ID:"+exportTable.getString("ID") + "\t");
		System.out.println("NUMBER:"+exportTable.getString("NUMBER") + "\t");
		System.out.println("LOG_NO:"+exportTable.getString("LOG_NO") + "\t");
		System.out.println("LOG_MSG_NO:"+exportTable.getString("LOG_MSG_NO") + "\t");
		System.out.println("PARAMETER:"+exportTable.getString("PARAMETER") + "\t");
		System.out.println("ROW:"+exportTable.getString("ROW") + "\t");
		System.out.println("FIELD:"+exportTable.getString("FIELD") + "\t");
		System.out.println("SYSTEM:"+exportTable.getString("SYSTEM") + "\t");
		String result = exportTable.getString("TYPE");
		System.out.println(result);
		JCoContext.end(dest);
		switch (result) {
		case "S":
			return true;
		case "E":
			return false;
		default:
			break;
		}
		return false;

	}
}
