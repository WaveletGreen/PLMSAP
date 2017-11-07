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
 * 该工具类采用大量的static对象，只能用于传输TC对象上的数据到SAP系统中，并不能在这个工具类上做任何的逻辑判断以减轻内存负担。
 * <strong style="color:red">任何逻辑判断请在外部进行</strong>
 * 
 * @author connor
 *
 */
public class SAPUtil {
	/**
	 * 缓存function名，如果调用sendDataToSAP(String functionName, Map<String, String>
	 * TCInputParams, Map<String, String> TCInputTable)，则将function缓存起来
	 */
	private static Map<String, String> Cache = new HashMap<String, String>();
	/** 只存输入参数之间的储映射关系 */
	private static Map<String, String> InputParamsToSAP = new HashMap<String, String>();
	/** 只存储输入表和TC对象的映射关系 */
	private static Map<String, String> InputTableToSAP = new HashMap<String, String>();
	/** 输入参数的映射结果 */
	private static Map<String, String> inputParamsResult = new HashMap<String, String>();
	/** 输入table的映射结果 */
	private static Map<String, String> inputTableResult = new HashMap<String, String>();
	public static JCoDestination dest;
	// 设定默认XML文档路径
	private static String filePath = "src/util/SAPIntegrationConfig4.xml";
	private static boolean isCache = false;
	private static String functionName = null;
	private static String tableName = null;

	/**
	 * 提供需要解析的XML文件路径，必须提供确定的XML文件路径
	 * 
	 * @param filePath
	 *            确定的XML文件路径
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
	 * 该class是示例代码中的，直接拿来用了
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
	 * 获取连接池连接，该连接已经打开
	 * 
	 * @return 返回创建好的连接`
	 */
	public static JCoDestination getDestination() {
		return dest;
	}

	/**
	 * 根据传入的文件路径，解析出Organize节点，获取连接SAP系统的配置
	 * <strong>每调用一次该方法，都会重新创建一个JCoDestination，</strong>
	 * 考虑到效率，创建调用该方法后，如果JCoDestination未改变， 可以
	 * <strong>调用getDestination()方法避免重复连接</strong>
	 * 
	 * @param filePath
	 *            文件的路径名，需传入确定的文件路径名
	 * @param OrganizeName
	 *            Organize节点的名称
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
		// 获取连接对象
		dest = JCoDestinationManager.getDestination(destName);
	}

	/**
	 * 根据字段对象关系做映射， 根据TC对象的属性和XML上的SAP系统的字段做映射，首先做输入参数的映射，再做输入Table上属性表和TC对象的映射
	 * 
	 * @param TCInputParams
	 *            传输到SAP的输入参数，key是映射值，value是传输值
	 * 
	 * @param TCInputTable
	 *            从TC中传入到SAP中的输入Table，key存储与SAP映射的值，value存需要传递到SAP系统的数据 ，
	 * @param table
	 *            XML中的table
	 * @param reflection
	 *            映射关键字段，一般是SapComponent
	 * @param fieldConfigs
	 *            XML中的fieldConfigs字段
	 * @param inputParams
	 *            XML中的输入参数字段
	 */
	private static void reflection(Map<String, String> TCInputParams, Map<String, String> TCInputTable,
			String functionName, String reflection, Map<String, Map<String, String>> table,
			Map<String, Map<String, String>> inputParams, Map<String, Map<String, String>> fieldConfigs) {
		InputParamsToSAP.clear();
		InputTableToSAP.clear();
		// 获取输入参数的值，输入参数仅有一个
		for (String key : TCInputParams.keySet()) {
			// 获取传入的TC值
			// String value = TCInputParams.get(key);
			for (Map<String, String> NodeFromXML : inputParams.values()) {
				for (String value_property : NodeFromXML.values()) {
					if (key.equals(value_property)) {
						InputParamsToSAP.put(key, NodeFromXML.get(reflection));
					}
				}
			}
		}
		// 获取输入表，输入表参数只能有一个
		for (String key : TCInputTable.keySet()) {
			// 获取传入的TC值
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
	 * 获取TC对象和SAP系统的输入参数的映射关系，key为TC对象上的属性，value是SAP系统上输入参数的输入参数名
	 * 
	 * @return
	 */
	public static Map<String, String> getInputParamsToSAP() {
		return InputParamsToSAP;
	}

	/**
	 * 获取TC对象和SAP系统的输入table的映射关系，key为TC对象上的属性，value是SAP系统上输入table的输入参数名
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
	 * 提供需要执行的SAP函数名，根据这个函数名自动获取映射关系并传输数据到SAP系统中。
	 * <strong>映射关系是有要求的，必须提供必输的字段才能传到SAP系统中</strong>
	 * 
	 * @param RelFunctionName
	 *            RFC函数名
	 * @param TCInputParams
	 *            提供的输入参数，目前是输入参数名为MODEL，值为I/U(插入新数据/更新已有数据)，该值根据TC中的字段获取并传入
	 * @param TCInputTable
	 *            提供的参数列表，需要与SAP中的字段做映射，key为TC中的字段名，value从TC对象中获取。
	 * 
	 * @return 根据SAP系统返回的RETURN表，获取TYPE字段做判断如果是"S"则返回true，返回"E"则返回false
	 * @throws MalformedURLException
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static boolean sendDataToSAP(String filePathx, String RelFunctionName, Map<String, String> TCInputParams,
			Map<String, String> TCInputTable) throws MalformedURLException, Exception {

		if (Cache.containsKey(RelFunctionName) && Cache.containsKey(filePathx)) {
			// 调用映射关系并赋值
			for (String key : TCInputParams.keySet()) {
				inputParamsResult.put(InputParamsToSAP.get(key), TCInputParams.get(key));
			}
			for (String key : TCInputTable.keySet()) {
				inputTableResult.put(InputTableToSAP.get(key), TCInputTable.get(key));
			}
		} else {
			// 没有缓存，先找映射关系再赋值
			isCache = false;
			filePath = filePathx;
			inputTableResult.clear();
			inputParamsResult.clear();
			/** 工具类的一般写法----Start */
			KHXMLUtil xutil = new KHXMLUtil();
			xutil.autoLoadXMLFunctionNode(new File(filePath).toURL(), RelFunctionName);
			Map<String, String> function = xutil.getFunctionConfigs();
			// 获取映射字段
			String reflection = function.get("ReflectionKey");
			functionName = function.get("FunctionName");
			tableName = function.get("ReturenTable");
			Map<String, Map<String, String>> inputParams = xutil.getInputParam();
			Map<String, Map<String, String>> table = xutil.getTableConfigs();
			Map<String, Map<String, String>> fieldConfigs = xutil.getFieldConfigs();
			/** 工具类的一般写法----End */
			reflection(TCInputParams, TCInputTable, RelFunctionName, reflection, table, inputParams, fieldConfigs);
			// 缓存起来，根据这两个值作为判断映射是否缓存的开关
			Cache.put(RelFunctionName, RelFunctionName);
			// 缓存起来
			Cache.put(filePathx, filePathx);

			return sendDataToSAP(filePath, RelFunctionName, TCInputParams, TCInputTable);

		}

		JCoFunction sapFunction = dest.getRepository().getFunction(functionName);
		if (null == sapFunction) {
			throw new RuntimeException("XML文档中没有找到接口函数，或Function节点没有FunctionName这一属性"
					+ "，请检查接口函数名和Destination是否正确。或者在调用该方法之前请调用getCustomDestination(String"
					+ "fileName, String OrganizeName)方法重新确定Destination");
		}
		// 设定本次是状态连接
		JCoContext.begin(dest);
		// 根据从TC里传进来的输入参数，传到SAP的输入参数中，目前是传到SAP的输入MODEL中
		for (String key : inputParamsResult.keySet()) {
			sapFunction.getImportParameterList().setValue(key, inputParamsResult.get(key));
		}
		JCoTable importTable = sapFunction.getTableParameterList().getTable(tableName);
		// 测试用
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
